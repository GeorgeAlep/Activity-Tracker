package com.example.help.Controller;

import android.content.Context;

import com.example.help.Model.UserSettings;
import com.example.help.utils.Constants;

import java.util.Locale;

/**
 * The DisplayController class is responsible for managing the display-related logic,
 * such as formatting distances based on user settings.
 */
public class DisplayController {

    // A boolean indicating whether to use kilometers or miles for distance formatting.
    private final boolean useKm;

    /**
     * Constructor for the DisplayController class.
     * Initializes the distance unit preference (km or miles) based on user settings.
     *
     * @param context The application context, used to retrieve user settings.
     */
    public DisplayController(Context context) {
        // Retrieve user settings using the SettingsController.
        SettingsController settingsController = new SettingsController(context);
        UserSettings settings = settingsController.getUserSettings();

        // Initialize the distance unit preference.
        this.useKm = settings.isUseKm();
    }

    /**
     * Returns whether the user prefers kilometers for distance display.
     *
     * @return true if kilometers should be used, false if miles should be used.
     */
    public boolean isUseKm() {
        return useKm;
    }

    /**
     * Formats a given distance based on the user's unit preference.
     *
     * @param distanceInKm The distance in kilometers to be formatted.
     * @return A formatted string representing the distance in the preferred unit (km or miles).
     */
    public String getFormattedDistance(double distanceInKm) {
        // Convert the distance to the preferred unit (km or miles).
        double distance = useKm ? distanceInKm : distanceInKm * Constants.KM_TO_MILES;

        // Select the appropriate unit label (km or miles).
        String unit = useKm ? "km" : "miles";

        // Return the formatted string with two decimal places and the unit.
        return String.format(Locale.getDefault(), "%.2f %s", distance, unit);
    }
}
