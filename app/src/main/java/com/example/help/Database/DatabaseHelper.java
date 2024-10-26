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

    private static final String DATABASE_NAME = "activity_db";
    private static final String TABLE_NAME = "activity_data";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_DISTANCE_WALKED = "distance_walked";
    private static final String COL_DISTANCE_RUN = "distance_run";
    private static final String COL_DISTANCE_DRIVEN = "distance_driven";  // New column for distance driven

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_DISTANCE_WALKED + " REAL, " +
                COL_DISTANCE_RUN + " REAL, " +
                COL_DISTANCE_DRIVEN + " REAL)";  // Store distance driven as a float
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
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
    public void updateActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DISTANCE_WALKED, distanceWalked);
        values.put(COL_DISTANCE_RUN, distanceRun);
        values.put(COL_DISTANCE_DRIVEN, distanceDriven);  // Update distance driven as a float
        db.update(TABLE_NAME, values, COL_DATE + " = ?", new String[]{date});
    }

    // Insert a new activity entry
    public void insertActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, date);
        values.put(COL_DISTANCE_WALKED, distanceWalked);
        values.put(COL_DISTANCE_RUN, distanceRun);
        values.put(COL_DISTANCE_DRIVEN, distanceDriven);  // Insert distance driven as a float
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

                activityDataList.add(new ActivityData(id, date, distanceWalked, distanceRun, distanceDriven));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activityDataList;
    }
}
