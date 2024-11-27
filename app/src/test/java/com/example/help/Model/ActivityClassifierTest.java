package com.example.help.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.help.utils.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityClassifierTest {

    private ActivityClassifier activityClassifier;

    @BeforeEach
    void setUp() {
        activityClassifier = new ActivityClassifier();
    }


    @Test
    void classifyActivity_shouldReturnIdle_whenSpeedIsBelowIdleThreshold() {
        float speed = Constants.SPEED_IDLE_THRESHOLD - 0.1f;
        String result = activityClassifier.classifyActivity(speed);
        assertEquals("Idle", result);
    }


    @Test
    void classifyActivity_shouldReturnWalking_whenSpeedIsWithinWalkingRange() {
        float speed = (Constants.SPEED_IDLE_THRESHOLD + Constants.SPEED_WALKING_THRESHOLD) / 2;
        String result = activityClassifier.classifyActivity(speed);
        assertEquals("Walking", result);
    }

    @Test
    void classifyActivity_shouldReturnRunning_whenSpeedIsWithinRunningRange() {
        float speed = (Constants.SPEED_WALKING_THRESHOLD + Constants.SPEED_RUNNING_THRESHOLD) / 2;
        String result = activityClassifier.classifyActivity(speed);
        assertEquals("Running", result);
    }

    @Test
    void classifyActivity_shouldReturnDriving_whenSpeedExceedsRunningThreshold() {
        float speed = Constants.SPEED_RUNNING_THRESHOLD + 0.1f;
        String result = activityClassifier.classifyActivity(speed);
        assertEquals("Driving", result);
    }
}
