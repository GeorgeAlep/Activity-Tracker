package com.example.help.Database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.help.utils.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DatabaseHelperTest {

    @Mock
    private Context mockContext;

    @Mock
    private SQLiteDatabase mockDatabase;

    @Mock
    private Cursor mockCursor;

    private DatabaseHelper databaseHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        databaseHelper = spy(new DatabaseHelper(mockContext)); // Create a spy of DatabaseHelper
        doReturn(mockDatabase).when(databaseHelper).getReadableDatabase(); // Stub the method
    }

    @Test
    void testDoesEntryExist_True() {
        // Arrange
        String date = "2024-11-25";
        String query = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?";
        when(mockDatabase.rawQuery(query, new String[]{date})).thenReturn(mockCursor);
        when(mockCursor.getCount()).thenReturn(1); // Simulate one matching row
        when(mockCursor.moveToFirst()).thenReturn(true);

        // Act
        boolean result = databaseHelper.doesEntryExist(date);

        // Assert
        assertTrue(result);
        verify(mockDatabase).rawQuery(query, new String[]{date});
        verify(mockCursor).close();
    }

    @Test
    void testDoesEntryExist_False() {
        // Arrange
        String date = "2024-11-25";
        String query = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COL_DATE + " = ?";
        when(mockDatabase.rawQuery(query, new String[]{date})).thenReturn(mockCursor);
        when(mockCursor.getCount()).thenReturn(0); // Simulate no matching rows
        when(mockCursor.moveToFirst()).thenReturn(false);

        // Act
        boolean result = databaseHelper.doesEntryExist(date);

        // Assert
        assertFalse(result);
        verify(mockDatabase).rawQuery(query, new String[]{date});
        verify(mockCursor).close();
    }
}
