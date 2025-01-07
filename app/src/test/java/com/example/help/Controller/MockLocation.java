package com.example.help.Controller;

import android.location.Location;

public class MockLocation extends Location {
    private float speed;

    public MockLocation(float speed) {
        super("Test");
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
