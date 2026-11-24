package com.casos.board;

import com.casos.domain.AgeBand;
import com.casos.domain.Case;
import com.casos.domain.StaffingPreference;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/** A CaseCard plus the extra fields shown on the detail view. Inheritance: a detail is a card. */
public class CaseDetail extends CaseCard {

    private final AgeBand ageBand;
    private final Set<StaffingPreference> preferences;
    private final Instant expiresAt;

    public CaseDetail(Case c, Optional<Double> distance, Optional<Integer> overlap) {
        super(c, distance, overlap);
        this.ageBand = c.ageBand();
        this.preferences = c.preferences();
        this.expiresAt = c.expiresAt();
    }

    public AgeBand ageBand() {
        return ageBand;
    }

    public Set<StaffingPreference> preferences() {
        return preferences;
    }

    public Instant expiresAt() {
        return expiresAt;
    }
}
