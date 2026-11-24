package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Clinician. Immutable value object. */
public record ClinicianId(UUID value) {

    public static ClinicianId newId() {
        return new ClinicianId(UUID.randomUUID());
    }
}
