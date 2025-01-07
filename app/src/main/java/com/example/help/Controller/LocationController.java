package com.example.help.Controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import androidx.annotation.NonNull;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityClassifier;
import com.example.help.Model.ActivityData;
import com.example.help.Model.CalorieCalculator;
import com.example.help.utils.Constants;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The LocationController class is responsible for handling location updates,
 * activity classification, distance tracking, calorie calculation, and user direction.
 * It also interfaces with a database to save and retrieve activity data.
 */
public class LocationController extends LocationCallback implements SensorEventListener {

    // Singleton instance of LocationController
    private static LocationController instance;

    // Components for activity classification and database interaction
    private final ActivityClassifier activityClassifier;
    private LocationControllerListener listener; // Listener to communicate with other parts of the app
    private Location lastLocation; // Tracks the last recorded location
    private float distanceWalked = 0f; // Distance walked in km
    private float distanceRun = 0f; // Distance run in km
    private float distanceDriven = 0f; // Distance driven in km
    private double caloriesBurned = 0; // Calories burned
    private final DatabaseHelper databaseHelper; // Database helper for saving/loading data
    private String lastSavedDate; // The last date data was saved
    private final double weightKg; // User's weight for calorie calculations

    /**
     * Private constructor to initialize LocationController components.
     *
     * @param listener       Listener to receive updates about activity, location, and direction.
     * @param databaseHelper Helper for database operations.
     * @param sensorManager  Manages sensor events for detecting device orientation.
     * @param weightKg       User's weight in kilograms for calorie calculations.
     */
    private LocationController(LocationControllerListener listener, DatabaseHelper databaseHelper, SensorManager sensorManager, double weightKg) {
        this.listener = listener;
        this.databaseHelper = databaseHelper;
        this.weightKg = weightKg;
        this.activityClassifier = new ActivityClassifier();
        this.lastSavedDate = getCurrentDate();

        loadSavedData(); // Load saved data for the current date

        // Register the rotation vector sensor to monitor device orientation
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Provides a singleton instance of LocationController.
     * If the instance doesn't exist, it creates one.
     *
     * @param listener       Listener to receive updates.
     * @param databaseHelper Database helper instance.
     * @param sensorManager  Sensor manager for registering sensor events.
     * @param weightKg       User's weight in kilograms.
     * @return The singleton instance of LocationController.
     */
    public static synchronized LocationController getInstance(LocationControllerListener listener, DatabaseHelper databaseHelper, SensorManager sensorManager, double weightKg) {
        if (instance == null) {
            instance = new LocationController(listener, databaseHelper, sensorManager, weightKg);
        } else if (listener != null) {
            instance.listener = listener; // Update the listener if it's not null
        }
        return instance;
    }

    /**
     * Called when location updates are received.
     *
     * @param locationResult Contains a list of new location updates.
     */
    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        String currentDate = getCurrentDate();
        if (!currentDate.equals(lastSavedDate)) {
            resetDailyValues(); // Reset daily values if the date has changed
            lastSavedDate = currentDate;
        }

        for (Location location : locationResult.getLocations()) {
            float speed = location.getSpeed();
            String activity = activityClassifier.classifyActivity(speed * Constants.MS_TO_KMH_CONVERSION); // Classify activity based on speed

            if (lastLocation != null) {
                float distanceInMeters = location.distanceTo(lastLocation);
                float distanceInKm = distanceInMeters / Constants.METERS_IN_KM;

                if (weightKg > 0) {
                    // Update distances and calories burned based on classified activity
                    switch (activity) {
                        case "Walking":
                            distanceWalked += distanceInKm;
                            caloriesBurned = CalorieCalculator.calculateCaloriesBurnedWalking();
                            break;
                        case "Running":
                            distanceRun += distanceInKm;
                            caloriesBurned = CalorieCalculator.calculateCaloriesBurnedRunning();
                            break;
                        case "Driving":
                            distanceDriven += distanceInKm;
                            break;
                    }
                }
            }

            lastLocation = location; // Update the last known location

            // Notify the listener with updated data
            if (listener != null) {
                listener.onActivityDataUpdated(activity, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                listener.onLocationUpdated(geoPoint);
            }

            saveData(); // Save updated data to the database
        }
    }

    /**
     * Retrieves the current date in the format specified by Constants.DATE_FORMAT_PATTERN.
     *
     * @return A string representing the current date.
     */
    String getCurrentDate() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.getDefault()).format(new Date());
    }

    /**
     * Resets daily tracking values (distances and calories).
     */
    void resetDailyValues() {
        distanceWalked = 0f;
        distanceRun = 0f;
        distanceDriven = 0f;
        caloriesBurned = 0;
    }

    /**
     * Saves activity data for the current date into the database.
     */
    private void saveData() {
        String currentDate = getCurrentDate();
        if (databaseHelper.doesEntryExist(currentDate)) {
            databaseHelper.updateActivityData(currentDate, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        } else {
            databaseHelper.insertActivityData(currentDate, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        }
    }

    /**
     * Loads saved data for the current date from the database.
     */
    private void loadSavedData() {
        String currentDate = getCurrentDate();
        ActivityData savedData = databaseHelper.getActivityDataForDate(currentDate);
        if (savedData != null) {
            distanceWalked = ActivityData.getDistanceWalked();
            distanceRun = ActivityData.getDistanceRun();
            distanceDriven = savedData.getDistanceDriven();
            caloriesBurned = savedData.getCaloriesBurned();
        }
    }

    /**
     * Called when the sensor's data changes.
     * Updates the direction based on the device's orientation.
     *
     * @param event The sensor event containing updated data.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360; // Normalize azimuth to [0, 360]

            String direction = calculateCardinalDirection(azimuth);
            if (listener != null) {
                listener.onDirectionChanged(direction);
            }
        }
    }

    /**
     * Calculates the cardinal direction (e.g., North, South) based on azimuth.
     *
     * @param azimuth The azimuth angle in degrees.
     * @return The corresponding cardinal direction.
     */
    private String calculateCardinalDirection(float azimuth) {
        return Constants.getCardinalDirection(azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Interface to define the callback methods for LocationController listeners.
     */
    public interface LocationControllerListener {
        void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned);
        void onLocationUpdated(GeoPoint location);
        void onDirectionChanged(String direction);
    }
}
