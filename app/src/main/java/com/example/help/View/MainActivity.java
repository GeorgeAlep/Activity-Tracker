package com.example.help.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityClassifier;
import com.example.help.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";

    private TextView activityTextView;
    private TextView distanceWalkedTextView, distanceRunTextView, idleTimeTextView;
    private Button historyButton;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityClassifier activityClassifier;
    private MapView mapView;
    private Marker locationMarker;

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    private float currentAzimuth = 0f;
    private Location lastLocation;
    private float distanceWalked = 0f;
    private float distanceRun = 0f;
    private long idleStartTime = 0;
    private long totalIdleTime = 0;

    private DatabaseHelper databaseHelper; // Database helper to store the data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityTextView = findViewById(R.id.activityTextView);
        distanceWalkedTextView = findViewById(R.id.distanceWalkedTextView);
        distanceRunTextView = findViewById(R.id.distanceRunTextView);
        idleTimeTextView = findViewById(R.id.idleTimeTextView);
        historyButton = findViewById(R.id.historyButton);
        mapView = findViewById(R.id.osmMapView);

        // Database initialization
        databaseHelper = new DatabaseHelper(this);

        // osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Setup map controller
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(52.5200, 13.4050); // Default to Berlin
        mapController.setCenter(startPoint);

        // Initialize location marker
        locationMarker = new Marker(mapView);
        locationMarker.setTitle("You are here");

        // Set a properly scaled arrow icon
        Drawable arrowIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow);
        if (arrowIcon != null) {
            arrowIcon.setBounds(0, 0, arrowIcon.getIntrinsicWidth() / 2, arrowIcon.getIntrinsicHeight() / 2);
        }
        locationMarker.setIcon(arrowIcon);

        // Center the marker at the middle of the icon
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        mapView.getOverlays().add(locationMarker);

        // Initialize sensor manager for orientation
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        activityClassifier = new ActivityClassifier();

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        // Set up history button listener
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);  // Open the history activity
        });
    }

    // Start GPS location updates
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);  // Update interval: 10 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    // Location callback to receive GPS data and classify activity
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                float speed = location.getSpeed(); // Speed in meters/second
                Log.d(TAG, "Speed (m/s): " + speed);

                String activity = activityClassifier.classifyActivity(speed * 3.6f); // Convert to km/h
                Log.d(TAG, "Classified activity: " + activity);

                // Calculate distance covered
                if (lastLocation != null) {
                    float distanceInMeters = location.distanceTo(lastLocation);
                    if (activity.equals("Walking")) {
                        distanceWalked += distanceInMeters / 1000;  // Convert meters to kilometers
                    } else if (activity.equals("Running")) {
                        distanceRun += distanceInMeters / 1000;  // Convert meters to kilometers
                    }
                }
                lastLocation = location;

                // Update stats display
                updateView(activity);

                // Update the location marker position
                GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                updateMarkerPosition(currentLocation);
            }
        }
    };

    // Update the view with the current activity
    public void updateView(String activity) {
        Log.d(TAG, "Updating UI with activity: " + activity);

        if (activityTextView != null) {
            activityTextView.setText("Current Activity: " + activity);
        }

        if (activity.equals("Idle")) {
            if (idleStartTime == 0) {
                idleStartTime = System.currentTimeMillis();  // Start idle timer
            }
        } else {
            if (idleStartTime > 0) {
                totalIdleTime += System.currentTimeMillis() - idleStartTime;  // Add idle time
                idleStartTime = 0;  // Reset idle timer
            }
        }

        // Update the display with total distances and idle time
        distanceWalkedTextView.setText("Distance Walked: " + String.format("%.2f km", distanceWalked));
        distanceRunTextView.setText("Distance Run: " + String.format("%.2f km", distanceRun));

        // Convert total idle time from milliseconds to minutes
        long idleMinutes = (totalIdleTime / 1000) / 60;
        idleTimeTextView.setText("Idle Time: " + idleMinutes + " minutes");

        // Save data at the end of the day
        if (System.currentTimeMillis() % (24 * 60 * 60 * 1000) < 60000) { // Save near the end of the day
            saveData();
        }
    }

    // Save data to the database
    private void saveData() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseHelper.insertActivityData(currentDate, distanceWalked, distanceRun, totalIdleTime);
    }

    // Update the marker position on the map
    private void updateMarkerPosition(GeoPoint location) {
        if (locationMarker != null) {
            locationMarker.setPosition(location);
            locationMarker.setRotation(currentAzimuth); // Rotate marker to match phone's orientation
            mapView.getController().setCenter(location); // Center the map on the new location
            mapView.invalidate(); // Refresh the map
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Remap coordinate system if needed
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, adjustedRotationMatrix);

            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            // Get the azimuth (rotation around Z axis), convert it to degrees
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            currentAzimuth = (360 - azimuthInDegrees) % 360;  // Correct azimuth for map orientation

            // Update the marker's orientation
            if (locationMarker != null) {
                locationMarker.setRotation(currentAzimuth);
                mapView.invalidate();  // Redraw the map
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }

    // Handle location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mapView.onPause();  // osmdroid MapView pause
        sensorManager.unregisterListener(this);  // Unregister the sensor listener to save battery
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        mapView.onResume();  // osmdroid MapView resume
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
    }
}
