package com.example.help.Model;

import android.util.Log;

public class ActivityClassifier {

    private static final String TAG = "ActivityClassifier";

    // Method to classify activity based on speed (in km/h)
    public String classifyActivity(float speed) {
        Log.d(TAG, "Speed (km/h): " + speed);

        if (speed < 2) {
            return "Idle";
        } else if (speed > 2 && speed <= 5) {
            return "Walking";
        } else {
            return "Running";
        }
    }
}
