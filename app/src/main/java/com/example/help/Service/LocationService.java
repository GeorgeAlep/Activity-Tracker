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

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationController locationController;
    private DatabaseHelper databaseHelper;
    private float userWeight;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the database helper and retrieve user weight from shared preferences
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        userWeight = preferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT);

        // Initialize SensorManager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Retrieve singleton instance of LocationController with null listener
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

        // Start service in the foreground to keep it active even when app is in the background
        startForeground(1, notification);
        startLocationUpdates();
    }

    // Create a notification channel for API level 26 and above
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

    // Start requesting location updates
    private void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL); // Use constant for interval
        locationRequest.setFastestInterval(Constants.LOCATION_FASTEST_UPDATE_INTERVAL); // Use constant for fastest interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check for location permissions before requesting updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, null);
        } else {
            // Stop the service if location permission is not granted
            stopSelf();
        }
    }

    // Service does not provide binding, so return null
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Clean up location updates when the service is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationController);
        }
    }
}
