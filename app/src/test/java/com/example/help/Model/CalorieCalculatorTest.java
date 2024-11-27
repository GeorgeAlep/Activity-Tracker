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
        double actualCaloriesBurned = CalorieCalculator.calculateCaloriesBurned("Walking", weightKg, distanceKm);

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
        double actualCaloriesBurned = CalorieCalculator.calculateCaloriesBurned("Running", weightKg, distanceKm);

        // Assert the result
        Assertions.assertEquals(expectedCaloriesBurned, actualCaloriesBurned, 0.01, "Calories burned for running did not match expected value");
    }

    @Test
    void testCalculateCaloriesBurnedInvalidActivity() {
        final double weightKg = 70.0; // Example weight in kg
        final double distanceKm = 5.0; // Example distance in km

        // Perform calculation for an invalid activity
        double actualCaloriesBurned = CalorieCalculator.calculateCaloriesBurned("Swimming", weightKg, distanceKm);

        // Assert the result
        Assertions.assertEquals(0.0, actualCaloriesBurned, "Calories burned for an invalid activity should be 0");
    }
}
