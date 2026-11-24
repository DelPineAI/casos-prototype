package com.casos.lifecycle;

import com.casos.compliance.DeidentificationPolicy;
import com.casos.compliance.ValidationResult;
import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.CaseId;
import com.casos.domain.County;
import com.casos.domain.GeoPoint;
import com.casos.domain.GeoResolver;
import com.casos.geo.PinJitter;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Builds a Case from a draft: re-derive, validate, sanitize, resolve, number, jitter (Round 4, W1). */
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

    /** Throws ValidationException when the draft fails the de-identification policy or the ZIP is out of area. */
    public Case create(CaseDraft draft, AgencyId agency) {
        List<String> violations = new ArrayList<>();

        // County is a protected column: always re-derived from the ZIP, never taken from the form (S14.5).
        Optional<County> county = draft.zip() == null ? Optional.empty() : geo.countyOf(draft.zip());
        Optional<GeoPoint> exact = draft.zip() == null ? Optional.empty() : geo.resolve(draft.zip());
        if (draft.zip() != null && (county.isEmpty() || exact.isEmpty())) {
            violations.add("ZIP " + draft.zip().value() + " is outside the Miami-Dade and Broward service area.");
        }

        CaseDraft derived = new CaseDraft(agency, draft.coverageType(), draft.credentialRequired(), draft.zip(),
                county.orElse(null), draft.ageBand(), draft.weeklyHours(), draft.schedule(),
                draft.insurancePlan(), draft.competencies(), draft.hourlyRateCents(), draft.preferences());

        ValidationResult result = policy.validate(derived);
        violations.addAll(result.violations());
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }

        CaseDraft clean = policy.sanitize(derived);
        CaseId id = CaseId.newId();
        int number = sequence.next(agency);
        GeoPoint display = jitter.offset(exact.orElseThrow(), id);
        return new Case(id, agency, number, clean, exact.orElseThrow(), display, clock.instant());
    }
}
