package com.example.exercisesystem.model;

public enum WorkoutType {
    OUTDOOR("야외운동"),
    INDOOR("실내운동");

    private final String displayName;

    WorkoutType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
