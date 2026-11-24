package com.casos.board;

import com.casos.domain.AvailabilityQuery;
import com.casos.domain.CaseId;
import com.casos.domain.CaseQuery;
import com.casos.domain.ClinicianId;
import com.casos.domain.DomainEventBus;
import com.casos.domain.FilterCriteria;
import com.casos.domain.GeoPoint;
import java.time.Clock;
import java.util.Optional;

/** Read-only board queries (U1.0, U2.0, U3.0). Writes moved to InterestService in Round 4 (W6). */
public class BoardService {

    private final CaseQuery cases;
    private final AvailabilityQuery availability;
    private final DomainEventBus bus;
    private final Clock clock;

    public BoardService(CaseQuery cases, AvailabilityQuery availability, DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.availability = availability;
        this.bus = bus;
        this.clock = clock;
    }

    /** Publishes BoardViewed. */
    public BoardPage openCases(FilterCriteria f, Optional<GeoPoint> viewer,
                               Optional<ClinicianId> viewerId, String sessionId) {
        return null;
    }

    /** Publishes CaseDetailViewed. */
    public Optional<CaseDetail> detail(CaseId id, Optional<GeoPoint> viewer,
                                       Optional<ClinicianId> viewerId, String sessionId) {
        return Optional.empty();
    }
}
