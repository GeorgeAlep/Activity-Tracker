package com.example.help.Controller;

import com.example.help.Model.UserSettings;
import com.example.help.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.content.SharedPreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SettingsControllerTest {

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    private SettingsController settingsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE))
                .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        settingsController = new SettingsController(mockContext);
    }

    @Test
    void testGetUserSettings() {
        // Arrange
        when(mockSharedPreferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT))
                .thenReturn(70.0f);
        when(mockSharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM))
                .thenReturn(true);

        // Act
        UserSettings settings = settingsController.getUserSettings();

        // Assert
        assertEquals(70.0f, settings.getWeight());
        assertEquals(true, settings.isUseKm());
    }

    @Test
    void testSaveUserSettings() {
        // Arrange
        UserSettings settings = new UserSettings(80.0f, false);
        when(mockEditor.putFloat(Constants.KEY_WEIGHT, settings.getWeight())).thenReturn(mockEditor);
        when(mockEditor.putBoolean(Constants.KEY_USE_KM, settings.isUseKm())).thenReturn(mockEditor);
        doNothing().when(mockEditor).apply();

        // Act
        settingsController.saveUserSettings(settings);

        // Assert
        verify(mockEditor).putFloat(Constants.KEY_WEIGHT, 80.0f);
        verify(mockEditor).putBoolean(Constants.KEY_USE_KM, false);
        verify(mockEditor).apply();
    }
}
