package com.example.exercisesystem.model;

public class GpsPoint {
    private final double latitude;
    private final double longitude;
    private final long timestampMillis;

    public GpsPoint(double latitude, double longitude, long timestampMillis) {
        validateLatitude(latitude);
        validateLongitude(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
        this.timestampMillis = timestampMillis;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    private void validateLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }
    }

    private void validateLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
    }
}
