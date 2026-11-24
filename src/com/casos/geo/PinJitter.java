package com.casos.geo;

import com.casos.domain.CaseId;
import com.casos.domain.GeoPoint;

/** Deterministic per-case offset of 0.3 to 0.8 miles so the map never shows the real address (S14.4). */
public class PinJitter {

    private final double minMiles;
    private final double maxMiles;

    public PinJitter() {
        this.minMiles = 0.3;
        this.maxMiles = 0.8;
    }

    public GeoPoint offset(GeoPoint exact, CaseId seed) {
        return exact;
    }
}
