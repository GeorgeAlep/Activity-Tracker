package com.example.help.Model;

/**
 * The ActivityData class represents the data collected for user activities on a specific date.
 * It includes distances walked, run, and driven, as well as the total calories burned.
 */
public class ActivityData {

    // Date of the activity data
    private final String date;

    // Static variables for distances walked and run (shared across all instances)
    private static float distanceWalked;
    private static float distanceRun;

    // Distance driven in kilometers
    private final float distanceDriven;

    // Total calories burned
    private final double caloriesBurned;

    /**
     * Constructor for the ActivityData class.
     *
     * @param date           The date of the activity data.
     * @param distanceWalked The total distance walked in kilometers.
     * @param distanceRun    The total distance run in kilometers.
     * @param distanceDriven The total distance driven in kilometers.
     * @param caloriesBurned The total calories burned.
     */
    public ActivityData(String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        this.date = date;
        ActivityData.distanceWalked = distanceWalked;
        ActivityData.distanceRun = distanceRun;
        this.distanceDriven = distanceDriven;
        this.caloriesBurned = caloriesBurned;
    }

    /**
     * Retrieves the date of the activity data.
     *
     * @return The date as a String.
     */
    public String getDate() {
        return date;
    }

    /**
     * Retrieves the total distance walked.
     *
     * @return The distance walked in kilometers.
     */
    public static float getDistanceWalked() {
        return distanceWalked;
    }

    /**
     * Retrieves the total distance run.
     *
     * @return The distance run in kilometers.
     */
    public static float getDistanceRun() {
        return distanceRun;
    }

    /**
     * Retrieves the total distance driven.
     *
     * @return The distance driven in kilometers.
     */
    public float getDistanceDriven() {
        return distanceDriven;
    }

    /**
     * Retrieves the total calories burned.
     *
     * @return The calories burned as a double.
     */
    public double getCaloriesBurned() {
        return caloriesBurned;
    }
}
