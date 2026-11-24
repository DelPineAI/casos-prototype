package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CaseFilled extends DomainEvent {

    private final CaseId caseId;

    public CaseFilled(Instant occurredAt, CaseId caseId, AgencyId agencyId) {
        super(occurredAt, Optional.of(agencyId));
        this.caseId = caseId;
    }

    @Override
    public String type() {
        return "CaseFilled";
    }

    public CaseId caseId() {
        return caseId;
    }
}
