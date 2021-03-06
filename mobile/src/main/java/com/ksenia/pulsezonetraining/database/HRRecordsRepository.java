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
    SQLiteDatabase database;
    SQLiteDatabase databaseRead;

    public HRRecordsRepository(Activity activity) {
        database =  new FitnessSQLiteDBHelper(activity).getWritableDatabase();
        databaseRead = new FitnessSQLiteDBHelper(activity).getReadableDatabase();
    }

    public void addNewRecord(int heartRate) {
        ContentValues values = new ContentValues();
        values.put(FitnessSQLiteDBHelper.RECORDS_COLUMN_HR, heartRate);
        values.put(FitnessSQLiteDBHelper.RECORDS_TIMESTAMP, SystemClock.elapsedRealtime());
        database.insert(FitnessSQLiteDBHelper.RECORDS_TABLE_NAME, null, values);
    }

    public int getMaxHeartRate(long startTime, long endTime) {
        Cursor cursor = databaseRead.rawQuery("SELECT MAX(heart_rate) FROM records WHERE time BETWEEN " + startTime + " and " + endTime, null);
        int value = 0;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }

    public int getAverageHeartRate(long startTime, long endTime) {
        Cursor cursor = databaseRead.rawQuery("SELECT AVG(heart_rate) FROM records WHERE time BETWEEN " + startTime + " and " + endTime, null);
        int value = 0;
        if (cursor.moveToFirst()) {
            value = Math.round(cursor.getFloat(0));
        }
        cursor.close();
        return value;
    }

    public void closeDb() {
        databaseRead.close();
        database.close();
    }

}

