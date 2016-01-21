package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Libs;

public class AccountEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Libs libs = new Libs(AccountEditActivity.this);
        libs.dashboarMenuNavigation();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void goToDashboard(View v) {
        Intent nextActivity = new Intent(AccountEditActivity.this, MyAccountActivity.class);
        startActivity(nextActivity);
    }
}
