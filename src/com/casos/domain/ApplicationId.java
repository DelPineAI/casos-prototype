package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Application. Immutable value object. */
public record ApplicationId(UUID value) {

    public static ApplicationId newId() {
        return new ApplicationId(UUID.randomUUID());
    }
}
