package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class BoardViewed extends DomainEvent {

    private final String sessionId;
    private final FilterCriteria criteria;

    public BoardViewed(Instant occurredAt, String sessionId, FilterCriteria criteria) {
        super(occurredAt, Optional.empty());
        this.sessionId = sessionId;
        this.criteria = criteria;
    }

    @Override
    public String type() {
        return "BoardViewed";
    }

    public String sessionId() {
        return sessionId;
    }

    public FilterCriteria criteria() {
        return criteria;
    }
}
