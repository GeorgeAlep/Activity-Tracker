package com.example.help.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;

import com.example.help.Database.DatabaseHelper;
import com.google.android.gms.location.LocationResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

class LocationControllerTrackingIntegrationTest {

    @Mock
    private DatabaseHelper mockDatabaseHelper;

    @Mock
    private LocationController.LocationControllerListener mockListener;

    @Mock
    private SensorManager mockSensorManager;

    @Mock
    private Sensor mockRotationSensor;

    private LocationController locationController;

    private static final double USER_WEIGHT_KG = 70.0;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock getDefaultSensor to return a mock sensor when requested
        when(mockSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)).thenReturn(mockRotationSensor);

        // Initialize LocationController with mocked SensorManager and other dependencies
        locationController = LocationController.getInstance(mockListener, mockDatabaseHelper, mockSensorManager, USER_WEIGHT_KG);
    }

    private LocationResult createLocationResult(Location location) {
        LocationResult locationResult = mock(LocationResult.class);
        when(locationResult.getLocations()).thenReturn(Collections.singletonList(location));
        return locationResult;
    }
}
