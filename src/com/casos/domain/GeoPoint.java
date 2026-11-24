package com.casos.domain;

/** Latitude and longitude pair. */
public record GeoPoint(double latitude, double longitude) {

    private static final double EARTH_RADIUS_MILES = 3958.8;

    /** Great-circle distance in miles to another point (haversine, S3.4). */
    public double milesTo(GeoPoint other) {
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(other.longitude - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_MILES * Math.asin(Math.sqrt(a));
    }
}
