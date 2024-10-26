package com.example.help.Model;

public class ActivityData {

    private long id;
    private String date; // Store the date for each activity record
    private float distanceWalked;
    private float distanceRun;
    private long idleTime; // in minutes

    public ActivityData(long id, String date, float distanceWalked, float distanceRun, long idleTime) {
        this.id = id;
        this.date = date;
        this.distanceWalked = distanceWalked;
        this.distanceRun = distanceRun;
        this.idleTime = idleTime;
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

    public long getIdleTime() {
        return idleTime;
    }
}
