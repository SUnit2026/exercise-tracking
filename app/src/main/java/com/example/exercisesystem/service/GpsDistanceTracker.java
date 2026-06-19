package com.example.exercisesystem.service;

import com.example.exercisesystem.model.GpsPoint;
import com.example.exercisesystem.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GpsDistanceTracker {
    private final List<GpsPoint> points = new ArrayList<>();
    private double totalDistanceKm = 0.0;
    private boolean tracking = false;
    private boolean resetAnchorOnNextLocation = false;

    public void start() {
        points.clear();
        totalDistanceKm = 0.0;
        tracking = true;
        resetAnchorOnNextLocation = false;
    }

    public void pause() {
        tracking = false;
    }

    public void resume() {
        tracking = true;
        resetAnchorOnNextLocation = true;
    }

    public void stop() {
        tracking = false;
    }

    public void addLocation(double latitude, double longitude, long timestampMillis) {
        if (!tracking) {
            return;
        }

        GpsPoint newPoint = new GpsPoint(latitude, longitude, timestampMillis);
        if (resetAnchorOnNextLocation) {
            resetAnchorOnNextLocation = false;
        } else if (!points.isEmpty()) {
            GpsPoint lastPoint = points.get(points.size() - 1);
            totalDistanceKm += DistanceCalculator.calculateKm(lastPoint, newPoint);
        }

        points.add(newPoint);
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public List<GpsPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public boolean isTracking() {
        return tracking;
    }
}
