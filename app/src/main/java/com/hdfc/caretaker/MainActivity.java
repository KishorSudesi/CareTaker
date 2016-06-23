package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.config.CareTaker;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils utils = new Utils(MainActivity.this);
        utils.setStatusBarColor("#2196f3");

        //CrashLogger.getInstance().init(MainActivity.this);
        try {
            CareTaker.dbCon = DbCon.getInstance(getApplicationContext());
        } catch (Exception e) {
            e.getMessage();
        }


       /* try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.app_bg, Config.intScreenWidth, Config.intScreenHeight));

                //CrashLogger.getInstance().init(MainActivity.this);
            }

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }*/
    }

    public void goToWho(View v) {
      /*  Intent selection = new Intent(MainActivity.this, CareSelectionActivity.class);
        startActivity(selection);*/

        Intent selection = new Intent(MainActivity.this, ActivityGuruPersonalInfo.class);
        startActivity(selection);
    }

    public void goToLogin(View v) {
        Intent selection = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(selection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (CareTaker.dbCon != null) {
            CareTaker.dbCon.close();
        }

        moveTaskToBack(true);
        finish();
    }
}
