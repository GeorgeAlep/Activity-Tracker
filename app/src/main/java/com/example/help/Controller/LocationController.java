package com.example.help.Controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

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

public class LocationController extends LocationCallback implements SensorEventListener {

    private static final String TAG = Constants.LOCATION_CONTROLLER_TAG;  // Using a constant for logging
    private static LocationController instance;

    private final ActivityClassifier activityClassifier;
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

    public static synchronized LocationController getInstance(LocationControllerListener listener, DatabaseHelper databaseHelper, SensorManager sensorManager, double weightKg) {
        if (instance == null) {
            instance = new LocationController(listener, databaseHelper, sensorManager, weightKg);
        } else if (listener != null) {
            instance.listener = listener;  // Update listener if provided
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
            String activity = activityClassifier.classifyActivity(speed * Constants.MS_TO_KMH_CONVERSION);  // Convert speed to km/h
            Log.d(TAG, "Received location update. Speed: " + speed + " m/s, Activity: " + activity);

            if (lastLocation != null) {
                float distanceInMeters = location.distanceTo(lastLocation);
                float distanceInKm = distanceInMeters / Constants.METERS_IN_KM;
                Log.d(TAG, "Calculated distance: " + distanceInKm + " km between updates.");

                if (weightKg > 0) {
                    switch (activity) {
                        case "Walking":
                            distanceWalked += distanceInKm;
                            caloriesBurned += CalorieCalculator.calculateCaloriesBurned("Walking", weightKg, distanceInKm);
                            Log.d(TAG, "Updated walking distance: " + distanceWalked + " km, Total calories burned: " + caloriesBurned);
                            break;
                        case "Running":
                            distanceRun += distanceInKm;
                            caloriesBurned += CalorieCalculator.calculateCaloriesBurned("Running", weightKg, distanceInKm);
                            Log.d(TAG, "Updated running distance: " + distanceRun + " km, Total calories burned: " + caloriesBurned);
                            break;
                        case "Driving":
                            distanceDriven += distanceInKm;
                            Log.d(TAG, "Updated driving distance: " + distanceDriven + " km");
                            break;
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
        return new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.DEFAULT_LOCALE).format(new Date());
    }

    private void resetDailyValues() {
        distanceWalked = 0f;
        distanceRun = 0f;
        distanceDriven = 0f;
        caloriesBurned = 0;
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
        return Constants.getCardinalDirection(azimuth);
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
