package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import java.util.HashMap;
import java.util.Map;

/** Per-agency incrementing case number (S10.5). */
public class CaseNumberSequence {

    private final Map<AgencyId, Integer> counters;

    public CaseNumberSequence() {
        this.counters = new HashMap<>();
    }

    public int next(AgencyId a) {
        return 0;
    }
}
