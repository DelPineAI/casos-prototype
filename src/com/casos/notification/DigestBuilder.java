package com.casos.notification;

import com.casos.domain.CaseQuery;
import com.casos.domain.Clinician;
import com.casos.domain.GeoResolver;
import java.time.Clock;
import java.util.Optional;

/** Assembles the weekly digest from a clinician's saved filters and subscriptions (S8.3). */
public class DigestBuilder {

    private final CaseQuery cases;
    private final GeoResolver geo;
    private final Clock clock;

    public DigestBuilder(CaseQuery cases, GeoResolver geo, Clock clock) {
        this.cases = cases;
        this.geo = geo;
        this.clock = clock;
    }

    /** Empty when nothing matched this week. */
    public Optional<EmailMessage> build(Clinician c) {
        return Optional.empty();
    }
}
