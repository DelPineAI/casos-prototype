package com.casos.board;

import com.casos.domain.CaseId;
import com.casos.domain.ClinicianId;
import com.casos.domain.Contact;
import java.util.Optional;

/** Input for expressing interest in a case (S4.1). */
public final class InterestRequest {

    private final CaseId caseId;
    private final Contact contact;
    private final Optional<String> note;
    private final Optional<ClinicianId> clinicianId;

    public InterestRequest(CaseId caseId, Contact contact, Optional<String> note, Optional<ClinicianId> clinicianId) {
        this.caseId = caseId;
        this.contact = contact;
        this.note = note;
        this.clinicianId = clinicianId;
    }

    public CaseId caseId() {
        return caseId;
    }

    public Contact contact() {
        return contact;
    }

    public Optional<String> note() {
        return note;
    }

    public Optional<ClinicianId> clinicianId() {
        return clinicianId;
    }
}
