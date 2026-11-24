package com.casos.lifecycle;

import com.casos.compliance.DeidentificationPolicy;
import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.GeoResolver;
import com.casos.geo.PinJitter;
import java.time.Clock;

/** Builds a Case from a draft: validate, sanitize, resolve, number, jitter (Round 4, W1). */
public class CaseFactory {

    private final DeidentificationPolicy policy;
    private final GeoResolver geo;
    private final PinJitter jitter;
    private final CaseNumberSequence sequence;
    private final Clock clock;

    public CaseFactory(DeidentificationPolicy policy, GeoResolver geo, PinJitter jitter,
                       CaseNumberSequence sequence, Clock clock) {
        this.policy = policy;
        this.geo = geo;
        this.jitter = jitter;
        this.sequence = sequence;
        this.clock = clock;
    }

    /** Throws ValidationException when the draft fails the de-identification policy. */
    public Case create(CaseDraft draft, AgencyId agency) {
        return null;
    }
}
