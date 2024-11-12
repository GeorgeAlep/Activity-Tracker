package com.example.help.Model;

import android.util.Log;

public class ActivityClassifier {

    private static final String TAG = "ActivityClassifier";

    // Method to classify activity based on speed (in km/h)
    public String classifyActivity(float speed) {
        Log.d(TAG, "Speed (km/h): " + speed);

        if (speed < 0) {
            return "Idle";
        } else if (speed >= 0 && speed <= 7) {
            return "Walking";
        } else if (speed > 7 && speed <= 30) {
            return "Running";
        } else {
            return "Driving";  // Speed above 15 km/h is classified as Driving
        }
    }
}
