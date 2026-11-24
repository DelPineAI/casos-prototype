package com.casos.domain;

import java.util.Optional;

/** Port for ZIP and device coordinate resolution. Implemented by com.casos.geo.ZipResolver. */
public interface GeoResolver {

    Optional<GeoPoint> resolve(ZipCode zip);

    GeoPoint resolveDevice(double latitude, double longitude);

    /** County a ZIP belongs to, used to re-derive the protected county column (S14.5). */
    Optional<County> countyOf(ZipCode zip);
}
