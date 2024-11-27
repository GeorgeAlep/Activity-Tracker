package com.example.help.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSettingsTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        float weight = 75.5f;
        boolean useKm = true;

        // Act
        UserSettings userSettings = new UserSettings(weight, useKm);

        // Assert
        assertEquals(weight, userSettings.getWeight());
        assertTrue(userSettings.isUseKm());
    }

    @Test
    void testSetWeight() {
        // Arrange
        UserSettings userSettings = new UserSettings(70.0f, false);

        // Act
        userSettings.setWeight(80.0f);

        // Assert
        assertEquals(80.0f, userSettings.getWeight());
    }

    @Test
    void testSetUseKm() {
        // Arrange
        UserSettings userSettings = new UserSettings(65.0f, false);

        // Act
        userSettings.setUseKm(true);

        // Assert
        assertTrue(userSettings.isUseKm());
    }
}
