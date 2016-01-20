package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_activity_step2);

        Button cancelButton = (Button) findViewById(R.id.buttonBack);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityStep2Activity.this, AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void goToActivityList(View v) {
        Intent newIntent = new Intent(AddNewActivityStep2Activity.this, ActivityListActivity.class);
        startActivity(newIntent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
