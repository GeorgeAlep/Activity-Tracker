package com.example.help.Controller;

import android.content.Context;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistoryController {

    private final DatabaseHelper databaseHelper;
    private final Calendar calendar;

    public HistoryController(Context context) {
        databaseHelper = new DatabaseHelper(context);
        calendar = Calendar.getInstance();
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    public void setDate(int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
    }

    public void addDays(int days) {
        calendar.add(Calendar.DAY_OF_MONTH, days);
    }

    public Calendar getCurrentCalendar() {
        return calendar;
    }

    public ActivityData getActivityDataForCurrentDate() {
        String currentDate = getFormattedDate();
        return databaseHelper.getActivityDataForDate(currentDate);
    }
}
