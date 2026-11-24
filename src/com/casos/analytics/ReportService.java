package com.casos.analytics;

import com.casos.domain.AgencyId;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import java.time.Clock;

/** Agency-scoped report generation and CSV export (U15.0, S15.4). */
public class ReportService {

    private final EventStore store;
    private final AuthorizationPolicy authz;
    private final Clock clock;

    public ReportService(EventStore store, AuthorizationPolicy authz, Clock clock) {
        this.store = store;
        this.authz = authz;
        this.clock = clock;
    }

    public CasePerformanceReport casePerformance(AgencyId a, Session actor, DateRange r) {
        return null;
    }

    public InterestFunnelReport interestFunnel(AgencyId a, Session actor, DateRange r) {
        return null;
    }

    public BoardInventoryReport boardInventory(Session actor) {
        return null;
    }

    public RosterPipelineReport rosterPipeline(AgencyId a, Session actor, DateRange r) {
        return null;
    }

    /** Polymorphic: any Report exports through the same call. */
    public String exportCsv(Report r) {
        return r.toCsv();
    }
}
