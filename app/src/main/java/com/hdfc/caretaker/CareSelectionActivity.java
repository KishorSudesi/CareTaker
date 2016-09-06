package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Utils;

public class CareSelectionActivity extends AppCompatActivity {

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_selection);

        utils = new Utils(CareSelectionActivity.this);

        //todo set status color
    }

    public void selectedMyself(View v) {
        utils.toast(1, 1, getString(R.string.coming_soon));
    }

    public void selectedLovedOne(View v) {
        Intent selection = new Intent(CareSelectionActivity.this, SignupActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
