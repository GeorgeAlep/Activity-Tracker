package com.example.help.utils;

import java.util.Locale;

public final class Constants {

    // Prevent instantiation
    private Constants() {}

    // Activity speed constants
    public static final float SPEED_IDLE_THRESHOLD = 4f;
    public static final float SPEED_WALKING_THRESHOLD = 10f;
    public static final float SPEED_RUNNING_THRESHOLD = 20f;


    // General conversion constants
    public static final double KM_TO_MILES = 0.621371;
    public static final float MS_TO_KMH_CONVERSION = 3.6f;  // Meters per second to km/h
    public static final float METERS_IN_KM = 1000f;

    // SharedPreferences keys
    public static final String PREFS_NAME = "user_settings";
    public static final String KEY_WEIGHT = "user_weight";
    public static final String KEY_USE_KM = "use_km";

    // Default values
    public static final float DEFAULT_WEIGHT = 70.0f;
    public static final boolean DEFAULT_USE_KM = true;

    // Database constants
    public static final String DATABASE_NAME = "activity_db";
    public static final int DATABASE_VERSION = 5;
    public static final String TABLE_NAME = "activity_data";

    // Database column names
    public static final String COL_ID = "id";
    public static final String COL_DATE = "date";
    public static final String COL_DISTANCE_WALKED = "distance_walked";
    public static final String COL_DISTANCE_RUN = "distance_run";
    public static final String COL_DISTANCE_DRIVEN = "distance_driven";
    public static final String COL_CALORIES_BURNED = "calories_burned";

    // Notification Channel constants
    public static final String CHANNEL_ID = "location_channel";
    public static final String CHANNEL_NAME = "Location Tracking";

    // Calorie calculation rates (per kg per km)
    public static final double WALKING_CALORIE_BURN_RATE = 0.03;
    public static final double RUNNING_CALORIE_BURN_RATE = 0.06;

    // Location and UI constants
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final long LOCATION_UPDATE_INTERVAL = 10000L;  // in milliseconds
    public static final long LOCATION_FASTEST_UPDATE_INTERVAL = 2000L; // in milliseconds
    public static final float DEFAULT_ZOOM_LEVEL = 15.0f;

    // Date format pattern
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    // Cardinal directions (bounds for azimuth angles)
    public static final float DIRECTION_N_BOUND = 337.5f;
    public static final float DIRECTION_NE_BOUND = 22.5f;
    public static final float DIRECTION_E_BOUND = 67.5f;
    public static final float DIRECTION_SE_BOUND = 112.5f;
    public static final float DIRECTION_S_BOUND = 157.5f;
    public static final float DIRECTION_SW_BOUND = 202.5f;
    public static final float DIRECTION_W_BOUND = 247.5f;
    public static final float DIRECTION_NW_BOUND = 292.5f;

    // Utility method for determining cardinal direction
    public static String getCardinalDirection(float azimuth) {
        if (azimuth >= DIRECTION_N_BOUND || azimuth < DIRECTION_NE_BOUND) return "N";
        else if (azimuth >= DIRECTION_NE_BOUND && azimuth < DIRECTION_E_BOUND) return "NE";
        else if (azimuth >= DIRECTION_E_BOUND && azimuth < DIRECTION_SE_BOUND) return "E";
        else if (azimuth >= DIRECTION_SE_BOUND && azimuth < DIRECTION_S_BOUND) return "SE";
        else if (azimuth >= DIRECTION_S_BOUND && azimuth < DIRECTION_SW_BOUND) return "S";
        else if (azimuth >= DIRECTION_SW_BOUND && azimuth < DIRECTION_W_BOUND) return "SW";
        else if (azimuth >= DIRECTION_W_BOUND && azimuth < DIRECTION_NW_BOUND) return "W";
        else return "NW";
    }

    // Logging tags
    public static final String LOCATION_CONTROLLER_TAG = "LocationController";
}
