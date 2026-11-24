package com.casos.domain;

import java.time.Instant;

/** A roster the clinician says they already belong to (S6.1 to S6.4). */
public class SelfReportedRoster {

    private final AgencyId agencyId;
    private final InsurancePlan insurancePlan;
    private final Instant recordedAt;

    public SelfReportedRoster(AgencyId agencyId, InsurancePlan insurancePlan, Instant recordedAt) {
        this.agencyId = agencyId;
        this.insurancePlan = insurancePlan;
        this.recordedAt = recordedAt;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public InsurancePlan insurancePlan() {
        return insurancePlan;
    }

    public Instant recordedAt() {
        return recordedAt;
    }
}
