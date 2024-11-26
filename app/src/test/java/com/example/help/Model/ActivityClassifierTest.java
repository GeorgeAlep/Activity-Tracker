package com.example.help.Model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ActivityClassifierTest {

    private ActivityClassifier activityClassifier;

    @Before
    public void setUp() {
        activityClassifier = new ActivityClassifier();
    }

    @Test
    public void testClassifyActivity_Idle() {
        assertEquals("Idle", activityClassifier.classifyActivity(-1));
        assertEquals("Idle", activityClassifier.classifyActivity(0));
    }

    @Test
    public void testClassifyActivity_Walking() {
        assertEquals("Walking", activityClassifier.classifyActivity(1));
        assertEquals("Walking", activityClassifier.classifyActivity(3.5f));
        assertEquals("Walking", activityClassifier.classifyActivity(7));  // boundary test for walking
    }

    @Test
    public void testClassifyActivity_Running() {
        assertEquals("Running", activityClassifier.classifyActivity(8));  // just above walking threshold
        assertEquals("Running", activityClassifier.classifyActivity(15));
        assertEquals("Running", activityClassifier.classifyActivity(30));  // boundary test for running
    }

    @Test
    public void testClassifyActivity_Driving() {
        assertEquals("Driving", activityClassifier.classifyActivity(31));  // just above running threshold
        assertEquals("Driving", activityClassifier.classifyActivity(50));
        assertEquals("Driving", activityClassifier.classifyActivity(100)); // arbitrary high speed for driving
    }
}
