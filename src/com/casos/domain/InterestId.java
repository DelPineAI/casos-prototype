package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Interest. Immutable value object. */
public record InterestId(UUID value) {

    public static InterestId newId() {
        return new InterestId(UUID.randomUUID());
    }
}
