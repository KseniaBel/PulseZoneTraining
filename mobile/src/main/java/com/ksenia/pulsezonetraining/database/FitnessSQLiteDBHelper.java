package com.ksenia.pulsezonetraining.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ksenia on 06.01.19.
 */

public class FitnessSQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "fitness_activity_database";
    public static final String RECORDS_TABLE_NAME = "records";
    public static final  String RECORDS_COLUMN_ID = "id";
    public static final String RECORDS_COLUMN_HR = "heart_rate";
    public static final String RECORDS_TIMESTAMP = "time";

    public FitnessSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RECORDS_TABLE_NAME + " (" +
                RECORDS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RECORDS_COLUMN_HR + " INTEGER, " +
                RECORDS_TIMESTAMP + " LONG" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

