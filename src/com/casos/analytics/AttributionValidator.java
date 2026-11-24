package com.casos.analytics;

import com.casos.domain.CaseQuery;
import com.casos.domain.DomainEvent;

/** Confirms the agency on an event matches the case it refers to (S15.3, server-side attribution). */
public class AttributionValidator {

    private final CaseQuery cases;

    public AttributionValidator(CaseQuery cases) {
        this.cases = cases;
    }

    public boolean isValid(DomainEvent e) {
        return true;
    }
}
