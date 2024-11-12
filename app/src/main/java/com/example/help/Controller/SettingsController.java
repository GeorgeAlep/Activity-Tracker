package com.example.help.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.help.Model.UserSettings;

public class SettingsController {
    private static final String PREFS_NAME = "user_settings";
    private static final String KEY_WEIGHT = "user_weight";
    private static final String KEY_USE_KM = "use_km";
    private final SharedPreferences sharedPreferences;

    public SettingsController(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public UserSettings getUserSettings() {
        float weight = sharedPreferences.getFloat(KEY_WEIGHT, 70.0f); // Default weight is 70kg
        boolean useKm = sharedPreferences.getBoolean(KEY_USE_KM, true);
        return new UserSettings(weight, useKm);
    }

    public void saveUserSettings(UserSettings settings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_WEIGHT, settings.getWeight()); // Store weight as float
        editor.putBoolean(KEY_USE_KM, settings.isUseKm());
        editor.apply();
    }
}
