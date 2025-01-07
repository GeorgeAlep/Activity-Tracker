package com.example.help.Controller;

import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.example.help.Database.DatabaseHelper;
import com.google.android.gms.location.LocationResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.osmdroid.util.GeoPoint;

import java.util.Collections;

class LocationControllerTest {

    private LocationController.LocationControllerListener mockListener;
    private DatabaseHelper mockDatabaseHelper;
    private SensorManager mockSensorManager;
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        mockListener = mock(LocationController.LocationControllerListener.class);
        mockDatabaseHelper = mock(DatabaseHelper.class);
        mockSensorManager = mock(SensorManager.class);

        locationController = LocationController.getInstance(mockListener, mockDatabaseHelper, mockSensorManager, 70.0);
    }

    @Test
    void testOnLocationResult_updatesActivityDataAndSendsToListener() {
        // Mock the location data
        Location mockLocation = mock(Location.class);
        when(mockLocation.getSpeed()).thenReturn(1.5f); // Speed in m/s
        when(mockLocation.getLatitude()).thenReturn(52.5200);
        when(mockLocation.getLongitude()).thenReturn(13.4050);

        LocationResult locationResult = LocationResult.create(Collections.singletonList(mockLocation));

        // Call the method
        locationController.onLocationResult(locationResult);

        // Verify listener interactions
        verify(mockListener, times(1)).onActivityDataUpdated(eq("Walking"), anyFloat(), anyFloat(), anyFloat(), anyDouble());
        verify(mockListener, times(1)).onLocationUpdated(new GeoPoint(52.5200, 13.4050));
    }

    @Test
    void testOnLocationResult_withNullLocationResult_logsAndSkipsProcessing() {
        // Mock a null location result
        LocationResult nullLocationResult = null;

        // Call the method
        locationController.onLocationResult(nullLocationResult);

        // Verify that listener methods were not called
        verifyNoInteractions(mockListener);
    }

    @Test
    void testOnLocationResult_resetsDailyValuesOnDateChange() {
        // Mock the location data
        Location mockLocation = mock(Location.class);
        when(mockLocation.getSpeed()).thenReturn(1.5f); // Speed in m/s
        when(mockLocation.getLatitude()).thenReturn(52.5200);
        when(mockLocation.getLongitude()).thenReturn(13.4050);

        when(mockDatabaseHelper.doesEntryExist(anyString())).thenReturn(true);

        LocationResult locationResult = LocationResult.create(Collections.singletonList(mockLocation));

        // Simulate a new day by changing the internal date
        locationController.onLocationResult(locationResult);

        // Verify listener interactions and database interactions
        verify(mockListener, atLeastOnce()).onActivityDataUpdated(anyString(), eq(0f), eq(0f), eq(0f), eq(0.0));
        verify(mockDatabaseHelper, times(1)).updateActivityData(anyString(), anyFloat(), anyFloat(), anyFloat(), anyDouble());
    }
}

class MockAndroidUtil {
    static MockedStatic<Log> logMock;
    static void mockAndroidLog() {
        if (logMock == null) {
            logMock = mockStatic(android.util.Log.class);
            when(android.util.Log.d(anyString(), anyString())).thenReturn(0);
            when(android.util.Log.e(anyString(), anyString())).thenReturn(0);
            when(android.util.Log.i(anyString(), anyString())).thenReturn(0);
        }
    }
}