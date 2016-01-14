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

    private ImageButton buttonNotifications, buttonAccount;
    private TextView textViewNotifications, textViewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        Button btnMonthly = (Button) findViewById(R.id.buttonMonthly);
        ImageView addActivity = (ImageView) findViewById(R.id.addActivity);

        buttonAccount = (ImageButton) findViewById(R.id.buttonAccount);
        textViewAccount = (TextView) findViewById(R.id.textViewAccount);

        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccount();
            }
        });

        textViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccount();
            }
        });

        ImageButton buttonSeniors = (ImageButton) findViewById(R.id.buttonSeniors);
        buttonSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, DashboardActivity.class);
                startActivity(newIntent);
            }
        });

        TextView textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);
        textViewSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, DashboardActivity.class);
                startActivity(newIntent);
            }
        });

        LinearLayout lin1, lin2, lin3;

        lin1 = (LinearLayout) findViewById(R.id.listView1);
        lin2 = (LinearLayout) findViewById(R.id.listView2);
        lin3 = (LinearLayout) findViewById(R.id.listView3);

        buttonNotifications = (ImageButton) findViewById(R.id.buttonNotifications);
        textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

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

                Intent newIntent = new Intent(ActivityListActivity.this, UpcomingActivityActivity.class);
                startActivity(newIntent);
            }
        });

        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, UpcomingActivityActivity.class);
                startActivity(newIntent);
            }
        });

        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityListActivity.this, UpcomingActivityActivity.class);
                startActivity(newIntent);
            }
        });

        textViewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goToNotifications() {
        Intent selection = new Intent(ActivityListActivity.this, NotificationsActivity.class);
        startActivity(selection);
        finish();
    }

    public void goToAccount() {
        Intent selection = new Intent(ActivityListActivity.this, AccountEditActivity.class);
        startActivity(selection);
        finish();
    }
}
