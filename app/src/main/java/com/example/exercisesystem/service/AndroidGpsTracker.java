package com.example.exercisesystem.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class AndroidGpsTracker implements LocationListener {
    public interface Listener {
        void onDistanceChanged(double distanceKm);

        void onGpsUnavailable(String reason);
    }

    private static final long MIN_TIME_MS = 2000L;
    private static final float MIN_DISTANCE_METERS = 2.0f;
    private static final float MAX_ACCEPTED_ACCURACY_METERS = 50.0f;

    private final Context context;
    private final LocationManager locationManager;
    private final GpsDistanceTracker distanceTracker;
    private final Listener listener;

    public AndroidGpsTracker(Context context, Listener listener) {
        this.context = context.getApplicationContext();
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.distanceTracker = new GpsDistanceTracker();
        this.listener = listener;
    }

    public void start() {
        if (!hasLocationPermission()) {
            listener.onGpsUnavailable("Location permission is not granted.");
            return;
        }

        distanceTracker.start();

        try {
            if (hasFineLocationPermission()) {
                requestUpdates(LocationManager.GPS_PROVIDER);
            }
            if (hasCoarseLocationPermission()) {
                requestUpdates(LocationManager.NETWORK_PROVIDER);
            }
        } catch (SecurityException exception) {
            listener.onGpsUnavailable("Location permission is not granted.");
        } catch (IllegalArgumentException exception) {
            listener.onGpsUnavailable("Location provider is unavailable.");
        }
    }

    public void pause() {
        distanceTracker.pause();
    }

    public void resume() {
        distanceTracker.resume();
    }

    public void stop() {
        distanceTracker.stop();
        locationManager.removeUpdates(this);
    }

    public double getDistanceKm() {
        return distanceTracker.getTotalDistanceKm();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null || !isAcceptableLocation(location)) {
            return;
        }

        distanceTracker.addLocation(
                location.getLatitude(),
                location.getLongitude(),
                location.getTime()
        );
        listener.onDistanceChanged(distanceTracker.getTotalDistanceKm());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        listener.onGpsUnavailable(provider + " is disabled.");
    }

    private void requestUpdates(String provider) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(
                    provider,
                    MIN_TIME_MS,
                    MIN_DISTANCE_METERS,
                    this
            );
        }
    }

    private boolean hasLocationPermission() {
        return hasFineLocationPermission() || hasCoarseLocationPermission();
    }

    private boolean hasFineLocationPermission() {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasCoarseLocationPermission() {
        return context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isAcceptableLocation(Location location) {
        return !location.hasAccuracy()
                || location.getAccuracy() <= MAX_ACCEPTED_ACCURACY_METERS;
    }
}
