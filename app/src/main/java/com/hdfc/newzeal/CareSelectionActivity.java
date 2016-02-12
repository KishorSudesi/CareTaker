package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Libs;

public class CareSelectionActivity extends AppCompatActivity {

    // private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_selection);
    }

    public void selectedMyself(View v) {
        Libs.toast(1, 1, getString(R.string.coming_soon));
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
