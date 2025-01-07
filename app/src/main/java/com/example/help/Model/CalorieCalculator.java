package com.example.help.Model;

import com.example.help.utils.Constants;

/**
 * The CalorieCalculator class provides methods for calculating calories burned
 * based on the type of activity, the user's weight, and the distance covered.
 */
public class CalorieCalculator {

    /**
     * Calculates the calories burned for walking.
     *
     * @return The total calories burned as a double. Returns 0 if the activity type is unsupported.
     */
    public static double calculateCaloriesBurnedWalking() {
        // Calculate calories burned while walking based on the user's weight and distance walked.
        return Constants.WALKING_CALORIE_BURN_RATE * UserSettings.getWeight() * ActivityData.getDistanceWalked() * 10;
    }

    /**
     * Calculates the calories burned for running.
     *
     * @return The total calories burned as a double. Returns 0 if the activity type is unsupported.
     */
    public static double calculateCaloriesBurnedRunning() {
        // Calculate calories burned while running based on the user's weight and distance run.
        return Constants.RUNNING_CALORIE_BURN_RATE * UserSettings.getWeight() * ActivityData.getDistanceRun() * 10;
    }
}
