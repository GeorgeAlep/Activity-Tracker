package com.example.help.utils;

/**
 * The Constants class provides a centralized location for all constant values used throughout the application.
 * This includes thresholds, conversion factors, database configurations, SharedPreferences keys,
 * notification settings, and utility methods.
 * <p>
 * The class is final to prevent inheritance, and the private constructor ensures it cannot be instantiated.
 */
public final class Constants {

    // Private constructor to prevent instantiation
    private Constants() {}

    // Activity speed thresholds (in km/h) for classifying user activities
    public static final float SPEED_IDLE_THRESHOLD = 0f;        // Speed below this is considered "Idle"
    public static final float SPEED_WALKING_THRESHOLD = 10f;    // Speed between idle and this is "Walking"
    public static final float SPEED_RUNNING_THRESHOLD = 20f;    // Speed between walking and this is "Running"

    // Conversion constants for units
    public static final double KM_TO_MILES = 0.621371;           // Kilometers to miles conversion factor
    public static final float MS_TO_KMH_CONVERSION = 3.6f;       // Meters per second to kilometers per hour
    public static final float METERS_IN_KM = 1000f;              // Number of meters in a kilometer

    // SharedPreferences keys for storing user settings
    public static final String PREFS_NAME = "user_settings";     // Name of the SharedPreferences file
    public static final String KEY_WEIGHT = "user_weight";       // Key for storing the user's weight
    public static final String KEY_USE_KM = "use_km";            // Key for storing the user's preferred distance unit

    // Default values for user settings
    public static final float DEFAULT_WEIGHT = 70.0f;            // Default weight in kilograms
    public static final boolean DEFAULT_USE_KM = true;           // Default distance unit (true for kilometers)

    // Database configuration constants
    public static final String DATABASE_NAME = "activity_db";    // Name of the SQLite database
    public static final int DATABASE_VERSION = 5;                // Current database version
    public static final String TABLE_NAME = "activity_data";     // Table name for activity data

    // Database column names
    public static final String COL_ID = "id";                    // Column for unique ID
    public static final String COL_DATE = "date";                // Column for date
    public static final String COL_DISTANCE_WALKED = "distance_walked";  // Column for distance walked
    public static final String COL_DISTANCE_RUN = "distance_run";        // Column for distance run
    public static final String COL_DISTANCE_DRIVEN = "distance_driven";  // Column for distance driven
    public static final String COL_CALORIES_BURNED = "calories_burned";  // Column for calories burned

    // Notification channel constants for foreground service
    public static final String CHANNEL_ID = "location_channel";  // Notification channel ID
    public static final String CHANNEL_NAME = "Location Tracking"; // Notification channel name

    // Calorie calculation rates (calories per kg per km)
    public static final double WALKING_CALORIE_BURN_RATE = 0.03; // Calorie burn rate for walking
    public static final double RUNNING_CALORIE_BURN_RATE = 0.06; // Calorie burn rate for running

    // Location tracking constants
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;       // Request code for location permissions
    public static final long LOCATION_UPDATE_INTERVAL = 10000L;         // Location update interval in milliseconds
    public static final long LOCATION_FASTEST_UPDATE_INTERVAL = 2000L; // Fastest location update interval
    public static final float DEFAULT_ZOOM_LEVEL = 15.0f;               // Default zoom level for maps

    // Date format pattern for displaying and storing dates
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";      // Date format (e.g., 2023-01-07)

    // Cardinal direction bounds for azimuth angles (used in compass calculations)
    public static final float DIRECTION_N_BOUND = 337.5f;   // Bound for North
    public static final float DIRECTION_NE_BOUND = 22.5f;   // Bound for North-East
    public static final float DIRECTION_E_BOUND = 67.5f;    // Bound for East
    public static final float DIRECTION_SE_BOUND = 112.5f;  // Bound for South-East
    public static final float DIRECTION_S_BOUND = 157.5f;   // Bound for South
    public static final float DIRECTION_SW_BOUND = 202.5f;  // Bound for South-West
    public static final float DIRECTION_W_BOUND = 247.5f;   // Bound for West
    public static final float DIRECTION_NW_BOUND = 292.5f;  // Bound for North-West

    /**
     * Determines the cardinal direction based on the azimuth angle.
     *
     * @param azimuth The azimuth angle in degrees (0 to 360).
     * @return A string representing the cardinal direction (e.g., "N", "NE").
     */
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
}
