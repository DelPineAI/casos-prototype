package com.casos.geo;

import com.casos.domain.GeoPoint;
import com.casos.domain.ZipCode;
import java.util.Map;
import java.util.Optional;

/** Static lookup from ZIP code to centroid coordinate. */
public class ZipCentroidTable {

    private final Map<ZipCode, GeoPoint> centroids;

    public ZipCentroidTable(Map<ZipCode, GeoPoint> seed) {
        this.centroids = seed;
    }

    public Optional<GeoPoint> lookup(ZipCode z) {
        return Optional.ofNullable(centroids.get(z));
    }
}
