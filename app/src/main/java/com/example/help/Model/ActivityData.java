package com.example.help.Model;

public class ActivityData {

    private long id;
    private String date;
    private float distanceWalked;
    private float distanceRun;
    private float distanceDriven;  // New field for distance driven

    public ActivityData(long id, String date, float distanceWalked, float distanceRun, float distanceDriven) {
        this.id = id;
        this.date = date;
        this.distanceWalked = distanceWalked;
        this.distanceRun = distanceRun;
        this.distanceDriven = distanceDriven;
    }

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
}
