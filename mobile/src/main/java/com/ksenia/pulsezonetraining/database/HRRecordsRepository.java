package com.ksenia.pulsezonetraining.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

/**
 * Created by ksenia on 08.01.19.
 */

public class HRRecordsRepository {
    private FitnessSQLiteDBHelper dbHelper;
    SQLiteDatabase database;

    public HRRecordsRepository(FitnessSQLiteDBHelper helper) {
        dbHelper = helper;
        database =  dbHelper.getWritableDatabase();
    }

    public void addNewRecord(int heartRate) {
        ContentValues values = new ContentValues();
        values.put(FitnessSQLiteDBHelper.RECORDS_COLUMN_HR, heartRate);
        values.put(FitnessSQLiteDBHelper.RECORDS_TIMESTAMP, SystemClock.elapsedRealtime());
        database.insert(FitnessSQLiteDBHelper.RECORDS_TABLE_NAME, null, values);
    }


    public int getMaxHeartRate(long startTime, long endTime) {
        Cursor cursor = database.rawQuery("SELECT (heart_rate) FROM records WHERE time BETWEEN " + startTime + " and " + endTime, null);
        int value = 0;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }

    public int getAverageHeartRate(long startTime, long endTime) {
        Cursor cursor = database.rawQuery("SELECT AVG(heart_rate) FROM records WHERE time BETWEEN " + startTime + " and " + endTime, null);
        int value = 0;
        if (cursor.moveToFirst()) {
            value = Math.round(cursor.getFloat(0));
        }
        cursor.close();
        return value;
    }

    public void closeDb() {
        database.close();
    }

}

