package com.casos.compliance;

import java.util.List;

/** Outcome of a policy check. */
public final class ValidationResult {

    private final boolean ok;
    private final List<String> violations;

    public ValidationResult(boolean ok, List<String> violations) {
        this.ok = ok;
        this.violations = violations;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult fail(List<String> violations) {
        return new ValidationResult(false, violations);
    }

    public boolean isOk() {
        return ok;
    }

    public List<String> violations() {
        return violations;
    }
}
