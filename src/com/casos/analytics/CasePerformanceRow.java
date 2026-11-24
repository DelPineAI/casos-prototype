package com.casos.analytics;

import com.casos.domain.CaseId;
import com.casos.domain.CaseStatus;

public record CasePerformanceRow(CaseId caseId, int caseNumber, int views, int detailViews,
                                 int interests, CaseStatus outcome) {
}
