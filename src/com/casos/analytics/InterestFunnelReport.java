package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.Optional;

/** RPT-02: board views to detail views to interests. */
public class InterestFunnelReport extends Report {

    private final int viewed;
    private final int detailOpened;
    private final int interested;

    public InterestFunnelReport(Instant generatedAt, Optional<AgencyId> scope, DateRange range,
                                int viewed, int detailOpened, int interested) {
        super(generatedAt, scope, range);
        this.viewed = viewed;
        this.detailOpened = detailOpened;
        this.interested = interested;
    }

    @Override
    public String title() {
        return "Interest Funnel";
    }

    @Override
    public String toCsv() {
        return "";
    }
}
