package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityCompletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_completed);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        TextView txtViewHeader = (TextView) findViewById(R.id.header);

        txtViewHeader.setText("Activity Completed");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dashboardIntent = new Intent(ActivityCompletedActivity.this, ActivityListActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        });
    }
}
