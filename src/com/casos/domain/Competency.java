package com.casos.domain;

/** Controlled vocabulary entry for a clinical competency (S14.1). */
public final class Competency {

    private final String code;
    private final String label;

    public Competency(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }
}
