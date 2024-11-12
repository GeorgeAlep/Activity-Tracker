package com.example.help.Controller;

import android.content.Context;
import com.example.help.Model.UserSettings;

public class DisplayController {
    private final SettingsController settingsController;
    private final boolean useKm;
    private static final double KM_TO_MILES = 0.621371;

    public DisplayController(Context context) {
        settingsController = new SettingsController(context);
        UserSettings settings = settingsController.getUserSettings();
        this.useKm = settings.isUseKm();
    }

    // New method to expose useKm
    public boolean isUseKm() {
        return useKm;
    }

    public String getFormattedDistance(double distanceInKm) {
        double distance = useKm ? distanceInKm : distanceInKm * KM_TO_MILES;
        String unit = useKm ? "km" : "miles";
        return String.format("%.2f %s", distance, unit);
    }
}