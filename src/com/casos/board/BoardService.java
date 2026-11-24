package com.casos.board;

import com.casos.domain.AvailabilityQuery;
import com.casos.domain.BoardViewed;
import com.casos.domain.Case;
import com.casos.domain.CaseDetailViewed;
import com.casos.domain.CaseId;
import com.casos.domain.CaseQuery;
import com.casos.domain.ClinicianId;
import com.casos.domain.DomainEventBus;
import com.casos.domain.FilterCriteria;
import com.casos.domain.GeoPoint;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Read-only board queries (U1.0, U2.0, U3.0). No account is ever required to call these. */
public class BoardService {

    private final CaseQuery cases;
    private final AvailabilityQuery availability;
    private final DomainEventBus bus;
    private final Clock clock;
    private final OverlapRanker ranker = new OverlapRanker();
    private final CaseSorter sorter = new CaseSorter();

    public BoardService(CaseQuery cases, AvailabilityQuery availability, DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.availability = availability;
        this.bus = bus;
        this.clock = clock;
    }

    /** SD-1. Publishes BoardViewed. */
    public BoardPage openCases(FilterCriteria f, Optional<GeoPoint> viewer,
                               Optional<ClinicianId> viewerId, String sessionId) {
        Instant now = clock.instant();
        CaseFilter filter = new CaseFilter(f, Set.of());
        List<CaseCard> cards = new ArrayList<>();
        for (Case c : cases.findOpen(now)) {
            Optional<Double> distance = distanceTo(c, viewer);
            if (filter.matches(c, distance)) {
                cards.add(new CaseCard(c, distance, overlapFor(c, viewerId)));
            }
        }
        List<CaseCard> sorted = sorter.sort(cards, viewer.isPresent());
        bus.publish(new BoardViewed(now, sessionId, f));
        return new BoardPage(sorted, f);
    }

    /** Detail drawer. Empty when the case is filled, expired or unknown (S1.5). Publishes CaseDetailViewed. */
    public Optional<CaseDetail> detail(CaseId id, Optional<GeoPoint> viewer,
                                       Optional<ClinicianId> viewerId, String sessionId) {
        Instant now = clock.instant();
        Optional<Case> found = cases.findById(id).filter(c -> c.isVisibleAt(now));
        found.ifPresent(c -> bus.publish(new CaseDetailViewed(now, sessionId, id)));
        return found.map(c -> new CaseDetail(c, distanceTo(c, viewer), overlapFor(c, viewerId)));
    }

    private Optional<Double> distanceTo(Case c, Optional<GeoPoint> viewer) {
        return viewer.map(c::distanceMilesFrom);
    }

    private Optional<Integer> overlapFor(Case c, Optional<ClinicianId> viewerId) {
        return viewerId.flatMap(availability::availabilityOf).map(s -> ranker.score(c, s));
    }
}
