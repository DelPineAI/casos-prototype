package com.casos.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Base class for every domain event. Subclasses add their own payload. */
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final Optional<AgencyId> agencyId;

    protected DomainEvent(Instant occurredAt, Optional<AgencyId> agencyId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = occurredAt;
        this.agencyId = agencyId;
    }

    /** Short stable name used for analytics records, for example "CasePosted". */
    public abstract String type();

    public UUID eventId() {
        return eventId;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public Optional<AgencyId> agencyId() {
        return agencyId;
    }
}
