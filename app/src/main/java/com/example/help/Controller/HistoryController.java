package com.example.help.Controller;

import android.content.Context;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityData;
import com.example.help.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoryController {

    private final DatabaseHelper databaseHelper;
    private final Calendar calendar;

    public HistoryController(Context context) {
        databaseHelper = new DatabaseHelper(context);
        calendar = Calendar.getInstance();
    }

    // Get the current date formatted as a string
    public String getFormattedDate() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.DEFAULT_LOCALE).format(calendar.getTime());
    }

    // Set a specific date in the calendar
    public void setDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
    }

    // Add or subtract days to the current calendar date
    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_MONTH, days);
    }

    // Retrieve the current calendar instance
    public Calendar getCurrentCalendar() {
        return calendar;
    }

    // Retrieve activity data for the current date
    public ActivityData getActivityDataForCurrentDate() {
        String currentDate = getFormattedDate();
        return databaseHelper.getActivityDataForDate(currentDate);
    }
}
