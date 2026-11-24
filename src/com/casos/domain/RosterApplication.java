package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

/** A clinician's application to join an agency roster (S7.6, S7.7, S13.4). */
public class RosterApplication {

    private final ApplicationId applicationId;
    private final AgencyId agencyId;
    private final Optional<ClinicianId> clinicianId;
    private final Contact contact;
    private final ApplicationSnapshot snapshot;
    private ApplicationStatus status;
    private final Instant submittedAt;
    private Optional<Instant> decidedAt;
    private Optional<MemberId> decidedBy;

    public RosterApplication(ApplicationId applicationId, AgencyId agencyId,
                             Optional<ClinicianId> clinicianId, Contact contact,
                             ApplicationSnapshot snapshot, Instant submittedAt) {
        this.applicationId = applicationId;
        this.agencyId = agencyId;
        this.clinicianId = clinicianId;
        this.contact = contact;
        this.snapshot = snapshot;
        this.status = ApplicationStatus.APPLIED;
        this.submittedAt = submittedAt;
        this.decidedAt = Optional.empty();
        this.decidedBy = Optional.empty();
    }

    /** Owner decision. Throws if the application is not in APPLIED state. */
    public void decide(ApplicationStatus outcome, MemberId by, Instant at) {
        if (status != ApplicationStatus.APPLIED) {
            throw new IllegalStateException("Application already decided");
        }
    }

    public ApplicationId applicationId() {
        return applicationId;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public Optional<ClinicianId> clinicianId() {
        return clinicianId;
    }

    public Contact contact() {
        return contact;
    }

    public ApplicationSnapshot snapshot() {
        return snapshot;
    }

    public ApplicationStatus status() {
        return status;
    }

    public Instant submittedAt() {
        return submittedAt;
    }

    public Optional<Instant> decidedAt() {
        return decidedAt;
    }

    public Optional<MemberId> decidedBy() {
        return decidedBy;
    }
}
