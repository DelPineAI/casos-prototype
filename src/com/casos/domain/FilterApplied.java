package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class FilterApplied extends DomainEvent {

    private final String sessionId;
    private final FilterCriteria criteria;

    public FilterApplied(Instant occurredAt, String sessionId, FilterCriteria criteria) {
        super(occurredAt, Optional.empty());
        this.sessionId = sessionId;
        this.criteria = criteria;
    }

    @Override
    public String type() {
        return "FilterApplied";
    }

    public String sessionId() {
        return sessionId;
    }

    public FilterCriteria criteria() {
        return criteria;
    }
}
