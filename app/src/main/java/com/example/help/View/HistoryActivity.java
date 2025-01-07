package com.example.help.View;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.help.Controller.DisplayController;
import com.example.help.Controller.HistoryController;
import com.example.help.Model.ActivityData;
import com.example.help.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * The HistoryActivity class is responsible for displaying historical activity data to the user.
 * It includes navigation between dates, a date picker, and a ListView to display activity details.
 */
public class HistoryActivity extends AppCompatActivity {

    // ListView to display activity history data
    ListView historyListView;

    // Controller to handle historical data logic
    HistoryController historyController;

    /**
     * Called when the activity is created.
     * Initializes the UI components, sets up event listeners, and loads the initial data.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize UI components
        historyListView = findViewById(R.id.historyListView);
        Button datePickerButton = findViewById(R.id.datePickerButton);
        ImageButton previousDayButton = findViewById(R.id.previousDayButton);
        ImageButton nextDayButton = findViewById(R.id.nextDayButton);

        // Initialize the HistoryController for managing historical data
        historyController = new HistoryController(this);

        // Set the initial date to today and load the data
        updateDisplayedData();

        // Set up the date picker dialog to select a specific date
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Navigate to the previous day's data
        previousDayButton.setOnClickListener(v -> {
            historyController.addDays(-1); // Subtract one day
            updateDisplayedData();
        });

        // Navigate to the next day's data
        nextDayButton.setOnClickListener(v -> {
            historyController.addDays(1); // Add one day
            updateDisplayedData();
        });

        // Hide the ActionBar for a cleaner UI
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Displays a date picker dialog to allow the user to select a specific date.
     */
    private void showDatePickerDialog() {
        // Get the current date from the HistoryController
        Calendar currentCalendar = historyController.getCurrentCalendar();

        // Create and display the date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Update the selected date and refresh the displayed data
                    historyController.setDate(year, month, dayOfMonth);
                    updateDisplayedData();
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Updates the displayed data for the currently selected date.
     * Retrieves data from the HistoryController and passes it to the displayData method.
     */
    public void updateDisplayedData() {
        ActivityData data = historyController.getActivityDataForCurrentDate(); // Get activity data for the current date
        displayData(data);
    }

    /**
     * Displays the given activity data in the ListView.
     *
     * @param data The ActivityData object containing activity details for the selected date.
     */
    private void displayData(ActivityData data) {
        String[] historyArray;

        if (data != null) {
            // Initialize the DisplayController to format distance units
            DisplayController displayController = new DisplayController(this);
            boolean useKm = displayController.isUseKm();

            // Format the activity data into a string array
            historyArray = new String[]{
                    "Date: " + data.getDate(),
                    "Walked: " + String.format(Locale.getDefault(), "%.2f", ActivityData.getDistanceWalked()) + " " + (useKm ? "km" : "miles"),
                    "Ran: " + String.format(Locale.getDefault(), "%.2f", ActivityData.getDistanceRun()) + " " + (useKm ? "km" : "miles"),
                    "Driven: " + String.format(Locale.getDefault(), "%.2f", data.getDistanceDriven()) + " " + (useKm ? "km" : "miles"),
                    "Calories Burned: " + String.format(Locale.getDefault(), "%.0f kcal", data.getCaloriesBurned()) // Display total calories burned
            };
        } else {
            // Show a placeholder message if no data exists for the selected date
            historyArray = new String[]{"No data available for this date."};
        }

        // Use a custom layout to display the data in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, historyArray);
        historyListView.setAdapter(adapter);
    }
}
