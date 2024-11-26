package com.example.help.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.help.Model.UserSettings;
import com.example.help.utils.Constants;

public class SettingsController {
    private final SharedPreferences sharedPreferences;

    public SettingsController(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public UserSettings getUserSettings() {
        float weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT, Constants.DEFAULT_WEIGHT);
        boolean useKm = sharedPreferences.getBoolean(Constants.KEY_USE_KM, Constants.DEFAULT_USE_KM);
        return new UserSettings(weight, useKm);
    }

    public void saveUserSettings(UserSettings settings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Constants.KEY_WEIGHT, settings.getWeight());
        editor.putBoolean(Constants.KEY_USE_KM, settings.isUseKm());
        editor.apply();
    }
}
