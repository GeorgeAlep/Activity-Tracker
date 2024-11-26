package com.example.help.Controller;

import android.content.Context;

import com.example.help.Model.UserSettings;
import com.example.help.utils.Constants;

public class DisplayController {
    private final SettingsController settingsController;
    private final boolean useKm;

    public DisplayController(Context context) {
        settingsController = new SettingsController(context);
        UserSettings settings = settingsController.getUserSettings();
        this.useKm = settings.isUseKm();
    }

    public boolean isUseKm() {
        return useKm;
    }

    public String getFormattedDistance(double distanceInKm) {
        double distance = useKm ? distanceInKm : distanceInKm * Constants.KM_TO_MILES;
        String unit = useKm ? "km" : "miles";
        return String.format("%.2f %s", distance, unit);
    }
}
