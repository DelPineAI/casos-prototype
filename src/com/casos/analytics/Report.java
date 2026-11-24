package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.Optional;

/** Abstract base for every report. Subclasses supply a title and a CSV body (Round 4, W5). */
public abstract class Report {

    protected final Instant generatedAt;
    protected final Optional<AgencyId> scope;
    protected final DateRange range;

    protected Report(Instant generatedAt, Optional<AgencyId> scope, DateRange range) {
        this.generatedAt = generatedAt;
        this.scope = scope;
        this.range = range;
    }

    public abstract String title();

    public abstract String toCsv();

    public Instant generatedAt() {
        return generatedAt;
    }

    public Optional<AgencyId> scope() {
        return scope;
    }

    public DateRange range() {
        return range;
    }
}
