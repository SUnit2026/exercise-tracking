package com.example.exercisesystem.model;

public class WorkoutRecord {
    private final WorkoutType workoutType;
    private final long startedAtMillis;
    private final long endedAtMillis;
    private final long activeDurationMillis;
    private final double distanceKm;
    private final int calories;

    public WorkoutRecord(
            WorkoutType workoutType,
            long startedAtMillis,
            long endedAtMillis,
            long activeDurationMillis,
            double distanceKm,
            int calories
    ) {
        this.workoutType = workoutType;
        this.startedAtMillis = startedAtMillis;
        this.endedAtMillis = endedAtMillis;
        this.activeDurationMillis = activeDurationMillis;
        this.distanceKm = distanceKm;
        this.calories = calories;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public long getStartedAtMillis() {
        return startedAtMillis;
    }

    public long getEndedAtMillis() {
        return endedAtMillis;
    }

    public long getActiveDurationMillis() {
        return activeDurationMillis;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getCalories() {
        return calories;
    }
}
