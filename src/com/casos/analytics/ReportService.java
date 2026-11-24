package com.casos.analytics;

import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.CaseRepository;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import com.casos.identity.UnauthorizedException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Agency-scoped report generation and CSV export (U15.0, S15.4). */
public class ReportService {

    private final EventStore store;
    private final AuthorizationPolicy authz;
    private final CaseRepository cases;
    private final Clock clock;

    public ReportService(EventStore store, AuthorizationPolicy authz, CaseRepository cases, Clock clock) {
        this.store = store;
        this.authz = authz;
        this.cases = cases;
        this.clock = clock;
    }

    /** RPT-01. */
    public CasePerformanceReport casePerformance(AgencyId a, Session actor, DateRange r) {
        require(a, actor);
        List<CasePerformanceRow> rows = new ArrayList<>();
        for (Case c : cases.forAgency(a)) {
            int views = count(a, r, "CaseDetailViewed", c);
            int interests = count(a, r, "InterestSubmitted", c);
            rows.add(new CasePerformanceRow(c.caseId(), c.caseNumber(), views, views, interests, c.status()));
        }
        return new CasePerformanceReport(clock.instant(), Optional.of(a), r, rows);
    }

    /** RPT-02. Board views are anonymous and site-wide; detail views and interests are this agency's. */
    public InterestFunnelReport interestFunnel(AgencyId a, Session actor, DateRange r) {
        require(a, actor);
        int boardViews = store.query(Optional.empty(), r.from(), r.to(), Optional.of("BoardViewed")).size();
        int details = store.query(Optional.of(a), r.from(), r.to(), Optional.of("CaseDetailViewed")).size();
        int interests = store.query(Optional.of(a), r.from(), r.to(), Optional.of("InterestSubmitted")).size();
        return new InterestFunnelReport(clock.instant(), Optional.of(a), r, boardViews, details, interests);
    }

    /** RPT-03. Specified in D2; not part of the prototype journey. */
    public BoardInventoryReport boardInventory(Session actor) {
        throw new UnsupportedOperationException("RPT-03 is specified but not in the prototype.");
    }

    /** RPT-05. Roster pipeline is Phase 4. */
    public RosterPipelineReport rosterPipeline(AgencyId a, Session actor, DateRange r) {
        throw new UnsupportedOperationException("RPT-05 belongs to the Phase 4 roster pipeline.");
    }

    /** Polymorphic: any Report exports through the same call. */
    public String exportCsv(Report r) {
        return r.toCsv();
    }

    private int count(AgencyId a, DateRange r, String type, Case c) {
        return (int) store.query(Optional.of(a), r.from(), r.to(), Optional.of(type)).stream()
                .filter(e -> e.caseId().map(c.caseId()::equals).orElse(false))
                .count();
    }

    private void require(AgencyId a, Session actor) {
        if (!authz.canManageCases(actor, a)) {
            throw new UnauthorizedException("Reports are visible only to the agency's own members.");
        }
    }
}
