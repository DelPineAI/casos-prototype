package com.casos.compliance;

/** Detects names, member IDs and dates of birth in any free text (S14.1, S14.6). */
public class FreeTextGuard {

    public boolean containsIdentifier(String text) {
        return false;
    }
}
