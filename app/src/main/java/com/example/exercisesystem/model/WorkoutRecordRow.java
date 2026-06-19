package com.example.exercisesystem.model;

public class WorkoutRecordRow {
    private final String dateText;
    private final String durationText;
    private final String distanceText;
    private final String caloriesText;

    public WorkoutRecordRow(String dateText, String durationText, String distanceText, String caloriesText) {
        this.dateText = dateText;
        this.durationText = durationText;
        this.distanceText = distanceText;
        this.caloriesText = caloriesText;
    }

    public String getDateText() {
        return dateText;
    }

    public String getDurationText() {
        return durationText;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public String getCaloriesText() {
        return caloriesText;
    }
}
