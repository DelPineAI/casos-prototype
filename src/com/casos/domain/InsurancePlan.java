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
}
