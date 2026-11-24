package com.casos.lifecycle;

import java.util.List;

/** Thrown by CaseFactory when a draft violates the de-identification policy. */
public class ValidationException extends RuntimeException {

    private final List<String> violations;

    public ValidationException(List<String> violations) {
        super(String.join("; ", violations));
        this.violations = violations;
    }

    public List<String> violations() {
        return violations;
    }
}
