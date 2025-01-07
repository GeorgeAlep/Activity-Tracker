package com.example.help.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.help.Model.ActivityData;
import com.example.help.utils.Constants;

/**
 * The DatabaseHelper class provides methods to interact with the SQLite database
 * for storing and retrieving activity data such as distances walked, run, driven,
 * and calories burned. It also handles database creation and version upgrades.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Constructor for the DatabaseHelper class.
     *
     * @param context The application context, used to create or open the database.
     */
    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Creates the table for storing activity data.
     *
     * @param db The SQLite database instance.
     */
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

    /**
     * Called when the database needs to be upgraded.
     * Handles table schema changes and data migration.
     *
     * @param db         The SQLite database instance.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < Constants.DATABASE_VERSION) {
            // Alter table to add a new column for calories burned
            db.execSQL("ALTER TABLE " + Constants.TABLE_NAME + " ADD COLUMN " + Constants.COL_CALORIES_BURNED + " REAL DEFAULT 0;");

            // Migrate data: Combine calories from walking and running
            db.execSQL("UPDATE " + Constants.TABLE_NAME + " SET " + Constants.COL_CALORIES_BURNED + " = COALESCE(calories_walked, 0) + COALESCE(calories_run, 0);");

            // Backup existing data into a temporary table
            db.execSQL("CREATE TEMP TABLE backup AS SELECT " +
                    Constants.COL_ID + ", " +
                    Constants.COL_DATE + ", " +
                    Constants.COL_DISTANCE_WALKED + ", " +
                    Constants.COL_DISTANCE_RUN + ", " +
                    Constants.COL_DISTANCE_DRIVEN + ", " +
                    Constants.COL_CALORIES_BURNED +
                    " FROM " + Constants.TABLE_NAME + ";");

            // Drop the old table
            db.execSQL("DROP TABLE " + Constants.TABLE_NAME + ";");

            // Recreate the table with the updated schema
            db.execSQL("CREATE TABLE " + Constants.TABLE_NAME + " (" +
                    Constants.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.COL_DATE + " TEXT, " +
                    Constants.COL_DISTANCE_WALKED + " REAL, " +
                    Constants.COL_DISTANCE_RUN + " REAL, " +
                    Constants.COL_DISTANCE_DRIVEN + " REAL, " +
                    Constants.COL_CALORIES_BURNED + " REAL DEFAULT 0);");

            // Restore data from the backup
            db.execSQL("INSERT INTO " + Constants.TABLE_NAME + " SELECT * FROM backup;");
            db.execSQL("DROP TABLE backup;");
        }
    }

    /**
     * Checks if an entry exists for a specific date.
     *
     * @param date The date to check for.
     * @return true if an entry exists, false otherwise.
     */
    public boolean doesEntryExist(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?", new String[]{date});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Updates an existing entry in the database for the specified date.
     *
     * @param date           The date of the entry to update.
     * @param distanceWalked The distance walked in km.
     * @param distanceRun    The distance run in km.
     * @param distanceDriven The distance driven in km.
     * @param caloriesBurned The calories burned.
     */
    public void updateActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_DISTANCE_WALKED, distanceWalked);
        values.put(Constants.COL_DISTANCE_RUN, distanceRun);
        values.put(Constants.COL_DISTANCE_DRIVEN, distanceDriven);
        values.put(Constants.COL_CALORIES_BURNED, caloriesBurned);
        db.update(Constants.TABLE_NAME, values, Constants.COL_DATE + " = ?", new String[]{date});
    }

    /**
     * Inserts a new activity entry into the database.
     *
     * @param date           The date of the activity.
     * @param distanceWalked The distance walked in km.
     * @param distanceRun    The distance run in km.
     * @param distanceDriven The distance driven in km.
     * @param caloriesBurned The calories burned.
     */
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

    /**
     * Retrieves activity data for a specific date from the database.
     *
     * @param date The date for which to retrieve the activity data.
     * @return An ActivityData object containing the activity data for the given date,
     *         or null if no data exists for that date.
     */
    public ActivityData getActivityDataForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?", new String[]{date});

        if (cursor.moveToFirst()) {
            float distanceWalked = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_WALKED));
            float distanceRun = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_RUN));
            float distanceDriven = cursor.getFloat(cursor.getColumnIndexOrThrow(Constants.COL_DISTANCE_DRIVEN));
            double caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(Constants.COL_CALORIES_BURNED));
            cursor.close();
            return new ActivityData(date, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        }
        cursor.close();
        return null;
    }
}
