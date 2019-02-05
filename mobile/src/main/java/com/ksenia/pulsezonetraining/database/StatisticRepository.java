package com.ksenia.pulsezonetraining.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksenia.pulsezonetraining.WorkoutHistoryItem;

import java.util.ArrayList;

/**
 * Created by ksenia on 03.02.19.
 */

public class StatisticRepository {
    private FitnessSQLiteDBHelper dbHelper;
    SQLiteDatabase database;

    public StatisticRepository(FitnessSQLiteDBHelper helper) {
        dbHelper = helper;
        database =  dbHelper.getWritableDatabase();
    }

    public void addStatisticRecord(long startTime, long elapsedTime, int calories, String zone, long currentTime) {
        ContentValues values = new ContentValues();
        values.put(FitnessSQLiteDBHelper.STATISTIC_START_TIME, startTime);
        values.put(FitnessSQLiteDBHelper.STATISTIC_ELAPSED_TIME, elapsedTime);
        values.put(FitnessSQLiteDBHelper.STATISTIC_CALORIES, calories);
        values.put(FitnessSQLiteDBHelper.STATISTIC_ZONE, zone);
        values.put(FitnessSQLiteDBHelper.STATISTIC_TIMESTAMP, currentTime);
        database.insert(FitnessSQLiteDBHelper.STATISTIC_TABLE_NAME, null, values);
    }

    public ArrayList<WorkoutHistoryItem> listAllWorkouts() {
        ArrayList<WorkoutHistoryItem> workouts = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM statistic", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WorkoutHistoryItem workoutHistoryItem = new WorkoutHistoryItem();
                //workoutHistoryItem.setId(cursor.getInt(cursor.getColumnIndex("id")));
                workoutHistoryItem.setStartTime(cursor.getLong(cursor.getColumnIndex("startTime")));
                workoutHistoryItem.setElapsedTime(cursor.getLong(cursor.getColumnIndex("elapsedTime")));
                workoutHistoryItem.setTotalCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                workoutHistoryItem.setZone(cursor.getString(cursor.getColumnIndex("zone")));
                workoutHistoryItem.setCurrentTime(cursor.getLong(cursor.getColumnIndex("currentTime")));
                workouts.add(0, workoutHistoryItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workouts;
    }

    public void closeDb() {
        database.close();
    }
}
