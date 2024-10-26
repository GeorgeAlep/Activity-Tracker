package com.example.help.View;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.help.Database.DatabaseHelper;
import com.example.help.Model.ActivityData;
import com.example.help.R;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        databaseHelper = new DatabaseHelper(this);

        // Load data from database
        loadHistoryData();
    }

    private void loadHistoryData() {
        List<ActivityData> activityDataList = databaseHelper.getAllActivityData();

        // Convert activity data to displayable format
        String[] historyArray = new String[activityDataList.size()];
        for (int i = 0; i < activityDataList.size(); i++) {
            ActivityData data = activityDataList.get(i);
            historyArray[i] = "Date: " + data.getDate() +
                    "\nWalked: " + data.getDistanceWalked() + " km" +
                    "\nRan: " + data.getDistanceRun() + " km" +
                    "\nIdle Time: " + data.getIdleTime() + " min";
        }

        // Display the data in the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyArray);
        historyListView.setAdapter(adapter);
    }
}
