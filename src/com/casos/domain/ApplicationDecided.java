package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class ApplicationDecided extends DomainEvent {

    private final ApplicationId applicationId;
    private final ApplicationStatus outcome;
    private final String applicantEmail;

    public ApplicationDecided(Instant occurredAt, ApplicationId applicationId, AgencyId agencyId, ApplicationStatus outcome, String applicantEmail) {
        super(occurredAt, Optional.of(agencyId));
        this.applicationId = applicationId;
        this.outcome = outcome;
        this.applicantEmail = applicantEmail;
    }

    @Override
    public String type() {
        return "ApplicationDecided";
    }

    public ApplicationId applicationId() {
        return applicationId;
    }

    public ApplicationStatus outcome() {
        return outcome;
    }

    public String applicantEmail() {
        return applicantEmail;
    }
}
