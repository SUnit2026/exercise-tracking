package com.example.exercisesystem.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutFormatters {
    private static final SimpleDateFormat ROW_DATE_FORMAT =
            new SimpleDateFormat("M/d(E)", Locale.KOREAN);
    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("d", Locale.KOREAN);

    private WorkoutFormatters() {
    }

    public static String formatDuration(long durationMillis) {
        long totalSeconds = Math.max(durationMillis / 1000, 0);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    public static String formatDistance(double distanceKm) {
        return String.format(Locale.US, "%.2fkm", Math.max(distanceKm, 0.0));
    }

    public static String formatCalories(int calories) {
        return Math.max(calories, 0) + "kcal";
    }

    public static String formatRowDate(long millis) {
        return ROW_DATE_FORMAT.format(new Date(millis));
    }

    public static String formatDayLabel(long millis) {
        return DAY_FORMAT.format(new Date(millis));
    }
}
