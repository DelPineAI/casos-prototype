package com.casos.domain;

/** Five-digit ZIP code. Stored on a Case but never rendered to the public board (S14.3). */
public record ZipCode(String value) {

    public static boolean isValid(String raw) {
        return raw != null && raw.matches("\\d{5}");
    }
}
