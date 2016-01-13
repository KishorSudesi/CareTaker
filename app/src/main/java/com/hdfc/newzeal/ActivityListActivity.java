package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ActivityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        Button btnMonthly = (Button) findViewById(R.id.buttonMonthly);

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("ERROR", "1");
                Intent newIntent = new Intent(ActivityListActivity.this, ActivityMonthActivity.class);
                startActivity(newIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
