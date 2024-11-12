package com.example.help.View;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.help.Controller.HistoryController;
import com.example.help.Model.ActivityData;
import com.example.help.R;

import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private TextView selectedDateTextView;
    private HistoryController historyController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        Button datePickerButton = findViewById(R.id.datePickerButton);
        ImageButton previousDayButton = findViewById(R.id.previousDayButton);
        ImageButton nextDayButton = findViewById(R.id.nextDayButton);

        historyController = new HistoryController(this);

        // Set initial date to today and load data
        updateDisplayedData();

        // Open the date picker when the button is clicked
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Go to the previous day when the left arrow is clicked
        previousDayButton.setOnClickListener(v -> {
            historyController.addDays(-1);
            updateDisplayedData();
        });

        // Go to the next day when the right arrow is clicked
        nextDayButton.setOnClickListener(v -> {
            historyController.addDays(1);
            updateDisplayedData();
        });
    }

    private void showDatePickerDialog() {
        Calendar currentCalendar = historyController.getCurrentCalendar();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    historyController.setDate(year, month, dayOfMonth);
                    updateDisplayedData();
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDisplayedData() {
        // Get formatted date and display it
        String selectedDate = historyController.getFormattedDate();
        selectedDateTextView.setText(selectedDate);

        // Load and display data for the selected date
        ActivityData data = historyController.getActivityDataForCurrentDate();
        displayData(data);
    }

    private void displayData(ActivityData data) {
        String[] historyArray;
        if (data != null) {
            historyArray = new String[]{
                    "Date: " + data.getDate(),
                    "Walked: " + String.format("%.2f", data.getDistanceWalked()) + " km",
                    "Ran: " + String.format("%.2f", data.getDistanceRun()) + " km",
                    "Driven: " + String.format("%.2f", data.getDistanceDriven()) + " km",
                    "Calories Burned: " + String.format("%.0f kcal", data.getCaloriesBurned()) // Display total calories burned
            };
        } else {
            historyArray = new String[]{"No data available for this date."};
        }

        // Display the data in the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyArray);
        historyListView.setAdapter(adapter);
    }
}