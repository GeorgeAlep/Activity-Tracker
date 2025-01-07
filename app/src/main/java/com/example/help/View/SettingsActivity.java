package com.example.help.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.help.Controller.SettingsController;
import com.example.help.Model.UserSettings;
import com.example.help.R;

/**
 * The SettingsActivity class allows users to update their preferences,
 * such as weight and preferred distance units (kilometers or miles).
 */
public class SettingsActivity extends AppCompatActivity {

    // UI components
    private EditText weightEditText;
    private RadioButton kmRadioButton;

    // Controller for managing user settings
    private SettingsController settingsController;

    /**
     * Called when the activity is created.
     * Initializes the UI components, loads current settings, and sets up save functionality.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        weightEditText = findViewById(R.id.weightEditText);
        kmRadioButton = findViewById(R.id.kmRadioButton);
        RadioButton milesRadioButton = findViewById(R.id.milesRadioButton);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize the SettingsController to load and save user preferences
        settingsController = new SettingsController(this);

        // Load current settings and populate the UI
        UserSettings settings = settingsController.getUserSettings();
        weightEditText.setText(String.valueOf(UserSettings.getWeight())); // Populate weight field
        if (settings.isUseKm()) {
            kmRadioButton.setChecked(true); // Check "km" if preferred unit is kilometers
        } else {
            milesRadioButton.setChecked(true); // Check "miles" otherwise
        }

        // Save settings when the save button is clicked
        saveButton.setOnClickListener(v -> {
            try {
                // Retrieve input values
                float weight = Float.parseFloat(weightEditText.getText().toString());
                boolean useKm = kmRadioButton.isChecked();

                // Save new settings using the SettingsController
                UserSettings newSettings = new UserSettings(weight, useKm);
                settingsController.saveUserSettings(newSettings);

                // Notify the user that settings have been saved
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

                // Broadcast an intent to notify other components about updated settings
                Intent intent = new Intent("com.example.help.SETTINGS_UPDATED");
                sendBroadcast(intent);
            } catch (NumberFormatException e) {
                // Show error if the weight input is invalid
                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
            }
        });

        // Hide the ActionBar for a cleaner UI
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
