package com.casos.geo;

import com.casos.domain.GeoPoint;

/** Haversine distance in miles (S3.4). */
public final class DistanceCalculator {

    private DistanceCalculator() {
    }

    public static double milesBetween(GeoPoint a, GeoPoint b) {
        return a.milesTo(b);
    }
}
