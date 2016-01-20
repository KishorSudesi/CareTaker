package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AddNewActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityActivity.this, ActivityListActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void addNewActivityStep2(View v) {
        Intent newIntent = new Intent(AddNewActivityActivity.this, AddNewActivityStep2Activity.class);
        startActivity(newIntent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
