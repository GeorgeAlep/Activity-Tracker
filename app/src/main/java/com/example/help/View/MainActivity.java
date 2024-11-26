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
import com.example.help.utils.Constants;
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
    private TextView cardinalDirection;
    private TextView caloriesTextView;
    private ImageView activityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView gifBackground = findViewById(R.id.gifBackground);
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
        mapController.setZoom(Constants.DEFAULT_ZOOM_LEVEL);

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
        locationController = LocationController.getInstance(this, databaseHelper, sensorManager, Constants.DEFAULT_WEIGHT);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
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
        setupCardinalDirectionAnimation();

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
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

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

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationController, getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

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
                    unitTextView.setText("");
                    break;
            }

            activityIcon.setImageResource(iconResId);
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
            onDirectionChanged(direction);  // Update direction display
        }
    }


    @Override
    public void onDirectionChanged(String direction) {
        runOnUiThread(() -> cardinalDirection.setText(direction));
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
