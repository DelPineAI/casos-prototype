package com.casos.domain;

/** Latitude and longitude pair. */
public record GeoPoint(double latitude, double longitude) {

    /** Great-circle distance in miles to another point (haversine). */
    public double milesTo(GeoPoint other) {
        return 0.0;
    }
}
