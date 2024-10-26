package com.example.help.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.help.Controller.LocationController;
import com.example.help.Database.DatabaseHelper;
import com.example.help.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.api.IMapController;

public class MainActivity extends AppCompatActivity implements LocationController.LocationControllerListener, SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView activityTextView, distanceWalkedTextView, distanceRunTextView, idleTimeTextView;
    private Button historyButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationController locationController;
    private MapView mapView;
    private Marker locationMarker;
    private IMapController mapController;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float currentAzimuth = 0f;  // To track the current azimuth
    private DatabaseHelper databaseHelper;  // Database Helper (Model)

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

        // Initialize the database helper (Model)
        databaseHelper = new DatabaseHelper(this);  // Pass to Controller later

        // osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize the map controller and set a default zoom level
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Initialize marker with custom icon
        locationMarker = new Marker(mapView);
        locationMarker.setTitle("You are here");

        // Set a custom marker icon (like an arrow)
        Drawable arrowIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow);
        if (arrowIcon != null) {
            arrowIcon.setBounds(0, 0, arrowIcon.getIntrinsicWidth() / 2, arrowIcon.getIntrinsicHeight() / 2);
            locationMarker.setIcon(arrowIcon);
        }

        // Center the marker icon
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mapView.getOverlays().add(locationMarker);

        // Sensor manager for rotation (azimuth)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationController = new LocationController(this, databaseHelper);  // Pass DatabaseHelper to Controller

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);  // Update interval: 10 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, getMainLooper());
    }

    @Override
    public void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven) {
        runOnUiThread(() -> {
            activityTextView.setText("Current Activity: " + activity);
            distanceWalkedTextView.setText("Distance Walked: " + String.format("%.2f km", distanceWalked));
            distanceRunTextView.setText("Distance Run: " + String.format("%.2f km", distanceRun));
            idleTimeTextView.setText("Distance Driven: " + String.format("%.2f km", distanceDriven));  // Update this label to display distance driven
        });
    }


    @Override
    public void onLocationUpdated(GeoPoint location) {
        runOnUiThread(() -> {
            if (locationMarker != null) {
                locationMarker.setPosition(location);
                locationMarker.setRotation(currentAzimuth);  // Rotate the marker to match device orientation
                mapController.setCenter(location);  // Center map on new location
                mapView.invalidate();  // Refresh the map
            }
        });
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

            // Get the azimuth (rotation around Z axis), convert to degrees
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            currentAzimuth = (360 - azimuthInDegrees) % 360;  // Correct azimuth for map orientation

            // Update marker rotation
            if (locationMarker != null) {
                locationMarker.setRotation(currentAzimuth);
                mapView.invalidate();  // Redraw the map
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed here
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationController);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
    }
}
