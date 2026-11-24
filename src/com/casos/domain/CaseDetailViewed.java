package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CaseDetailViewed extends DomainEvent {

    private final String sessionId;
    private final CaseId caseId;

    public CaseDetailViewed(Instant occurredAt, String sessionId, CaseId caseId) {
        super(occurredAt, Optional.empty());
        this.sessionId = sessionId;
        this.caseId = caseId;
    }

    @Override
    public String type() {
        return "CaseDetailViewed";
    }

    public String sessionId() {
        return sessionId;
    }

    public CaseId caseId() {
        return caseId;
    }
}
