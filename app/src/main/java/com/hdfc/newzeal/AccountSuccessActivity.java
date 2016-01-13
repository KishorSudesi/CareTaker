package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.hdfc.config.NewZeal;
import com.hdfc.db.DbCon;
import com.hdfc.libs.Libs;

public class AccountSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_success);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            imgBg.setImageBitmap(Libs.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_blue, screenWidth, screenHeight));

            NewZeal.dbCon = DbCon.getInstance(AccountSuccessActivity.this);
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
        //Libs libs = new Libs(AccountSuccessActivity.this);
        //libs.toast(1, 1, getString(R.string.coming_soon));

        Intent dashboardIntent = new Intent(AccountSuccessActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }
}
