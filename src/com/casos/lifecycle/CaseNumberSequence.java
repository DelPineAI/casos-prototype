package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Per-agency incrementing case number, starting at 1001 (S10.5). */
public class CaseNumberSequence {

    private static final int FIRST = 1001;

    private final Map<AgencyId, Integer> counters;

    public CaseNumberSequence() {
        this.counters = new ConcurrentHashMap<>();
    }

    public int next(AgencyId a) {
        return counters.merge(a, FIRST, (current, ignored) -> current + 1);
    }
}
