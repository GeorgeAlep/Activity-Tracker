package com.example.help.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.help.Controller.LocationController;
import com.example.help.Database.DatabaseHelper;
import com.example.help.R;
import com.example.help.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * The LocationService class is a foreground service that manages location updates
 * and sensor data for the app. It ensures that tracking continues even when the
 * app is in the background.
 */
public class LocationService extends Service {

    // Handles location updates using the fused location provider
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Singleton instance of LocationController to manage location-related logic
    private LocationController locationController;

    /**
     * Called when the service is created.
     * Initializes location tracking, the LocationController, and the foreground service.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the database helper and retrieve user weight from shared preferences
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        float userWeight = preferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT);

        // Initialize SensorManager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Retrieve singleton instance of LocationController with a null listener
        locationController = LocationController.getInstance(
                null, databaseHelper, sensorManager, userWeight
        );

        // Create a notification for the foreground service
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle("Tracking your location")
                .setContentText("App is running in the background")
                .setSmallIcon(R.drawable.ic_location)
                .build();

        // Start the service in the foreground
        startForeground(1, notification);

        // Begin location updates
        startLocationUpdates();
    }

    /**
     * Creates a notification channel for the foreground service.
     * Required for API level 26 (Android Oreo) and above.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Starts requesting location updates using the fused location provider.
     * Checks for location permissions and stops the service if permissions are missing.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "Location permission not granted. Stopping service.");
            stopSelf(); // Stop the service if location permission is not granted
            return;
        }

        // Initialize the fused location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Create the LocationRequest with desired settings
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, // High accuracy for location tracking
                Constants.LOCATION_UPDATE_INTERVAL // Interval between location updates
        )
                .setMinUpdateIntervalMillis(Constants.LOCATION_FASTEST_UPDATE_INTERVAL) // Minimum interval for updates
                .build();

        // Request location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, getMainLooper());
    }

    /**
     * Called when another component binds to the service.
     * This service does not provide binding, so null is returned.
     *
     * @param intent The intent used to bind to the service.
     * @return Always returns null as this service is not bound.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is destroyed.
     * Cleans up resources by stopping location updates.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationController);
        }
    }
}
