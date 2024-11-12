package com.example.help.Model;

public class CalorieCalculator {
    private static final double WALKING_CALORIE_BURN_RATE = 0.03; // per kg per km
    private static final double RUNNING_CALORIE_BURN_RATE = 0.06; // per kg per km

    public static double calculateCaloriesBurned(String activityType, double weightKg, double distanceKm) {
        switch (activityType) {
            case "Walking":
                return WALKING_CALORIE_BURN_RATE * weightKg * distanceKm;
            case "Running":
                return RUNNING_CALORIE_BURN_RATE * weightKg * distanceKm;
            default:
                return 0;
        }
    }
}
