package com.casos.compliance;

import com.casos.domain.CaseDraft;
import com.casos.domain.Competency;
import com.casos.domain.ZipCode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Default policy: every field must come from a controlled list and stay inside safe bounds. */
public class StandardDeidentificationPolicy implements DeidentificationPolicy {

    public static final int MIN_WEEKLY_HOURS = 1;
    public static final int MAX_WEEKLY_HOURS = 40;
    public static final int MIN_RATE_CENTS = 1500;
    public static final int MAX_RATE_CENTS = 15000;

    private final ControlledVocabulary vocabulary;
    private final FreeTextGuard guard;

    public StandardDeidentificationPolicy(ControlledVocabulary vocabulary, FreeTextGuard guard) {
        this.vocabulary = vocabulary;
        this.guard = guard;
    }

    @Override
    public ValidationResult validate(CaseDraft draft) {
        List<String> violations = new ArrayList<>();
        if (draft.zip() == null || !ZipCode.isValid(draft.zip().value())) {
            violations.add("ZIP code must be five digits.");
        }
        if (draft.coverageType() == null) {
            violations.add("Choose coverage or ongoing.");
        }
        if (draft.credentialRequired() == null) {
            violations.add("Choose the credential required.");
        }
        if (draft.ageBand() == null) {
            violations.add("Choose an age band. Exact ages and dates of birth are never stored.");
        }
        if (draft.weeklyHours() < MIN_WEEKLY_HOURS || draft.weeklyHours() > MAX_WEEKLY_HOURS) {
            violations.add("Weekly hours must be between " + MIN_WEEKLY_HOURS + " and " + MAX_WEEKLY_HOURS + ".");
        }
        if (draft.schedule() == null || draft.schedule().isEmpty()) {
            violations.add("Select at least one day and time block.");
        }
        if (draft.insurancePlan() == null || !vocabulary.containsPlan(draft.insurancePlan().code())) {
            violations.add("Choose an insurance plan from the list.");
        }
        for (Competency c : draft.competencies()) {
            if (!vocabulary.containsCompetency(c.code())) {
                violations.add("Skill '" + c.code() + "' is not in the controlled skills list.");
            }
            if (guard.containsIdentifier(c.label())) {
                violations.add("Skill labels cannot contain identifying information.");
            }
        }
        draft.hourlyRateCents().ifPresent(rate -> {
            if (rate < MIN_RATE_CENTS || rate > MAX_RATE_CENTS) {
                violations.add("Hourly rate must be between $15 and $150.");
            }
        });
        return violations.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(violations);
    }

    /** Replaces every vocabulary value with the canonical entry, so nothing client-supplied survives (S14.5). */
    @Override
    public CaseDraft sanitize(CaseDraft draft) {
        Set<Competency> canonical = new LinkedHashSet<>();
        for (Competency c : draft.competencies()) {
            vocabulary.competency(c.code()).ifPresent(canonical::add);
        }
        return new CaseDraft(draft.agencyId(), draft.coverageType(), draft.credentialRequired(), draft.zip(),
                draft.county(), draft.ageBand(), draft.weeklyHours(), draft.schedule(),
                vocabulary.plan(draft.insurancePlan().code()).orElseThrow(), canonical,
                draft.hourlyRateCents(), draft.preferences());
    }
}
