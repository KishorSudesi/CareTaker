package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils utils = new Utils(MainActivity.this);
        utils.setStatusBarColor("#96d4ce");
    }

    public void goToWho(View v) {
        Intent selection = new Intent(MainActivity.this, CareSelectionActivity.class);
        startActivity(selection);
    }

    public void goToLogin(View v) {
        Intent selection = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(selection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }
}
