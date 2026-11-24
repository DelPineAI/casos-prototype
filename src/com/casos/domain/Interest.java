package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

/** An expression of interest in a case, from a signed-in or anonymous clinician (S4.1 to S4.4). */
public class Interest {

    private final InterestId interestId;
    private final CaseId caseId;
    private final AgencyId agencyId;
    private final Optional<ClinicianId> clinicianId;
    private final Contact contact;
    private final Optional<String> note;
    private InterestStatus status;
    private final Instant submittedAt;

    public Interest(InterestId interestId, CaseId caseId, AgencyId agencyId,
                    Optional<ClinicianId> clinicianId, Contact contact,
                    Optional<String> note, Instant submittedAt) {
        this.interestId = interestId;
        this.caseId = caseId;
        this.agencyId = agencyId;
        this.clinicianId = clinicianId;
        this.contact = contact;
        this.note = note;
        this.status = InterestStatus.NEW;
        this.submittedAt = submittedAt;
    }

    /** Coordinator triage: NEW to CONTACTED to CLOSED (S12.3). */
    public void advance(InterestStatus next) {
        this.status = next;
    }

    public InterestId interestId() {
        return interestId;
    }

    public CaseId caseId() {
        return caseId;
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

    public Optional<String> note() {
        return note;
    }

    public InterestStatus status() {
        return status;
    }

    public Instant submittedAt() {
        return submittedAt;
    }
}
