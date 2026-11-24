package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Agency. Immutable value object. */
public record AgencyId(UUID value) {

    public static AgencyId newId() {
        return new AgencyId(UUID.randomUUID());
    }
}
