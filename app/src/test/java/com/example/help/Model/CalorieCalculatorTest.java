package com.example.help.Model;

import static com.example.help.utils.Constants.RUNNING_CALORIE_BURN_RATE;
import static com.example.help.utils.Constants.WALKING_CALORIE_BURN_RATE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CalorieCalculatorTest {

    @Test
    void testCalculateCaloriesBurnedWalking() {
        // Constants
        final double weightKg = 70.0; // Example weight in kg
        final double distanceKm = 5.0; // Example distance in km

        // Expected calculation
        double expectedCaloriesBurned = WALKING_CALORIE_BURN_RATE * weightKg * distanceKm;

        // Perform calculation
        double actualCaloriesBurned = CalorieCalculator.calculateCaloriesBurnedWalking();

        // Assert the result
        Assertions.assertEquals(expectedCaloriesBurned, actualCaloriesBurned, 0.01, "Calories burned for walking did not match expected value");
    }

    @Test
    void testCalculateCaloriesBurnedRunning() {
        // Constants
        final double weightKg = 70.0; // Example weight in kg
        final double distanceKm = 5.0; // Example distance in km

        // Expected calculation
        double expectedCaloriesBurned = RUNNING_CALORIE_BURN_RATE * weightKg * distanceKm;

        // Perform calculation
        double actualCaloriesBurned = CalorieCalculator.calculateCaloriesBurnedRunning();

        // Assert the result
        Assertions.assertEquals(expectedCaloriesBurned, actualCaloriesBurned, 0.01, "Calories burned for running did not match expected value");
    }
}
