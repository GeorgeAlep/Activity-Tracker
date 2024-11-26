package com.example.help.Model;

import com.example.help.utils.Constants;

public class CalorieCalculator {

    public static double calculateCaloriesBurned(String activityType, double weightKg, double distanceKm) {
        switch (activityType) {
            case "Walking":
                return Constants.WALKING_CALORIE_BURN_RATE * weightKg * distanceKm;
            case "Running":
                return Constants.RUNNING_CALORIE_BURN_RATE * weightKg * distanceKm;
            default:
                return 0;
        }
    }
}
