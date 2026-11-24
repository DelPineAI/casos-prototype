package com.casos.compliance;

import com.casos.domain.CaseDraft;

/** Abstraction over the de-identification rulebook enforced at write time (S14.1 to S14.6). */
public interface DeidentificationPolicy {

    ValidationResult validate(CaseDraft draft);

    /** Re-derive protected columns (age band, county) from the draft so nothing raw survives (S14.5). */
    CaseDraft sanitize(CaseDraft draft);
}
