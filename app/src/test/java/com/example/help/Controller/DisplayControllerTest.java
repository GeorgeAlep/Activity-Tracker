package com.example.help.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.help.utils.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DisplayControllerTest {
    private Context mockContext;
    private SharedPreferences mockSharedPreferences;
    private SharedPreferences.Editor mockEditor;

    @BeforeEach
    void setUp() {
        // Mock the Context and SharedPreferences
        mockContext = Mockito.mock(Context.class);
        mockSharedPreferences = Mockito.mock(SharedPreferences.class);
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);

        // Mock behavior for SharedPreferences
        when(mockContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE))
                .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM))
                .thenReturn(true); // Default to kilometers
        when(mockSharedPreferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT))
                .thenReturn(70.0f);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
    }

    @Test
    void testIsUseKmWhenTrue() {
        // Set up DisplayController with mocked Context
        DisplayController displayController = new DisplayController(mockContext);

        // Assert that isUseKm() returns true
        assertEquals(true, displayController.isUseKm());
    }

    @Test
    void testIsUseKmWhenFalse() {
        // Mock SharedPreferences to return false for useKm
        when(mockSharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM))
                .thenReturn(false);

        // Set up DisplayController with mocked Context
        DisplayController displayController = new DisplayController(mockContext);

        // Assert that isUseKm() returns false
        assertEquals(false, displayController.isUseKm());
    }

    @Test
    void testGetFormattedDistanceInKm() {
        // Set up DisplayController with mocked Context
        DisplayController displayController = new DisplayController(mockContext);

        // Test getFormattedDistance for kilometers
        double distanceInKm = 5.0;
        String expectedOutput = "5.00 km";
        assertEquals(expectedOutput, displayController.getFormattedDistance(distanceInKm));
    }

    @Test
    void testGetFormattedDistanceInMiles() {
        // Mock SharedPreferences to return false for useKm
        when(mockSharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM))
                .thenReturn(false);

        // Set up DisplayController with mocked Context
        DisplayController displayController = new DisplayController(mockContext);

        // Test getFormattedDistance for miles
        double distanceInKm = 5.0;
        double expectedDistanceInMiles = distanceInKm * Constants.KM_TO_MILES;
        String expectedOutput = String.format("%.2f miles", expectedDistanceInMiles);
        assertEquals(expectedOutput, displayController.getFormattedDistance(distanceInKm));
    }
}
