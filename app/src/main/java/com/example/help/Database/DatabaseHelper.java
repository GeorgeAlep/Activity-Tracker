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

    private static final String DATABASE_NAME = "activityData.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_NAME = "activity_data";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DISTANCE_WALKED = "distance_walked";
    private static final String COLUMN_DISTANCE_RUN = "distance_run";
    private static final String COLUMN_IDLE_TIME = "idle_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_DISTANCE_WALKED + " REAL, "
                + COLUMN_DISTANCE_RUN + " REAL, "
                + COLUMN_IDLE_TIME + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert new activity data
    public void insertActivityData(String date, float distanceWalked, float distanceRun, long idleTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DISTANCE_WALKED, distanceWalked);
        values.put(COLUMN_DISTANCE_RUN, distanceRun);
        values.put(COLUMN_IDLE_TIME, idleTime);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get all activity data
    public List<ActivityData> getAllActivityData() {
        List<ActivityData> activityDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                ActivityData activityData = new ActivityData(
                        cursor.getLong(0), // id
                        cursor.getString(1), // date
                        cursor.getFloat(2), // distance walked
                        cursor.getFloat(3), // distance run
                        cursor.getLong(4)  // idle time
                );
                activityDataList.add(activityData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return activityDataList;
    }
}
