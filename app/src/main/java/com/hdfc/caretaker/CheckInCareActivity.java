package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;

public class CheckInCareActivity extends AppCompatActivity {

    public static ListView checkActivities;
    public ScrollView activities;
    public ImageButton backButton;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_care);

        checkActivities = (ListView) findViewById(R.id.listCheckInCare);
        activities = (ScrollView) findViewById(R.id.scrollCheckInCare);
        backButton = (ImageButton) findViewById(R.id.buttonBackCheck);
        activities.setVisibility(View.GONE);

        String[] from = {"Check In Care Activity 1", "Check In Care Activity 2", "Check In Care Activity 3", "Check In Care Activity 4", "Check In Care Activity 5", "Check In Care Activity 5", "Check In Care Activity 5"};

        arrayAdapter = new ArrayAdapter(this, R.layout.check_in_care_activities, R.id.tvName, from);

        checkActivities.setAdapter(arrayAdapter);
        checkActivities.setDividerHeight(3);
        checkActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activities.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInCareActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
