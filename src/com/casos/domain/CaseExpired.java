package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CaseExpired extends DomainEvent {

    private final CaseId caseId;

    public CaseExpired(Instant occurredAt, CaseId caseId, AgencyId agencyId) {
        super(occurredAt, Optional.of(agencyId));
        this.caseId = caseId;
    }

    @Override
    public String type() {
        return "CaseExpired";
    }

    public CaseId caseId() {
        return caseId;
    }
}
