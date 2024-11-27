package com.example.help;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityUpdateListenerTest {

    private ActivityUpdateListener mockListener;

    @BeforeEach
    void setUp() {
        // Create a mock instance of ActivityUpdateListener
        mockListener = mock(ActivityUpdateListener.class);
    }

    @Test
    void testOnActivityUpdateIsCalled() {
        // Simulate calling the onActivityUpdate method
        String activity = "Walking";
        mockListener.onActivityUpdate(activity);

        // Verify that the method was called with the correct argument
        verify(mockListener).onActivityUpdate(activity);
    }

    @Test
    void testOnActivityUpdateWithDifferentActivities() {
        // Simulate calling the onActivityUpdate method with different activities
        String activity1 = "Running";
        String activity2 = "Driving";

        mockListener.onActivityUpdate(activity1);
        mockListener.onActivityUpdate(activity2);

        // Verify that the method was called for each activity
        verify(mockListener).onActivityUpdate(activity1);
        verify(mockListener).onActivityUpdate(activity2);
    }
}
