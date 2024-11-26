package com.example.help.Model;

import android.util.Log;
import com.example.help.utils.Constants;

public class ActivityClassifier {

    private static final String TAG = "ActivityClassifier";

    // Method to classify activity based on speed (in km/h)
    public String classifyActivity(float speed) {
        Log.d(TAG, "Speed (km/h): " + speed);

        if (speed < Constants.SPEED_IDLE_THRESHOLD) {
            return "Idle";
        } else if (speed >= Constants.SPEED_IDLE_THRESHOLD && speed <= Constants.SPEED_WALKING_THRESHOLD) {
            return "Walking";
        } else if (speed > Constants.SPEED_WALKING_THRESHOLD && speed <= Constants.SPEED_RUNNING_THRESHOLD) {
            return "Running";
        } else {
            return "Driving";
        }
    }
}
