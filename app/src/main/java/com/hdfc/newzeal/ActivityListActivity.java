package com.hdfc.newzeal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
