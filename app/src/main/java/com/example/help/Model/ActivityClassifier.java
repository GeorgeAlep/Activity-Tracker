package com.example.help.Model;

import com.example.help.utils.Constants;

/**
 * The ActivityClassifier class provides a method to classify user activity
 * based on the speed of movement in kilometers per hour (km/h).
 */
public class ActivityClassifier {

    /**
     * Classifies activity type based on the speed provided.
     *
     * @param speed The speed of movement in kilometers per hour (km/h).
     * @return A string representing the classified activity: "Idle", "Walking", "Running", or "Driving".
     */
    public String classifyActivity(float speed) {

        // If the speed is below the idle threshold, classify as "Idle".
        if (speed < Constants.SPEED_IDLE_THRESHOLD) {
            return "Idle";

            // If the speed is between idle and walking thresholds, classify as "Walking".
        } else if (speed >= Constants.SPEED_IDLE_THRESHOLD && speed <= Constants.SPEED_WALKING_THRESHOLD) {
            return "Walking";

            // If the speed is between walking and running thresholds, classify as "Running".
        } else if (speed > Constants.SPEED_WALKING_THRESHOLD && speed <= Constants.SPEED_RUNNING_THRESHOLD) {
            return "Running";

            // If the speed is above the running threshold, classify as "Driving".
        } else {
            return "Driving";
        }
    }
}
