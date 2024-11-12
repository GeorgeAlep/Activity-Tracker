package com.example.help.Model;

public class ActivityData {

    private long id;
    private String date;
    private float distanceWalked;
    private float distanceRun;
    private float distanceDriven;
    private double caloriesBurned;  // Single variable for total calories burned

    public ActivityData(long id, String date, float distanceWalked, float distanceRun, float distanceDriven, double caloriesBurned) {
        this.id = id;
        this.date = date;
        this.distanceWalked = distanceWalked;
        this.distanceRun = distanceRun;
        this.distanceDriven = distanceDriven;
        this.caloriesBurned = caloriesBurned;  // Set total calories burned
    }

    // Getters and setters for each field

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public float getDistanceWalked() {
        return distanceWalked;
    }

    public float getDistanceRun() {
        return distanceRun;
    }

    public float getDistanceDriven() {
        return distanceDriven;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}