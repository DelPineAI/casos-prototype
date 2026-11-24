package com.casos.analytics;

import com.casos.domain.AgencyId;
import com.casos.domain.County;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/** RPT-03: open cases by county and coverage type. */
public class BoardInventoryReport extends Report {

    private final Map<County, Integer> openByCounty;
    private final int coverageOpen;

    public BoardInventoryReport(Instant generatedAt, Optional<AgencyId> scope, DateRange range,
                                Map<County, Integer> openByCounty, int coverageOpen) {
        super(generatedAt, scope, range);
        this.openByCounty = openByCounty;
        this.coverageOpen = coverageOpen;
    }

    @Override
    public String title() {
        return "Board Inventory";
    }

    @Override
    public String toCsv() {
        return "";
    }
}
