package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.libs.Libs;

public class UpcomingActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_activity_activity);

        Libs libs = new Libs(UpcomingActivityActivity.this);
        libs.dashboarMenuNavigation();


        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        TextView txtViewHeader = (TextView) findViewById(R.id.header);

        txtViewHeader.setText("Upcoming Activity");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dashboardIntent = new Intent(UpcomingActivityActivity.this, ActivityListActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
