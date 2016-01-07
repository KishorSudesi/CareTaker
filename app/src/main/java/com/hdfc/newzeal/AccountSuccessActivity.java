package com.hdfc.newzeal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Libs;

public class AccountSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_success);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
    }

    public void goToDashboard(View v){
        Libs libs = new Libs(AccountSuccessActivity.this);
        Libs.toast(1, 1, getString(R.string.coming_soon));
    }
}
