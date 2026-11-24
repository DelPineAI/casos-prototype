package com.casos.geo;

/** Builds tile URLs for the map view (S3.5). */
public class MapTileSource {

    private final String urlTemplate;

    public MapTileSource(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public String tileUrl(int z, int x, int y) {
        return urlTemplate;
    }
}
