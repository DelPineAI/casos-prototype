package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CaseRenewed extends DomainEvent {

    private final CaseId caseId;
    private final Instant newExpiresAt;

    public CaseRenewed(Instant occurredAt, CaseId caseId, AgencyId agencyId, Instant newExpiresAt) {
        super(occurredAt, Optional.of(agencyId));
        this.caseId = caseId;
        this.newExpiresAt = newExpiresAt;
    }

    @Override
    public String type() {
        return "CaseRenewed";
    }

    public CaseId caseId() {
        return caseId;
    }

    public Instant newExpiresAt() {
        return newExpiresAt;
    }
}
