package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** RPT-01: per-case views, detail views, interests and outcome. */
public class CasePerformanceReport extends Report {

    private final List<CasePerformanceRow> rows;

    public CasePerformanceReport(Instant generatedAt, Optional<AgencyId> scope, DateRange range,
                                 List<CasePerformanceRow> rows) {
        super(generatedAt, scope, range);
        this.rows = rows;
    }

    @Override
    public String title() {
        return "Case Performance";
    }

    @Override
    public String toCsv() {
        return "";
    }

    public List<CasePerformanceRow> rows() {
        return rows;
    }
}
