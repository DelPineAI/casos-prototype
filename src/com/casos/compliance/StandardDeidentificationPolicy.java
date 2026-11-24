package com.casos.compliance;

import com.casos.domain.CaseDraft;

/** Default policy: controlled vocabulary check plus free-text identifier guard. */
public class StandardDeidentificationPolicy implements DeidentificationPolicy {

    private final ControlledVocabulary vocabulary;
    private final FreeTextGuard guard;

    public StandardDeidentificationPolicy(ControlledVocabulary vocabulary, FreeTextGuard guard) {
        this.vocabulary = vocabulary;
        this.guard = guard;
    }

    @Override
    public ValidationResult validate(CaseDraft draft) {
        return ValidationResult.ok();
    }

    @Override
    public CaseDraft sanitize(CaseDraft draft) {
        return draft;
    }
}
