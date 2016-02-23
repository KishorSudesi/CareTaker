package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        Button btnMonthly = (Button) findViewById(R.id.buttonMonthly);
        ImageView addActivity = (ImageView) findViewById(R.id.addActivity);

        ImageButton buttonSeniors = (ImageButton) findViewById(R.id.buttonSeniors);
        buttonSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, DashboardActivity.class);
                newIntent.putExtra("MY_ACCOUT", false);
                startActivity(newIntent);
            }
        });

        TextView textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);
        textViewSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, DashboardActivity.class);
                newIntent.putExtra("MY_ACCOUT", false);
                startActivity(newIntent);
            }
        });

        LinearLayout lin1, lin2, lin3, lin4, lin5;

        lin1 = (LinearLayout) findViewById(R.id.listView1);
        lin2 = (LinearLayout) findViewById(R.id.listView2);
        lin3 = (LinearLayout) findViewById(R.id.listView3);

        lin4 = (LinearLayout) findViewById(R.id.completedActivity1);
        lin5 = (LinearLayout) findViewById(R.id.completedActivity2);

        btnMonthly.setText("Monthly");

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, ActivityMonthActivity.class);
                startActivity(newIntent);
            }
        });


        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(ActivityListActivity.this, AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });

        lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpcomingActivity();
            }
        });

        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpcomingActivity();
            }
        });

        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUpcomingActivity();
            }
        });

        lin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCompletedActivity();
            }
        });

        lin5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCompletedActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void goToUpcomingActivity() {
        Intent newIntent = new Intent(ActivityListActivity.this, UpcomingActivityActivity.class);
        startActivity(newIntent);
    }

    public void goToCompletedActivity() {
        Intent newIntent = new Intent(ActivityListActivity.this, ActivityCompletedActivity.class);
        startActivity(newIntent);
    }
}
