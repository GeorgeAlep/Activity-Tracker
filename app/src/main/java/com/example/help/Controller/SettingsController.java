package com.example.help.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.help.Model.UserSettings;
import com.example.help.utils.Constants;

/**
 * The SettingsController class manages user preferences such as weight and
 * distance unit (kilometers or miles) using SharedPreferences for persistent storage.
 */
public class SettingsController {

    // SharedPreferences instance for accessing and storing user settings.
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor for the SettingsController class.
     * Initializes SharedPreferences for accessing and storing user settings.
     *
     * @param context The application context, used to access SharedPreferences.
     */
    public SettingsController(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves the user's settings from SharedPreferences.
     * If no settings are found, default values are used.
     *
     * @return A UserSettings object containing the user's preferences.
     */
    public UserSettings getUserSettings() {
        // Retrieve the weight and distance unit preference (km/miles) from SharedPreferences.
        float weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT);
        boolean useKm = sharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM);

        // Return a UserSettings object with the retrieved values.
        return new UserSettings(weight, useKm);
    }

    /**
     * Saves the user's settings to SharedPreferences.
     *
     * @param settings A UserSettings object containing the user's preferences to save.
     */
    public void saveUserSettings(UserSettings settings) {
        // Open the SharedPreferences editor to make changes.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the weight and distance unit preference to SharedPreferences.
        editor.putFloat(Constants.KEY_WEIGHT, UserSettings.getWeight());
        editor.putBoolean(Constants.KEY_USE_KM, settings.isUseKm());

        // Apply the changes to persist them.
        editor.apply();
    }
}
