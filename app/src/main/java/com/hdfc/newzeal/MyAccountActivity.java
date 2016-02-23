package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
    }

    public void editProfile(View v) {
        Intent selection = new Intent(MyAccountActivity.this, AccountEditActivity.class);
        startActivity(selection);
        finish();
    }

    public void goToBuyServices(View v) {
        Intent selection = new Intent(MyAccountActivity.this, AdditionalServicesActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
