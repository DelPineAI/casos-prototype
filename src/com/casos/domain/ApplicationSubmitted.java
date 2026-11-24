package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class ApplicationSubmitted extends DomainEvent {

    private final ApplicationId applicationId;

    public ApplicationSubmitted(Instant occurredAt, ApplicationId applicationId, AgencyId agencyId) {
        super(occurredAt, Optional.of(agencyId));
        this.applicationId = applicationId;
    }

    @Override
    public String type() {
        return "ApplicationSubmitted";
    }

    public ApplicationId applicationId() {
        return applicationId;
    }
}
