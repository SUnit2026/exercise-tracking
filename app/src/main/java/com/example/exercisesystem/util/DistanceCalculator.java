package com.example.exercisesystem.util;

import com.example.exercisesystem.model.GpsPoint;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator() {
    }

    public static double calculateKm(GpsPoint startPoint, GpsPoint endPoint) {
        return calculateKm(
                startPoint.getLatitude(),
                startPoint.getLongitude(),
                endPoint.getLatitude(),
                endPoint.getLongitude()
        );
    }

    public static double calculateKm(
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude
    ) {
        double latitudeDistance = Math.toRadians(endLatitude - startLatitude);
        double longitudeDistance = Math.toRadians(endLongitude - startLongitude);
        double startLatitudeRadians = Math.toRadians(startLatitude);
        double endLatitudeRadians = Math.toRadians(endLatitude);

        double haversine = Math.pow(Math.sin(latitudeDistance / 2), 2)
                + Math.cos(startLatitudeRadians)
                * Math.cos(endLatitudeRadians)
                * Math.pow(Math.sin(longitudeDistance / 2), 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return EARTH_RADIUS_KM * centralAngle;
    }
}
