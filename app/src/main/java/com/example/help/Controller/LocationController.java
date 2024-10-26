package com.example.help.Controller;

import android.location.Location;
import android.util.Log;

import com.example.help.Model.ActivityClassifier;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.example.help.ActivityUpdateListener;

public class LocationController extends LocationCallback {

    private static final String TAG = "LocationController";
    private ActivityClassifier activityClassifier;
    private ActivityUpdateListener activityUpdateListener;

    public LocationController(ActivityUpdateListener activityUpdateListener) {
        this.activityUpdateListener = activityUpdateListener;
        this.activityClassifier = new ActivityClassifier();
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            Log.d(TAG, "LocationResult is null");
            return;
        }

        for (Location location : locationResult.getLocations()) {
            float speed = location.getSpeed(); // Speed in meters/second
            Log.d(TAG, "Speed (m/s): " + speed);

            String activity = activityClassifier.classifyActivity(speed * 3.6f); // Convert to km/h
            Log.d(TAG, "Classified activity: " + activity);

            // Notify the listener (MainActivity) about the new activity classification
            activityUpdateListener.onActivityUpdate(activity);
        }
    }
}
