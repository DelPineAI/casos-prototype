package com.casos.compliance;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Detects names, member IDs and dates of birth in any free text (S14.1, S14.6).
 * Casos cases have no free text field at all; this guard protects the few places a person can type,
 * such as the note on an expression of interest, from carrying client identifiers.
 */
public class FreeTextGuard {

    private static final List<Pattern> IDENTIFIERS = List.of(
            // Dates such as 04/12/2019, 4-12-19 or 2019-04-12
            Pattern.compile("\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\b"),
            Pattern.compile("\\b(19|20)\\d{2}-\\d{2}-\\d{2}\\b"),
            // Labelled identifiers: DOB, member id, Medicaid number, client name
            Pattern.compile("(?i)\\b(dob|date of birth|member\\s*(id|#|number)|medicaid\\s*(id|#|number)|client'?s?\\s+name)\\b"),
            // Long letter+digit runs typical of insurance member IDs, e.g. W123456789 or ABC12345678
            Pattern.compile("\\b[A-Za-z]{1,3}\\d{7,}\\b"),
            // Social Security style numbers
            Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"));

    public boolean containsIdentifier(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return IDENTIFIERS.stream().anyMatch(p -> p.matcher(text).find());
    }
}
