package com.example.help.Controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityClassifier;
import com.example.help.Model.CalorieCalculator;
import com.example.help.Model.ActivityData;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationController extends LocationCallback implements SensorEventListener {

    private static final String TAG = "LocationController";
    private static LocationController instance; // Singleton instance

    private ActivityClassifier activityClassifier;
    private LocationControllerListener listener;
    private Location lastLocation;
    private float distanceWalked = 0f;
    private float distanceRun = 0f;
    private float distanceDriven = 0f;
    private double caloriesBurned = 0;
    private DatabaseHelper databaseHelper;
    private String lastSavedDate;
    private double weightKg;

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    // Private constructor to prevent direct instantiation
    private LocationController(LocationControllerListener listener, DatabaseHelper databaseHelper, SensorManager sensorManager, double weightKg) {
        this.listener = listener;
        this.databaseHelper = databaseHelper;
        this.sensorManager = sensorManager;
        this.weightKg = weightKg;
        this.activityClassifier = new ActivityClassifier();
        this.lastSavedDate = getCurrentDate();

        loadSavedData();

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // Singleton instance getter method
    public static synchronized LocationController getInstance(LocationControllerListener listener, DatabaseHelper databaseHelper, SensorManager sensorManager, double weightKg) {
        if (instance == null) {
            instance = new LocationController(listener, databaseHelper, sensorManager, weightKg);
        } else if (listener != null) {
            instance.listener = listener; // Update listener if provided
        }
        return instance;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            Log.d(TAG, "LocationResult is null");
            return;
        }

        String currentDate = getCurrentDate();
        if (!currentDate.equals(lastSavedDate)) {
            resetDailyValues();
            lastSavedDate = currentDate;
            Log.d(TAG, "Date changed, resetting daily values.");
        }

        for (Location location : locationResult.getLocations()) {
            float speed = location.getSpeed();
            String activity = activityClassifier.classifyActivity(speed * 3.6f); // Convert speed to km/h
            Log.d(TAG, "Received location update. Speed: " + speed + " m/s, Activity: " + activity);

            if (lastLocation != null) {
                float distanceInMeters = location.distanceTo(lastLocation);
                float distanceInKm = distanceInMeters / 1000;
                Log.d(TAG, "Calculated distance: " + distanceInKm + " km between updates.");

                if (weightKg > 0) {
                    if (activity.equals("Walking")) {
                        distanceWalked += distanceInKm;
                        caloriesBurned += CalorieCalculator.calculateCaloriesBurned("Walking", weightKg, distanceInKm);
                        Log.d(TAG, "Updated walking distance: " + distanceWalked + " km, Total calories burned: " + caloriesBurned);
                    } else if (activity.equals("Running")) {
                        distanceRun += distanceInKm;
                        caloriesBurned += CalorieCalculator.calculateCaloriesBurned("Running", weightKg, distanceInKm);
                        Log.d(TAG, "Updated running distance: " + distanceRun + " km, Total calories burned: " + caloriesBurned);
                    } else if (activity.equals("Driving")) {
                        distanceDriven += distanceInKm;
                        Log.d(TAG, "Updated driving distance: " + distanceDriven + " km");
                    }
                }
            }

            lastLocation = location;

            if (listener != null) {
                listener.onActivityDataUpdated(activity, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                listener.onLocationUpdated(geoPoint);
                Log.d(TAG, "Activity data and location updated to listener.");
            } else {
                Log.e(TAG, "LocationControllerListener is null");
            }

            saveData();
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void resetDailyValues() {
        distanceWalked = 0f;
        distanceRun = 0f;
        distanceDriven = 0f;
        caloriesBurned = 0; // Reset the consolidated calorie count
        Log.d(TAG, "Values reset for the new day.");
    }

    private void saveData() {
        String currentDate = getCurrentDate();
        if (databaseHelper.doesEntryExist(currentDate)) {
            databaseHelper.updateActivityData(currentDate, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        } else {
            databaseHelper.insertActivityData(currentDate, distanceWalked, distanceRun, distanceDriven, caloriesBurned);
        }
    }

    private void loadSavedData() {
        String currentDate = getCurrentDate();
        ActivityData savedData = databaseHelper.getActivityDataForDate(currentDate);
        if (savedData != null) {
            distanceWalked = savedData.getDistanceWalked();
            distanceRun = savedData.getDistanceRun();
            distanceDriven = savedData.getDistanceDriven();
            caloriesBurned = savedData.getCaloriesBurned();
            Log.d(TAG, "Loaded saved data for today: Walked = " + distanceWalked + " km, Run = " + distanceRun + " km, Driven = " + distanceDriven + " km, Total Calories = " + caloriesBurned);
        } else {
            Log.d(TAG, "No saved data for today.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360;

            String direction = calculateCardinalDirection(azimuth);
            if (listener != null) {
                listener.onDirectionChanged(direction);
            }
        }
    }

    private String calculateCardinalDirection(float azimuth) {
        if (azimuth >= 337.5 || azimuth < 22.5) return "N";
        else if (azimuth >= 22.5 && azimuth < 67.5) return "NE";
        else if (azimuth >= 67.5 && azimuth < 112.5) return "E";
        else if (azimuth >= 112.5 && azimuth < 157.5) return "SE";
        else if (azimuth >= 157.5 && azimuth < 202.5) return "S";
        else if (azimuth >= 202.5 && azimuth < 247.5) return "SW";
        else if (azimuth >= 247.5 && azimuth < 292.5) return "W";
        else return "NW";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    public interface LocationControllerListener {
        void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned);
        void onLocationUpdated(GeoPoint location);
        void onDirectionChanged(String direction);
    }
}
