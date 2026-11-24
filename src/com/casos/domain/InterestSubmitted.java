package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class InterestSubmitted extends DomainEvent {

    private final InterestId interestId;
    private final CaseId caseId;

    public InterestSubmitted(Instant occurredAt, InterestId interestId, CaseId caseId, AgencyId agencyId) {
        super(occurredAt, Optional.of(agencyId));
        this.interestId = interestId;
        this.caseId = caseId;
    }

    @Override
    public String type() {
        return "InterestSubmitted";
    }

    public InterestId interestId() {
        return interestId;
    }

    public CaseId caseId() {
        return caseId;
    }
}
