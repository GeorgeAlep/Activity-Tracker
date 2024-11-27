package com.example.help.Database;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.help.utils.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DatabaseHelperTest {

    @Mock
    private SQLiteDatabase mockWritableDb;

    @Mock
    private SQLiteDatabase mockReadableDb;

    @Mock
    private Cursor mockCursor;

    private DatabaseHelper databaseHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a real DatabaseHelper and override methods to use mocks
        databaseHelper = new DatabaseHelper(null) {
            @Override
            public SQLiteDatabase getWritableDatabase() {
                return mockWritableDb;
            }

            @Override
            public SQLiteDatabase getReadableDatabase() {
                return mockReadableDb;
            }
        };
    }

    @Test
    void testInsertActivityData() {
        // Arrange
        String date = "2024-11-25";
        float distanceWalked = 3.5f;
        float distanceRun = 1.2f;
        float distanceDriven = 10.5f;
        double caloriesBurned = 200.0;

        ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);

        when(mockWritableDb.insert(eq(Constants.TABLE_NAME), isNull(), any(ContentValues.class)))
                .thenReturn(1L);

        // Act
        databaseHelper.insertActivityData(date, distanceWalked, distanceRun, distanceDriven, caloriesBurned);

        // Assert
        verify(mockWritableDb).insert(eq(Constants.TABLE_NAME), isNull(), contentValuesCaptor.capture());

        ContentValues capturedValues = contentValuesCaptor.getValue();
        assertNotNull(capturedValues);
        assertEquals(date, capturedValues.get(Constants.COL_DATE));
        assertEquals(distanceWalked, capturedValues.get(Constants.COL_DISTANCE_WALKED));
        assertEquals(distanceRun, capturedValues.get(Constants.COL_DISTANCE_RUN));
        assertEquals(distanceDriven, capturedValues.get(Constants.COL_DISTANCE_DRIVEN));
        assertEquals(caloriesBurned, capturedValues.get(Constants.COL_CALORIES_BURNED));
    }

    @Test
    void testDoesEntryExist() {
        // Arrange
        String date = "2024-11-25";
        when(mockReadableDb.rawQuery(anyString(), any())).thenReturn(mockCursor);
        when(mockCursor.getCount()).thenReturn(1);

        // Act
        boolean exists = databaseHelper.doesEntryExist(date);

        // Assert
        assertTrue(exists);
        verify(mockCursor).close();
    }

    @Test
    void testUpdateActivityData() {
        // Arrange
        String date = "2024-11-25";
        float distanceWalked = 3.0f;
        float distanceRun = 1.0f;
        float distanceDriven = 12.0f;
        double caloriesBurned = 150.0;

        ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);

        // Act
        databaseHelper.updateActivityData(date, distanceWalked, distanceRun, distanceDriven, caloriesBurned);

        // Assert
        verify(mockWritableDb).update(eq(Constants.TABLE_NAME), contentValuesCaptor.capture(), eq(Constants.COL_DATE + " = ?"), eq(new String[]{date}));

        ContentValues capturedValues = contentValuesCaptor.getValue();
        assertNotNull(capturedValues);
        assertEquals(distanceWalked, capturedValues.get(Constants.COL_DISTANCE_WALKED));
        assertEquals(distanceRun, capturedValues.get(Constants.COL_DISTANCE_RUN));
        assertEquals(distanceDriven, capturedValues.get(Constants.COL_DISTANCE_DRIVEN));
        assertEquals(caloriesBurned, capturedValues.get(Constants.COL_CALORIES_BURNED));
    }
}
