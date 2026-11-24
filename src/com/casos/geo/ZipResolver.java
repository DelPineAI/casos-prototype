package com.casos.geo;

import com.casos.domain.GeoPoint;
import com.casos.domain.GeoResolver;
import com.casos.domain.ZipCode;
import java.util.Optional;

/** Implements the GeoResolver port using the centroid table (S3.1, S3.2). */
public class ZipResolver implements GeoResolver {

    private final ZipCentroidTable table;

    public ZipResolver(ZipCentroidTable table) {
        this.table = table;
    }

    @Override
    public Optional<GeoPoint> resolve(ZipCode z) {
        return table.lookup(z);
    }

    @Override
    public GeoPoint resolveDevice(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }
}
