package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.Optional;

/** RPT-05: applications received, accepted and declined. */
public class RosterPipelineReport extends Report {

    private final int applied;
    private final int accepted;
    private final int declined;

    public RosterPipelineReport(Instant generatedAt, Optional<AgencyId> scope, DateRange range,
                                int applied, int accepted, int declined) {
        super(generatedAt, scope, range);
        this.applied = applied;
        this.accepted = accepted;
        this.declined = declined;
    }

    @Override
    public String title() {
        return "Roster Pipeline";
    }

    @Override
    public String toCsv() {
        return "";
    }
}
