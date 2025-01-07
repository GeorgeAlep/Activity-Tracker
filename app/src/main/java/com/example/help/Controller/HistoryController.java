package com.example.help.Controller;

import android.content.Context;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityData;
import com.example.help.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * The HistoryController class manages access to historical activity data
 * and provides utilities for date manipulation and formatting.
 */
public class HistoryController {

    // DatabaseHelper instance for interacting with the database.
    private final DatabaseHelper databaseHelper;

    // Calendar instance for managing and manipulating dates.
    private final Calendar calendar;

    /**
     * Constructor for the HistoryController class.
     * Initializes the database helper and calendar instance.
     *
     * @param context The application context, used to initialize the database helper.
     */
    public HistoryController(Context context) {
        databaseHelper = new DatabaseHelper(context);
        calendar = Calendar.getInstance();
    }

    /**
     * Returns the current date formatted as a string.
     * The format is defined in the Constants class.
     *
     * @return A string representing the current date in the specified format.
     */
    public String getFormattedDate() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.getDefault()).format(calendar.getTime());
    }

    /**
     * Sets the calendar instance to a specific date.
     *
     * @param year       The year to set.
     * @param month      The month to set (0-based, i.e., January is 0).
     * @param dayOfMonth The day of the month to set.
     */
    public void setDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
    }

    /**
     * Adds or subtracts a specified number of days to/from the current calendar date.
     *
     * @param days The number of days to add (positive) or subtract (negative).
     */
    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_MONTH, days);
    }

    /**
     * Retrieves the current calendar instance.
     *
     * @return The current calendar instance.
     */
    public Calendar getCurrentCalendar() {
        return calendar;
    }

    /**
     * Retrieves the activity data for the current date from the database.
     *
     * @return An ActivityData object containing the activity data for the current date,
     *         or null if no data exists for that date.
     */
    public ActivityData getActivityDataForCurrentDate() {
        // Format the current date as a string.
        String currentDate = getFormattedDate();

        // Retrieve the activity data for the formatted date from the database.
        return databaseHelper.getActivityDataForDate(currentDate);
    }
}
