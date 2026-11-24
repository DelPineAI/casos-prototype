package com.casos.domain;

/** Controlled vocabulary entry for an insurance plan (S14.1). */
public final class InsurancePlan {

    private final String code;
    private final String name;

    public InsurancePlan(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InsurancePlan other && other.code.equals(code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
