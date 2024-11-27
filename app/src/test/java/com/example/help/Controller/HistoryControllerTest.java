package com.example.help.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityData;
import com.example.help.utils.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoryControllerTest {

    @Mock
    private Context mockContext;

    @Mock
    private DatabaseHelper mockDatabaseHelper;

    private HistoryController historyController;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Initialize HistoryController
        historyController = new HistoryController(mockContext);

        // Use reflection to inject the mock DatabaseHelper
        Field databaseHelperField = HistoryController.class.getDeclaredField("databaseHelper");
        databaseHelperField.setAccessible(true);
        databaseHelperField.set(historyController, mockDatabaseHelper);
    }

    @Test
    public void testGetFormattedDate() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(2024, Calendar.NOVEMBER, 26);
        String expectedDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.DEFAULT_LOCALE).format(testCalendar.getTime());

        historyController.setDate(2024, Calendar.NOVEMBER, 26);
        String actualDate = historyController.getFormattedDate();

        assertEquals(expectedDate, actualDate, "The formatted date should match the expected date.");
    }

    @Test
    public void testSetDate() {
        int year = 2023, month = Calendar.OCTOBER, day = 31;

        historyController.setDate(year, month, day);
        Calendar actualCalendar = historyController.getCurrentCalendar();

        assertEquals(year, actualCalendar.get(Calendar.YEAR));
        assertEquals(month, actualCalendar.get(Calendar.MONTH));
        assertEquals(day, actualCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testAddDays() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(2024, Calendar.NOVEMBER, 25);
        historyController.setDate(2024, Calendar.NOVEMBER, 25);

        historyController.addDays(5);
        Calendar updatedCalendar = historyController.getCurrentCalendar();

        assertEquals(testCalendar.get(Calendar.DAY_OF_MONTH) + 5, updatedCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testGetActivityDataForCurrentDate() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(2024, Calendar.NOVEMBER, 26);
        String currentDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.DEFAULT_LOCALE).format(testCalendar.getTime());
        ActivityData expectedActivityData = new ActivityData(1, currentDate, 3.0f, 2.0f, 10.0f, 500);

        when(mockDatabaseHelper.getActivityDataForDate(currentDate)).thenReturn(expectedActivityData);

        historyController.setDate(2024, Calendar.NOVEMBER, 26);
        ActivityData actualActivityData = historyController.getActivityDataForCurrentDate();

        assertEquals(expectedActivityData, actualActivityData, "The returned activity data should match the mock data.");
    }
}
