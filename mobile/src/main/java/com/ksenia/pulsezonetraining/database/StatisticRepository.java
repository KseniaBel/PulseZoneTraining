package com.ksenia.pulsezonetraining.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ksenia.pulsezonetraining.history.WorkoutHistoryItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ksenia on 03.02.19.
 */

public class StatisticRepository {
    private SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    public StatisticRepository(SQLiteOpenHelper helper) {
        dbHelper = helper;
        database =  dbHelper.getWritableDatabase();
    }

    public void addStatisticRecord(long elapsedTime, int calories, String zone, long currentTime) {
        ContentValues values = new ContentValues();
        values.put(FitnessSQLiteDBHelper.STATISTIC_ELAPSED_TIME, elapsedTime);
        values.put(FitnessSQLiteDBHelper.STATISTIC_CALORIES, calories);
        values.put(FitnessSQLiteDBHelper.STATISTIC_ZONE, zone);
        values.put(FitnessSQLiteDBHelper.STATISTIC_TIMESTAMP, currentTime);
        database.insert(FitnessSQLiteDBHelper.STATISTIC_TABLE_NAME, null, values);
    }

    public Map<String, List<WorkoutHistoryItem>> getAllWorkoutsGroupedByDate() {
      Map<String, List<WorkoutHistoryItem>> list = new HashMap<>();
      Cursor cursor = database.rawQuery("SELECT elapsedTime, calories, zone, currentTime, strftime('%m-%Y', currentTime / 1000, 'unixepoch', 'localtime') as date " +
              "FROM statistic " +
              "ORDER BY date desc", null);
        if (cursor.moveToFirst()) {
            do {
                List<WorkoutHistoryItem> workoutItems = new ArrayList<>();
                WorkoutHistoryItem workoutHistoryItem = new WorkoutHistoryItem();
                workoutHistoryItem.setElapsedTime(cursor.getLong(cursor.getColumnIndex(FitnessSQLiteDBHelper.STATISTIC_ELAPSED_TIME)));
                workoutHistoryItem.setTotalCalories(cursor.getInt(cursor.getColumnIndex(FitnessSQLiteDBHelper.STATISTIC_CALORIES)));
                workoutHistoryItem.setZone(cursor.getString(cursor.getColumnIndex(FitnessSQLiteDBHelper.STATISTIC_ZONE)));
                workoutHistoryItem.setCurrentTime(cursor.getLong(cursor.getColumnIndex(FitnessSQLiteDBHelper.STATISTIC_TIMESTAMP)));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if(list.containsKey(date)) {
                    workoutItems = list.get(date);
                }
                workoutItems.add(0, workoutHistoryItem);
                list.put(date, workoutItems);
            } while (cursor.moveToNext());
        }
            cursor.close();
            return list;
    }

    public void closeDb() {
        database.close();
    }
}
