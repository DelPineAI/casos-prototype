package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Member. Immutable value object. */
public record MemberId(UUID value) {

    public static MemberId newId() {
        return new MemberId(UUID.randomUUID());
    }
}
