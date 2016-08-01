package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hdfc.config.Config;
import com.hdfc.libs.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountSuccessActivity extends AppCompatActivity {

    public static boolean isCreatedNow = false;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_success);

        utils = new Utils(AccountSuccessActivity.this);
        progressDialog = new ProgressDialog(AccountSuccessActivity.this);

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);

            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.app_header_blue, Config.intScreenWidth, Config.intScreenHeight));
            }

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logout();
    }

    public void goToDashboard(View v) {

        try {

            Config.dependentModels = SignupActivity.dependentModels;

            SignupActivity.strCustomerPass = "";
            Config.strUserName = Config.customerModel.getStrEmail();

            SignupActivity.dependentModels = new ArrayList<>();
            SignupActivity.dependentNames = new ArrayList<>();
            DependentDetailPersonalActivity.dependentModel = null;

           /* progressDialog.setMessage(getResources().getString(
                    R.string.process_login));
            progressDialog.setCancelable(false);
            progressDialog.show();*/

//            threadHandler = new ThreadHandler();
//            Thread backgroundThread = new BackgroundThread();
//            backgroundThread.start();
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Intent dashboardIntent = new Intent(AccountSuccessActivity.this, DashboardActivity.class);
            Config.intSelectedMenu = Config.intDashboardScreen;
            Config.boolIsLoggedIn = true;
            isCreatedNow = true;
            startActivity(dashboardIntent);
            finish();
        } catch (Exception e) {
            logout();
        }
    }

    public void logout() {
        Config.strUserName = "";
     //   Config.customerModel = null;
        Config.dependentModels.clear();
        Utils.logout(AccountSuccessActivity.this);
    }

    public void moveFiles() {

        try {

            File fromFile1 = new File(Config.customerModel.getStrImgPath());
            File toFile1 = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());

            utils.moveFile(fromFile1, toFile1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            File fromFile2;
            File toFile2;
            for (int i = 0; i < Config.dependentModels.size(); i++) {

                if (Config.dependentModels.get(i).getStrImagePath() != null
                        && !Config.dependentModels.get(i).getStrImagePath().equalsIgnoreCase("")) {

                    Utils.log(Config.dependentModels.get(i).getStrImagePath() + " ~ " +
                            Config.dependentModels.get(i).getStrDependentID(), " PATH ");

                    fromFile2 = new File(Config.dependentModels.get(i).getStrImagePath());
                    toFile2 = utils.getInternalFileImages(Config.dependentModels.get(i).
                            getStrDependentID());

                    utils.moveFile(fromFile2, toFile2);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                moveFiles();
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Intent dashboardIntent = new Intent(AccountSuccessActivity.this, DashboardActivity.class);
            Config.intSelectedMenu = Config.intDashboardScreen;
            Config.boolIsLoggedIn = true;
            isCreatedNow = true;
            startActivity(dashboardIntent);
            finish();
        }
    }
}
