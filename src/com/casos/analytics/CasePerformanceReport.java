package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** RPT-01: per-case detail views, interests and outcome. */
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
        StringBuilder csv = new StringBuilder("case_number,detail_views,interests,outcome\n");
        for (CasePerformanceRow row : rows) {
            csv.append(row.caseNumber()).append(',').append(row.detailViews()).append(',')
               .append(row.interests()).append(',').append(row.outcome()).append('\n');
        }
        return csv.toString();
    }

    public List<CasePerformanceRow> rows() {
        return rows;
    }
}
