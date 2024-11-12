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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity implements LocationController.LocationControllerListener, SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationController locationController;
    private MapView mapView;
    private Marker locationMarker;
    private IMapController mapController;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float currentAzimuth = 0f;

    private TextView activityTextView;
    private TextView distanceTextView;
    private TextView unitTextView;
    private TextView cardinalDirection;  // New TextView for cardinal direction
    private TextView caloriesTextView;
    private ImageView activityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView gifBackground = findViewById(R.id.gifBackground);

        // Load the GIF using Glide
        Glide.with(this).asGif().load(R.drawable.loading_circle).into(gifBackground);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        activityTextView = findViewById(R.id.activityTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        unitTextView = findViewById(R.id.unitTextView);
        activityIcon = findViewById(R.id.activityIcon);
        cardinalDirection = findViewById(R.id.cardinalDirection);
        caloriesTextView = findViewById(R.id.caloriesTextView);

        // Set up the map view
        mapView = findViewById(R.id.osmMapView);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Initialize the database helper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialize marker with custom icon
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

        // Sensor manager for rotation (azimuth)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);

        // Initialize location controller as singleton
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationController = LocationController.getInstance(this, databaseHelper, sensorManager, 70.0 // Replace with actual weight if needed
        );

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        // Start the LocationService as a foreground service
        Intent serviceIntent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        // Setup Floating Action Button for history
        FloatingActionButton historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Setup cardinal direction click animation
        cardinalDirection.setOnClickListener(v -> {
            ObjectAnimator rotate360 = ObjectAnimator.ofFloat(cardinalDirection, "rotation", 0f, 360f);
            rotate360.setDuration(300);
            rotate360.setInterpolator(new LinearInterpolator());

            ObjectAnimator rotate10 = ObjectAnimator.ofFloat(cardinalDirection, "rotation", 360f, 365f);
            rotate10.setDuration(40);
            rotate10.setInterpolator(new LinearInterpolator());

            ObjectAnimator rotate5 = ObjectAnimator.ofFloat(cardinalDirection, "rotation", 370f, 367f);
            rotate5.setDuration(20);
            rotate5.setInterpolator(new LinearInterpolator());

            ObjectAnimator rotateBack15 = ObjectAnimator.ofFloat(cardinalDirection, "rotation", 367f, 360f);
            rotateBack15.setDuration(200);
            rotateBack15.setInterpolator(new LinearInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(rotate360, rotate10, rotate5, rotateBack15);
            animatorSet.start();
        });

        // Setup Floating Action Button for settings
        FloatingActionButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // Create a bitmap from the drawable if it is not already a BitmapDrawable
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with location updates
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, getMainLooper());
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityDataUpdated(String activity, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        runOnUiThread(() -> {
            // Update the UI with real-time data from LocationController
            DisplayController displayController = new DisplayController(this);

            boolean useKm = displayController.isUseKm();
            String unit = useKm ? "km" : "miles";
            unitTextView.setText(unit);

            double displayedDistanceWalked = useKm ? distanceWalked : distanceWalked * 0.621371;
            double displayedDistanceRun = useKm ? distanceRun : distanceRun * 0.621371;
            double displayedDistanceDriven = useKm ? distanceDriven : distanceDriven * 0.621371;

            // Update activity and distance views based on current activity
            activityTextView.setText(activity);
            int iconResId;
            switch (activity) {
                case "Walking":
                    iconResId = R.drawable.ic_walk;
                    distanceTextView.setText(String.format("%.2f", displayedDistanceWalked));
                    break;
                case "Running":
                    iconResId = R.drawable.ic_run;
                    distanceTextView.setText(String.format("%.2f", displayedDistanceRun));
                    break;
                case "Driving":
                    iconResId = R.drawable.ic_drive;
                    distanceTextView.setText(String.format("%.2f", displayedDistanceDriven));
                    break;
                default:
                    iconResId = R.drawable.ic_stand;
                    distanceTextView.setText("");
                    break;
            }

            activityIcon.setImageResource(iconResId);

            // Update calories burned
            caloriesTextView.setText(String.format("%.0f kcal", caloriesBurned));
            caloriesTextView.setVisibility(View.VISIBLE);
        });
    }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, adjustedRotationMatrix);

            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            currentAzimuth = (360 - azimuthInDegrees) % 360;

            // Update marker rotation
            if (locationMarker != null) {
                locationMarker.setRotation(currentAzimuth);
                mapView.invalidate();
            }

            // Update the cardinal direction TextView
            onDirectionChanged(getCardinalDirection(currentAzimuth));
        }
    }

    private String getCardinalDirection(float azimuth) {
        if (azimuth >= 352.5 || azimuth < 37.5) return "N";
        else if (azimuth >= 37.5 && azimuth < 82.5) return "NE";
        else if (azimuth >= 82.5 && azimuth < 127.5) return "E";
        else if (azimuth >= 127.5 && azimuth < 172.5) return "SE";
        else if (azimuth >= 172.5 && azimuth < 217.5) return "S";
        else if (azimuth >= 217.5 && azimuth < 262.5) return "SW";
        else if (azimuth >= 262.5 && azimuth < 307.5) return "W";
        else return "NW";
    }

    // Implement the missing method from LocationControllerListener
    @Override
    public void onDirectionChanged(String direction) {
        runOnUiThread(() -> cardinalDirection.setText(direction));  // Update the TextView for cardinal direction
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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