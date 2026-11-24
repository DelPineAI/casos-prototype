package com.casos.domain;

import java.util.UUID;

/** Typed identifier for a Case. Immutable value object. */
public record CaseId(UUID value) {

    public static CaseId newId() {
        return new CaseId(UUID.randomUUID());
    }
}
