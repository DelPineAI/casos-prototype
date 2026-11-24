package com.casos.roster;

import com.casos.domain.AgencyId;
import com.casos.domain.ClinicianId;
import com.casos.domain.Contact;
import com.casos.domain.Credential;
import com.casos.domain.InServiceRecord;
import com.casos.domain.ZipCode;
import java.util.Optional;
import java.util.Set;

/** Input for a roster application (S7.6). */
public final class ApplicationRequest {

    private final AgencyId agencyId;
    private final Optional<ClinicianId> clinicianId;
    private final Contact contact;
    private final Credential statedCredential;
    private final Set<InServiceRecord> statedInServices;
    private final Optional<ZipCode> homeZip;

    public ApplicationRequest(AgencyId agencyId, Optional<ClinicianId> clinicianId, Contact contact,
                              Credential statedCredential, Set<InServiceRecord> statedInServices,
                              Optional<ZipCode> homeZip) {
        this.agencyId = agencyId;
        this.clinicianId = clinicianId;
        this.contact = contact;
        this.statedCredential = statedCredential;
        this.statedInServices = statedInServices;
        this.homeZip = homeZip;
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

    public Credential statedCredential() {
        return statedCredential;
    }

    public Set<InServiceRecord> statedInServices() {
        return statedInServices;
    }

    public Optional<ZipCode> homeZip() {
        return homeZip;
    }
}
