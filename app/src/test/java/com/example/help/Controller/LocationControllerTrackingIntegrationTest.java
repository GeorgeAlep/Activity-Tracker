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

    @Test
    void testLocationUpdatesAndActivityClassification() {
        // Mock database behavior for updates
        when(mockDatabaseHelper.doesEntryExist(anyString())).thenReturn(true);

        // Mock location data with distances
        Location walkingLocation = mock(Location.class);
        when(walkingLocation.getSpeed()).thenReturn(1.0f); // Walking speed
        when(walkingLocation.distanceTo(any())).thenReturn(100.0f); // 100 meters

        Location runningLocation = mock(Location.class);
        when(runningLocation.getSpeed()).thenReturn(6.0f); // Running speed
        when(runningLocation.distanceTo(any())).thenReturn(200.0f); // 200 meters

        Location drivingLocation = mock(Location.class);
        when(drivingLocation.getSpeed()).thenReturn(20.0f); // Driving speed
        when(drivingLocation.distanceTo(any())).thenReturn(5000.0f); // 5 km

        // Trigger location updates
        locationController.onLocationResult(createLocationResult(walkingLocation));
        locationController.onLocationResult(createLocationResult(runningLocation));
        locationController.onLocationResult(createLocationResult(drivingLocation));

        // Verify listener received updates
        verify(mockListener, times(3)).onActivityDataUpdated(anyString(), anyFloat(), anyFloat(), anyFloat(), anyDouble());

        // Verify database update logic
        verify(mockDatabaseHelper, atLeastOnce()).updateActivityData(anyString(), anyFloat(), anyFloat(), anyFloat(), anyDouble());
    }


    private LocationResult createLocationResult(Location location) {
        LocationResult locationResult = mock(LocationResult.class);
        when(locationResult.getLocations()).thenReturn(Collections.singletonList(location));
        return locationResult;
    }
}
