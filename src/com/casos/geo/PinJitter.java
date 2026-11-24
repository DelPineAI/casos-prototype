package com.casos.geo;

import com.casos.domain.CaseId;
import com.casos.domain.GeoPoint;
import java.util.Random;

/**
 * Deterministic per-case offset of 0.3 to 0.8 miles so the map never shows the real address (S14.4).
 * Seeding the random generator with the case id keeps a pin stable between page loads.
 */
public class PinJitter {

    private static final double MILES_PER_DEGREE_LAT = 69.0;

    private final double minMiles;
    private final double maxMiles;

    public PinJitter() {
        this.minMiles = 0.3;
        this.maxMiles = 0.8;
    }

    public GeoPoint offset(GeoPoint exact, CaseId seed) {
        Random random = new Random(seed.value().getMostSignificantBits() ^ seed.value().getLeastSignificantBits());
        double distance = minMiles + random.nextDouble() * (maxMiles - minMiles);
        double bearing = random.nextDouble() * 2 * Math.PI;
        double dLat = distance * Math.cos(bearing) / MILES_PER_DEGREE_LAT;
        double milesPerDegreeLon = MILES_PER_DEGREE_LAT * Math.cos(Math.toRadians(exact.latitude()));
        double dLon = distance * Math.sin(bearing) / milesPerDegreeLon;
        return new GeoPoint(exact.latitude() + dLat, exact.longitude() + dLon);
    }

    public double minMiles() {
        return minMiles;
    }

    public double maxMiles() {
        return maxMiles;
    }
}
