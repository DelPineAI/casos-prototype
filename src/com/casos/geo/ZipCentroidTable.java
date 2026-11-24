package com.casos.geo;

import com.casos.domain.County;
import com.casos.domain.GeoPoint;
import com.casos.domain.ZipCode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Static lookup from ZIP code to centroid coordinate and county (seeded reference data, M6). */
public class ZipCentroidTable {

    private final Map<ZipCode, GeoPoint> centroids;
    private final Map<ZipCode, County> counties;

    public ZipCentroidTable(Map<ZipCode, GeoPoint> seed) {
        this(seed, Map.of());
    }

    public ZipCentroidTable(Map<ZipCode, GeoPoint> seed, Map<ZipCode, County> counties) {
        this.centroids = new HashMap<>(seed);
        this.counties = new HashMap<>(counties);
    }

    public Optional<GeoPoint> lookup(ZipCode z) {
        return Optional.ofNullable(centroids.get(z));
    }

    public Optional<County> countyOf(ZipCode z) {
        return Optional.ofNullable(counties.get(z));
    }

    public Set<ZipCode> zips() {
        return Collections.unmodifiableSet(centroids.keySet());
    }

    /** Approximate centroids for a representative set of Miami-Dade and Broward ZIP codes. */
    public static ZipCentroidTable southFlorida() {
        Map<ZipCode, GeoPoint> points = new HashMap<>();
        Map<ZipCode, County> owners = new HashMap<>();
        String[] miamiDade = {
            "33199 25.7562 -80.3755", "33174 25.7629 -80.3609", "33165 25.7344 -80.3588",
            "33155 25.7393 -80.3100", "33186 25.6563 -80.4110", "33173 25.7006 -80.3620",
            "33176 25.6570 -80.3630", "33183 25.7006 -80.4050", "33175 25.7337 -80.4066",
            "33144 25.7626 -80.3101", "33126 25.7765 -80.2995", "33172 25.7858 -80.3712",
            "33178 25.8130 -80.3990", "33166 25.8235 -80.3020", "33135 25.7665 -80.2346",
            "33130 25.7671 -80.2050", "33131 25.7646 -80.1898", "33137 25.8150 -80.1890",
            "33139 25.7826 -80.1340", "33141 25.8494 -80.1330", "33161 25.8930 -80.1840",
            "33169 25.9440 -80.2170", "33015 25.9390 -80.3170", "33012 25.8650 -80.3010",
            "33157 25.6060 -80.3500", "33032 25.5320 -80.3920", "33033 25.4790 -80.4110",
            "33030 25.4810 -80.5040"
        };
        String[] broward = {
            "33023 25.9890 -80.2120", "33024 26.0290 -80.2440", "33025 25.9880 -80.2840",
            "33027 25.9870 -80.3450", "33028 26.0180 -80.3440", "33029 25.9930 -80.4060",
            "33004 26.0540 -80.1410", "33009 25.9850 -80.1480", "33020 26.0170 -80.1510",
            "33021 26.0230 -80.1850", "33026 26.0260 -80.2970", "33301 26.1220 -80.1280",
            "33304 26.1380 -80.1210", "33311 26.1430 -80.1720", "33312 26.0880 -80.1790",
            "33314 26.0680 -80.2240", "33317 26.1110 -80.2270", "33324 26.1140 -80.2620",
            "33325 26.1100 -80.3220", "33326 26.1170 -80.3700", "33351 26.1790 -80.2750",
            "33441 26.3100 -80.0990", "33065 26.2720 -80.2600", "33071 26.2430 -80.2600"
        };
        load(miamiDade, County.MIAMI_DADE, points, owners);
        load(broward, County.BROWARD, points, owners);
        return new ZipCentroidTable(points, owners);
    }

    private static void load(String[] rows, County county, Map<ZipCode, GeoPoint> points, Map<ZipCode, County> owners) {
        for (String row : rows) {
            String[] parts = row.split(" ");
            ZipCode zip = new ZipCode(parts[0]);
            points.put(zip, new GeoPoint(Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
            owners.put(zip, county);
        }
    }
}
