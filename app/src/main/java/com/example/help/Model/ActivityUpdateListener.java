package com.example.help.Model;

/**
 * The ActivityUpdateListener interface defines a callback method for receiving activity updates.
 * Classes implementing this interface can be notified of changes in the user's activity.
 */
public interface ActivityUpdateListener {

    /**
     * Callback method triggered when the user's activity is updated.
     *
     * @param activity A string representing the current activity (e.g., "Walking", "Running", "Driving", "Idle").
     */
    void onActivityUpdate(String activity);
}
