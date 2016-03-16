package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;

public class AccountSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_success);

        Libs libs = new Libs(AccountSuccessActivity.this);

        libs.toast(1, 1, getResources().getString(R.string.register_success));

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            imgBg.setImageBitmap(Libs.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_blue, Config.intScreenWidth, Config.intScreenHeight));

            //CareTaker.dbCon = DbCon.getInstance(AccountSuccessActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
    }

    public void goToDashboard(View v) {
        Intent dashboardIntent = new Intent(AccountSuccessActivity.this, DashboardActivity.class);
        Config.intSelectedMenu=Config.intDashboardScreen;
        //dashboardIntent.putExtra("WHICH_SCREEN", Config.intDashboardScreen);
        Config.boolIsLoggedIn = true;
        startActivity(dashboardIntent);
        finish();
    }
}
