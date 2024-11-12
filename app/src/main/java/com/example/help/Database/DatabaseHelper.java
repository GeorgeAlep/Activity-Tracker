package com.example.help.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.help.Model.ActivityData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database configuration
    private static final String DATABASE_NAME = "activity_db";
    private static final int DATABASE_VERSION = 5;  // Incremented version to apply schema changes
    private static final String TABLE_NAME = "activity_data";

    // Column names
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_DISTANCE_WALKED = "distance_walked";
    private static final String COL_DISTANCE_RUN = "distance_run";
    private static final String COL_DISTANCE_DRIVEN = "distance_driven";
    private static final String COL_CALORIES_BURNED = "calories_burned";  // Single column for calories burned

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_DISTANCE_WALKED + " REAL, " +
                COL_DISTANCE_RUN + " REAL, " +
                COL_DISTANCE_DRIVEN + " REAL, " +
                COL_CALORIES_BURNED + " REAL DEFAULT 0)";
        db.execSQL(createTable);
    }

    // Handling database upgrades
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            // Upgrade logic: Replace separate columns with a single calories_burned column
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_CALORIES_BURNED + " REAL DEFAULT 0;");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_CALORIES_BURNED + " = COALESCE(calories_walked, 0) + COALESCE(calories_run, 0);");
            db.execSQL("CREATE TEMP TABLE backup AS SELECT " + COL_ID + ", " + COL_DATE + ", " +
                    COL_DISTANCE_WALKED + ", " + COL_DISTANCE_RUN + ", " + COL_DISTANCE_DRIVEN + ", " + COL_CALORIES_BURNED + " FROM " + TABLE_NAME + ";");
            db.execSQL("DROP TABLE " + TABLE_NAME + ";");
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_DATE + " TEXT, " +
                    COL_DISTANCE_WALKED + " REAL, " +
                    COL_DISTANCE_RUN + " REAL, " +
                    COL_DISTANCE_DRIVEN + " REAL, " +
                    COL_CALORIES_BURNED + " REAL DEFAULT 0);");
            db.execSQL("INSERT INTO " + TABLE_NAME + " SELECT * FROM backup;");
            db.execSQL("DROP TABLE backup;");
        }
    }

    // Check if an entry exists for the given date
    public boolean doesEntryExist(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ?", new String[]{date});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Update an existing entry for the given date
    public void updateActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DISTANCE_WALKED, distanceWalked);
        values.put(COL_DISTANCE_RUN, distanceRun);
        values.put(COL_DISTANCE_DRIVEN, distanceDriven);
        values.put(COL_CALORIES_BURNED, caloriesBurned);  // Save total calories burned
        db.update(TABLE_NAME, values, COL_DATE + " = ?", new String[]{date});
    }

    // Insert a new activity entry
    public void insertActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, date);
        values.put(COL_DISTANCE_WALKED, distanceWalked);
        values.put(COL_DISTANCE_RUN, distanceRun);
        values.put(COL_DISTANCE_DRIVEN, distanceDriven);
        values.put(COL_CALORIES_BURNED, caloriesBurned);  // Save total calories burned
        db.insert(TABLE_NAME, null, values);
    }

    // Retrieve all activity data
    public List<ActivityData> getAllActivityData() {
        List<ActivityData> activityDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                float distanceWalked = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_WALKED));
                float distanceRun = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_RUN));
                float distanceDriven = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_DRIVEN));
                double caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CALORIES_BURNED));  // Load total calories burned

                // Create a new ActivityData object and add it to the list
                activityDataList.add(new ActivityData(id, date, distanceWalked, distanceRun, distanceDriven, caloriesBurned));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activityDataList;
    }

    // Retrieve activity data for a specific date
    public ActivityData getActivityDataForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
            float distanceWalked = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_WALKED));
            float distanceRun = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_RUN));
            float distanceDriven = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_DISTANCE_DRIVEN));
            double caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CALORIES_BURNED));  // Load total calories burned
            cursor.close();
            return new ActivityData(id, date, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        }
        cursor.close();
        return null;
    }
}
