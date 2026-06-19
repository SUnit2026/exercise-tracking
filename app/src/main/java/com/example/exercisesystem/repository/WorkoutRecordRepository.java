package com.example.exercisesystem.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.exercisesystem.db.AppDatabase;
import com.example.exercisesystem.db.DailyWorkoutCalories;
import com.example.exercisesystem.db.WorkoutRecordDao;
import com.example.exercisesystem.db.WorkoutRecordEntity;
import com.example.exercisesystem.model.ChartBar;
import com.example.exercisesystem.model.WorkoutRecord;
import com.example.exercisesystem.model.WorkoutRecordRow;
import com.example.exercisesystem.model.WorkoutType;
import com.example.exercisesystem.util.WorkoutFormatters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRecordRepository {
    public interface ResultCallback<T> {
        void onResult(T result);
    }

    private final WorkoutRecordDao workoutRecordDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WorkoutRecordRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.workoutRecordDao = database.workoutRecordDao();
    }

    public void saveRecord(WorkoutRecord record, ResultCallback<Long> callback) {
        executorService.execute(() -> {
            long minimumMillis = getLastMonthStartMillis(record.getEndedAtMillis());
            workoutRecordDao.deleteRecordsOlderThan(minimumMillis);
            long id = workoutRecordDao.insert(toEntity(record));
            postResult(callback, id);
        });
    }

    public void getLast7DaysRows(ResultCallback<List<WorkoutRecordRow>> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            long startMillis = getLastDaysStartMillis(nowMillis, 7);
            List<WorkoutRecordEntity> entities = workoutRecordDao.getRecordsBetweenDescending(
                    startMillis,
                    nowMillis
            );
            postResult(callback, toRows(entities));
        });
    }

    public void getLastMonthRows(ResultCallback<List<WorkoutRecordRow>> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            long startMillis = getLastMonthStartMillis(nowMillis);
            List<WorkoutRecordEntity> entities = workoutRecordDao.getRecordsBetweenDescending(
                    startMillis,
                    nowMillis
            );
            postResult(callback, toRows(entities));
        });
    }

    public void getLast7DaysTotalCalories(ResultCallback<Integer> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            Integer totalCalories = workoutRecordDao.getTotalCaloriesBetween(
                    getLastDaysStartMillis(nowMillis, 7),
                    nowMillis
            );
            postResult(callback, totalCalories == null ? 0 : totalCalories);
        });
    }

    public void getLastMonthTotalCalories(ResultCallback<Integer> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            Integer totalCalories = workoutRecordDao.getTotalCaloriesBetween(
                    getLastMonthStartMillis(nowMillis),
                    nowMillis
            );
            postResult(callback, totalCalories == null ? 0 : totalCalories);
        });
    }

    public void getLast7DaysChartBars(ResultCallback<List<ChartBar>> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            long startMillis = getLastDaysStartMillis(nowMillis, 7);
            List<DailyWorkoutCalories> dailyCalories = workoutRecordDao.getDailyCaloriesBetween(
                    startMillis,
                    nowMillis
            );

            Map<Integer, Integer> caloriesByDay = new HashMap<>();
            for (DailyWorkoutCalories item : dailyCalories) {
                caloriesByDay.put(item.dayOfMonth, item.totalCalories);
            }

            List<ChartBar> chartBars = new ArrayList<>();
            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            calendar.setTimeInMillis(startMillis);
            for (int index = 0; index < 7; index++) {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int calories = caloriesByDay.containsKey(dayOfMonth)
                        ? caloriesByDay.get(dayOfMonth)
                        : 0;
                chartBars.add(new ChartBar(String.valueOf(dayOfMonth), calories));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            postResult(callback, chartBars);
        });
    }

    public void getLastMonthWeeklyCaloriesChartBars(ResultCallback<List<ChartBar>> callback) {
        executorService.execute(() -> {
            long nowMillis = System.currentTimeMillis();
            long startMillis = getLastMonthStartMillis(nowMillis);
            List<WorkoutRecordEntity> entities = workoutRecordDao.getRecordsBetweenAscending(
                    startMillis,
                    nowMillis
            );

            int[] weeklyCalories = new int[5];
            Calendar startCalendar = Calendar.getInstance(Locale.KOREA);
            startCalendar.setTimeInMillis(startMillis);

            for (WorkoutRecordEntity entity : entities) {
                long diffMillis = entity.endedAtMillis - startCalendar.getTimeInMillis();
                int weekIndex = (int) (diffMillis / (7L * 24L * 60L * 60L * 1000L));
                if (weekIndex < 0) {
                    weekIndex = 0;
                }
                if (weekIndex > 4) {
                    weekIndex = 4;
                }
                weeklyCalories[weekIndex] += entity.calories;
            }

            List<ChartBar> chartBars = new ArrayList<>();
            for (int index = 0; index < weeklyCalories.length; index++) {
                chartBars.add(new ChartBar((index + 1) + "주", weeklyCalories[index]));
            }

            postResult(callback, chartBars);
        });
    }

    private WorkoutRecordEntity toEntity(WorkoutRecord record) {
        return new WorkoutRecordEntity(
                record.getWorkoutType().name(),
                record.getStartedAtMillis(),
                record.getEndedAtMillis(),
                record.getActiveDurationMillis(),
                record.getDistanceKm(),
                record.getCalories()
        );
    }

    private List<WorkoutRecordRow> toRows(List<WorkoutRecordEntity> entities) {
        List<WorkoutRecordRow> rows = new ArrayList<>();
        for (WorkoutRecordEntity entity : entities) {
            rows.add(new WorkoutRecordRow(
                    WorkoutFormatters.formatRowDate(entity.endedAtMillis),
                    WorkoutFormatters.formatDuration(entity.activeDurationMillis),
                    WorkoutFormatters.formatDistance(entity.distanceKm),
                    WorkoutFormatters.formatCalories(entity.calories)
            ));
        }

        return rows;
    }

    public WorkoutRecord toRecord(WorkoutRecordEntity entity) {
        return new WorkoutRecord(
                WorkoutType.valueOf(entity.workoutType),
                entity.startedAtMillis,
                entity.endedAtMillis,
                entity.activeDurationMillis,
                entity.distanceKm,
                entity.calories
        );
    }

    private long getLastDaysStartMillis(long referenceMillis, int days) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(referenceMillis);
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        setStartOfDay(calendar);
        return calendar.getTimeInMillis();
    }

    private long getLastMonthStartMillis(long referenceMillis) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(referenceMillis);
        calendar.add(Calendar.MONTH, -1);
        setStartOfDay(calendar);
        return calendar.getTimeInMillis();
    }

    private void setStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private <T> void postResult(ResultCallback<T> callback, T result) {
        if (callback == null) {
            return;
        }

        mainHandler.post(() -> callback.onResult(result));
    }
}
