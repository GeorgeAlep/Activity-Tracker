package com.example.help.Controller;

import android.location.Location;
import android.util.Log;
import com.example.help.Model.ActivityClassifier;
import com.example.help.Database.DatabaseHelper;
import com.example.help.View.MainActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationController extends LocationCallback {

    private static final String TAG = "LocationController";
    private ActivityClassifier activityClassifier;
    private LocationControllerListener listener;
    private Location lastLocation;
    private float distanceWalked = 0f;
    private float distanceRun = 0f;
    private float distanceDriven = 0f;  // New variable to track distance driven
    private DatabaseHelper databaseHelper;
    private String lastSavedDate;  // Track the last date when data was saved

    public LocationController(LocationControllerListener listener, DatabaseHelper databaseHelper) {
        this.listener = listener;
        this.databaseHelper = databaseHelper;
        this.activityClassifier = new ActivityClassifier();
        this.lastSavedDate = getCurrentDate();  // Initialize with the current date
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            Log.d(TAG, "LocationResult is null");
            return;
        }

        // Check if a new day has started
        String currentDate = getCurrentDate();
        if (!currentDate.equals(lastSavedDate)) {
            resetDailyValues();  // Reset values when the day changes
            lastSavedDate = currentDate;  // Update the last saved date
        }

        for (Location location : locationResult.getLocations()) {
            float speed = location.getSpeed();
            Log.d(TAG, "Speed (m/s): " + speed);

            // Classify the activity based on speed
            String activity = activityClassifier.classifyActivity(speed * 3.6f);  // Convert to km/h
            Log.d(TAG, "Classified activity: " + activity);

            // Calculate distance covered
            if (lastLocation != null) {
                float distanceInMeters = location.distanceTo(lastLocation);
                if (activity.equals("Walking")) {
                    distanceWalked += distanceInMeters / 1000;  // Convert meters to kilometers
                } else if (activity.equals("Running")) {
                    distanceRun += distanceInMeters / 1000;  // Convert meters to kilometers
                } else if (activity.equals("Driving")) {
                    distanceDriven += distanceInMeters / 1000;  // Convert meters to kilometers
                }
            }

            lastLocation = location;

            // Update the View with the new activity data through the listener
            listener.onActivityDataUpdated(activity, distanceWalked, distanceRun, distanceDriven);

            // Update the map with the new location
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            listener.onLocationUpdated(geoPoint);

            // Save the data after each significant update
            saveData();
        }
    }

    // Helper method to get the current date
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Reset the activity data when a new day starts
    private void resetDailyValues() {
        distanceWalked = 0f;
        distanceRun = 0f;
        distanceDriven = 0f;  // Reset distance driven
        Log.d(TAG, "Values reset for the new day.");
    }

    // Save the data into the database
    private void saveData() {
        String currentDate = getCurrentDate();
        if (databaseHelper.doesEntryExist(currentDate)) {
            // If entry exists, update it
            databaseHelper.updateActivityData(currentDate, distanceWalked, distanceRun, distanceDriven);
        } else {
            // Otherwise, insert a new entry
            databaseHelper.insertActivityData(currentDate, distanceWalked, distanceRun, distanceDriven);
        }
    }

    public interface LocationControllerListener {
        void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven);
        void onLocationUpdated(GeoPoint location);
    }
}
