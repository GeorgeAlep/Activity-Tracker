package com.example.help.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.help.Model.ActivityData;
import com.example.help.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    // Creating the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + Constants.TABLE_NAME + " (" +
                Constants.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.COL_DATE + " TEXT, " +
                Constants.COL_DISTANCE_WALKED + " REAL, " +
                Constants.COL_DISTANCE_RUN + " REAL, " +
                Constants.COL_DISTANCE_DRIVEN + " REAL, " +
                Constants.COL_CALORIES_BURNED + " REAL DEFAULT 0)";
        db.execSQL(createTable);
    }

    // Handling database upgrades
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < Constants.DATABASE_VERSION) {
            // Upgrade logic: Add new column for calories burned and update data
            db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN " + Constants.COL_CALORIES_BURNED + " REAL DEFAULT 0;");
            db.execSQL("UPDATE " + Constants.TABLE_NAME + " SET " + Constants.COL_CALORIES_BURNED + " = COALESCE(calories_walked, 0) + COALESCE(calories_run, 0);");

            db.execSQL("CREATE TEMP TABLE backup AS SELECT " +
                    Constants.COL_ID + ", " +
                    Constants.COL_DATE + ", " +
                    Constants.COL_DISTANCE_WALKED + ", " +
                    Constants.COL_DISTANCE_RUN + ", " +
                    Constants.COL_DISTANCE_DRIVEN + ", " +
                    Constants.COL_CALORIES_BURNED +
                    " FROM " + Constants.TABLE_NAME + ";");

            db.execSQL("DROP TABLE " + Constants.TABLE_NAME + ";");

            db.execSQL("CREATE TABLE " + Constants.TABLE_NAME + " (" +
                    Constants.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COL_DATE + " TEXT, " +
                    Constants.COL_DISTANCE_WALKED + " REAL, " +
                    Constants.COL_DISTANCE_RUN + " REAL, " +
                    Constants.COL_DISTANCE_DRIVEN + " REAL, " +
                    Constants.COL_CALORIES_BURNED + " REAL DEFAULT 0);");

            db.execSQL("INSERT INTO " + Constants.TABLE_NAME + " SELECT * FROM backup;");
            db.execSQL("DROP TABLE backup;");
        }
    }

    // Check if an entry exists for the given date
    public boolean doesEntryExist(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?", new String[]{date});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Update an existing entry for the given date
    public void updateActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_DISTANCE_WALKED, distanceWalked);
        values.put(Constants.COL_DISTANCE_RUN, distanceRun);
        values.put(Constants.COL_DISTANCE_DRIVEN, distanceDriven);
        values.put(Constants.COL_CALORIES_BURNED, caloriesBurned);
        db.update(Constants.TABLE_NAME, values, Constants.COL_DATE + " = ?", new String[]{date});
    }

    // Insert a new activity entry
    public void insertActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_DATE, date);
        values.put(Constants.COL_DISTANCE_WALKED, distanceWalked);
        values.put(Constants.COL_DISTANCE_RUN, distanceRun);
        values.put(Constants.COL_DISTANCE_DRIVEN, distanceDriven);
        values.put(Constants.COL_CALORIES_BURNED, caloriesBurned);
        db.insert(Constants.TABLE_NAME, null, values);
    }

    // Retrieve all activity data
    public List<ActivityData> getAllActivityData() {
        List<ActivityData> activityDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COL_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_DATE));
                float distanceWalked = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_WALKED));
                float distanceRun = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_RUN));
                float distanceDriven = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_DRIVEN));
                double caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COL_CALORIES_BURNED));

                activityDataList.add(new ActivityData(id, date, distanceWalked, distanceRun, distanceDriven, caloriesBurned));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activityDataList;
    }

    // Retrieve activity data for a specific date
    public ActivityData getActivityDataForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.COL_ID));
            float distanceWalked = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_WALKED));
            float distanceRun = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_RUN));
            float distanceDriven = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_DRIVEN));
            double caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COL_CALORIES_BURNED));
            cursor.close();
            return new ActivityData(id, date, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        }
        cursor.close();
        return null;
    }
}
