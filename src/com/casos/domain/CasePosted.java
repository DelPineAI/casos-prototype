package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CasePosted extends DomainEvent {

    private final CaseId caseId;
    private final County county;

    public CasePosted(Instant occurredAt, CaseId caseId, AgencyId agencyId, County county) {
        super(occurredAt, Optional.of(agencyId));
        this.caseId = caseId;
        this.county = county;
    }

    @Override
    public String type() {
        return "CasePosted";
    }

    public CaseId caseId() {
        return caseId;
    }

    public County county() {
        return county;
    }
}
