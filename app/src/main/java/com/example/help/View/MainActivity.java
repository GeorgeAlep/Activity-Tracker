package com.example.help.View;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.help.Controller.DisplayController;
import com.example.help.Controller.LocationController;
import com.example.help.Database.DatabaseHelper;
import com.example.help.R;
import com.example.help.Service.LocationService;
import com.example.help.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

/**
 * MainActivity is the primary activity of the application.
 * It displays real-time activity, location, and direction data.
 * Features include a map, activity tracking, and animations for cardinal directions.
 */
public class MainActivity extends AppCompatActivity implements LocationController.LocationControllerListener, SensorEventListener {

    // Controllers and utilities
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationController locationController;
    private MapView mapView;
    private Marker locationMarker;
    private IMapController mapController;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float currentAzimuth = 0f;

    // UI components
    private TextView activityTextView;
    private TextView distanceTextView;
    private TextView unitTextView;
    private TextView cardinalDirection;
    private TextView caloriesTextView;
    private ImageView activityIcon;

    /**
     * Called when the activity is created.
     * Initializes UI components, controllers, and starts location and sensor services.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Load a GIF background
        ImageView gifBackground = findViewById(R.id.gifBackground);
        Glide.with(this).asGif().load(R.drawable.loading_circle).into(gifBackground);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        activityTextView = findViewById(R.id.activityTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        unitTextView = findViewById(R.id.unitTextView);
        activityIcon = findViewById(R.id.activityIcon);
        cardinalDirection = findViewById(R.id.cardinalDirection);
        caloriesTextView = findViewById(R.id.caloriesTextView);

        // Setup map view with initial settings
        mapView = findViewById(R.id.osmMapView);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(Constants.DEFAULT_ZOOM_LEVEL);

        // Database and osmdroid configurations
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Create a marker with a custom icon
        locationMarker = new Marker(mapView);
        locationMarker.setTitle("You are here");
        Drawable arrowIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow);
        if (arrowIcon != null) {
            Bitmap bitmap = drawableToBitmap(arrowIcon);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 10, 10, false);
            locationMarker.setIcon(new BitmapDrawable(getResources(), scaledBitmap));
        }
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mapView.getOverlays().add(locationMarker);

        // Initialize sensor manager for rotation vector
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);

        // Initialize LocationController
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationController = LocationController.getInstance(this, databaseHelper, sensorManager, Constants.DEFAULT_WEIGHT);

        // Start LocationService as a foreground service
        Intent serviceIntent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        // Setup history button
        FloatingActionButton historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Setup animation for cardinal direction on click
        setupCardinalDirectionAnimation();

        // Setup settings button
        FloatingActionButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handles permissions for location access.
     *
     * @param requestCode  Request code for permissions.
     * @param permissions  Array of requested permissions.
     * @param grantResults Results of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied. App functionality may be limited.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Converts a drawable resource to a bitmap.
     *
     * @param drawable Drawable resource to convert.
     * @return Converted bitmap.
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Sets up click animation for the cardinal direction text.
     */
    private void setupCardinalDirectionAnimation() {
        cardinalDirection.setOnClickListener(v -> {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(
                    ObjectAnimator.ofFloat(cardinalDirection, "rotation", 0f, 360f).setDuration(300),
                    ObjectAnimator.ofFloat(cardinalDirection, "rotation", 360f, 365f).setDuration(40),
                    ObjectAnimator.ofFloat(cardinalDirection, "rotation", 370f, 367f).setDuration(20),
                    ObjectAnimator.ofFloat(cardinalDirection, "rotation", 367f, 360f).setDuration(200)
            );
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.start();
        });
    }

    /**
     * Starts location updates using FusedLocationProviderClient.
     */
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                Constants.LOCATION_UPDATE_INTERVAL
        )
                .setMinUpdateIntervalMillis(Constants.LOCATION_FASTEST_UPDATE_INTERVAL)
                .build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Updates UI with activity data.
     */
    @Override
    public void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        runOnUiThread(() -> {
            DisplayController displayController = new DisplayController(this);
            boolean useKm = displayController.isUseKm();
            String unit = useKm ? "km" : "miles";
            unitTextView.setText(unit);

            double displayedDistanceWalked = useKm ? distanceWalked : distanceWalked * Constants.KM_TO_MILES;
            double displayedDistanceRun = useKm ? distanceRun : distanceRun * Constants.KM_TO_MILES;
            double displayedDistanceDriven = useKm ? distanceDriven : distanceDriven * Constants.KM_TO_MILES;

            activityTextView.setText(activity);
            int iconResId;
            switch (activity) {
                case "Walking":
                    iconResId = R.drawable.ic_walk;
                    distanceTextView.setText(String.format(Locale.getDefault(), "%.2f", displayedDistanceWalked));
                    break;
                case "Running":
                    iconResId = R.drawable.ic_run;
                    distanceTextView.setText(String.format(Locale.getDefault(), "%.2f", displayedDistanceRun));
                    break;
                case "Driving":
                    iconResId = R.drawable.ic_drive;
                    distanceTextView.setText(String.format(Locale.getDefault(), "%.2f", displayedDistanceDriven));
                    break;
                default:
                    iconResId = R.drawable.ic_stand;
                    distanceTextView.setText("");
                    unitTextView.setText("");
                    break;
            }

            activityIcon.setImageResource(iconResId);
            caloriesTextView.setText(String.format(Locale.getDefault(), "%.0f kcal", caloriesBurned));
            caloriesTextView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Updates the location marker on the map.
     */
    @Override
    public void onLocationUpdated(GeoPoint location) {
        runOnUiThread(() -> {
            if (locationMarker != null) {
                locationMarker.setPosition(location);
                locationMarker.setRotation(currentAzimuth);
                mapController.setCenter(location);
                mapView.invalidate();
            }
        });
    }

    /**
     * Updates the displayed cardinal direction based on sensor data.
     */
    @Override
    public void onDirectionChanged(String direction) {
        runOnUiThread(() -> cardinalDirection.setText(direction));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, adjustedRotationMatrix);

            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            float azimuthInDegrees = (float) Math.toDegrees(orientation[0]);
            currentAzimuth = (360 - azimuthInDegrees) % 360;

            String direction = Constants.getCardinalDirection(currentAzimuth);
            onDirectionChanged(direction);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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