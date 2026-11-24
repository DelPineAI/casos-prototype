package com.casos.domain;

import java.time.Instant;
import java.util.Optional;
import java.time.LocalDate;

public final class InServiceExpiring extends DomainEvent {

    private final ClinicianId clinicianId;
    private final InServiceType inServiceType;
    private final LocalDate expiresOn;

    public InServiceExpiring(Instant occurredAt, ClinicianId clinicianId, InServiceType inServiceType, LocalDate expiresOn) {
        super(occurredAt, Optional.empty());
        this.clinicianId = clinicianId;
        this.inServiceType = inServiceType;
        this.expiresOn = expiresOn;
    }

    @Override
    public String type() {
        return "InServiceExpiring";
    }

    public ClinicianId clinicianId() {
        return clinicianId;
    }

    public InServiceType inServiceType() {
        return inServiceType;
    }

    public LocalDate expiresOn() {
        return expiresOn;
    }
}
