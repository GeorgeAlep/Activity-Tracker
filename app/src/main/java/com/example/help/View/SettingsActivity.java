package com.example.help.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.help.Controller.SettingsController;
import com.example.help.Model.UserSettings;
import com.example.help.R;

public class SettingsActivity extends AppCompatActivity {

    private EditText weightEditText;
    private RadioButton kmRadioButton;
    private RadioButton milesRadioButton;
    private SettingsController settingsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        weightEditText = findViewById(R.id.weightEditText);
        kmRadioButton = findViewById(R.id.kmRadioButton);
        milesRadioButton = findViewById(R.id.milesRadioButton);
        Button saveButton = findViewById(R.id.saveButton);

        settingsController = new SettingsController(this);

        // Load current settings
        UserSettings settings = settingsController.getUserSettings();
        weightEditText.setText(String.valueOf(settings.getWeight()));
        if (settings.isUseKm()) {
            kmRadioButton.setChecked(true);
        } else {
            milesRadioButton.setChecked(true);
        }

        // Save settings when button is clicked
        saveButton.setOnClickListener(v -> {
            float weight = Float.parseFloat(weightEditText.getText().toString());
            boolean useKm = kmRadioButton.isChecked();

            UserSettings newSettings = new UserSettings(weight, useKm);
            settingsController.saveUserSettings(newSettings);
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
    }
}
