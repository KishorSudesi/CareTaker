package com.hdfc.newzeal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.FileModel;
import com.hdfc.newzeal.fragments.ActivityListFragment;
import com.hdfc.newzeal.fragments.ActivityMonthFragment;
import com.hdfc.newzeal.fragments.DashboardFragment;
import com.hdfc.newzeal.fragments.MyAccountFragment;
import com.hdfc.newzeal.fragments.NotificationFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity {

    private static int intWhichScreen;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        Bundle b = getIntent().getExtras();
        intWhichScreen = b.getInt("WHICH_SCREEN", Config.intDashboardScreen);

        progressDialog = new ProgressDialog(DashboardActivity.this);

        ImageButton buttonActivity = (ImageButton) findViewById(R.id.buttonCallActivity);
        TextView txtViewActivity = (TextView) findViewById(R.id.textViewActivity);

        ImageButton buttonNotifications = (ImageButton) findViewById(R.id.buttonNotifications);
        TextView textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

        ImageButton buttonAccount = (ImageButton) findViewById(R.id.buttonAccount);
        TextView textViewAccount = (TextView) findViewById(R.id.textViewAccount);

        ImageButton buttonSeniors = (ImageButton) findViewById(R.id.buttonSeniors);
        TextView textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);

        //txtViewActivity.setBackground();

        txtViewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity();
            }
        });

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity();
            }
        });

        textViewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccount();
            }
        });

        textViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccount();
            }
        });

        buttonSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDashboard();
            }
        });
        textViewSeniors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDashboard();
            }
        });

        if (intWhichScreen == Config.intNotificationScreen) {
            goToNotifications();
        }

        if (intWhichScreen == Config.intAccountScreen) {
            Config.intSelectedMenu = 0;
            goToAccount();
        }

        if (intWhichScreen == Config.intActivityScreen || intWhichScreen == Config.intListActivityScreen) {
            Config.intSelectedMenu = 0;
            goToActivity();
        }

        libs = new Libs(DashboardActivity.this);

        //libs.loadImages();

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public void goToNotifications() {
        if (Config.intSelectedMenu != Config.intNotificationScreen) {
            Config.intSelectedMenu = Config.intNotificationScreen;
            NotificationFragment fragment = NotificationFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goToAccount() {
        if (Config.intSelectedMenu != Config.intAccountScreen) {
            Config.intSelectedMenu = Config.intAccountScreen;
            MyAccountFragment fragment = MyAccountFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goToActivity() {
        if (Config.intSelectedMenu != Config.intActivityScreen || intWhichScreen == Config.intActivityScreen) {
            Config.intSelectedMenu = Config.intActivityScreen;
            ActivityMonthFragment fragment2 = ActivityMonthFragment.newInstance();
            Bundle args = new Bundle();
            fragment2.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment2);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if (Config.intSelectedMenu != Config.intListActivityScreen || intWhichScreen == Config.intListActivityScreen) {
            Config.intSelectedMenu = Config.intListActivityScreen;
            ActivityListFragment fragment = ActivityListFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goToDashboard() {
        if (Config.intSelectedMenu != Config.intDashboardScreen) {
            Config.intSelectedMenu = Config.intDashboardScreen;
            DashboardFragment newFragment = DashboardFragment.newInstance();
            Bundle args = new Bundle();
            //args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                for (int i = 0; i < Config.fileModels.size(); i++) {

                    FileModel fileModel = Config.fileModels.get(i);

                    if (fileModel != null && fileModel.getStrFileUrl() != null && !fileModel.getStrFileUrl().equalsIgnoreCase("")) {

                        String strUrl = libs.replaceSpace(fileModel.getStrFileUrl());

                        String strFileName = libs.replaceSpace(fileModel.getStrFileName());

                        File fileImage = libs.createFileInternal("images/" + strFileName);

                        if (fileImage.length() <= 0) {

                            InputStream input;
                            try {

                                URL url = new URL(strUrl); //URLEncoder.encode(fileModel.getStrFileUrl(), "UTF-8")
                                input = url.openStream();
                                byte[] buffer = new byte[1500];
                                OutputStream output = new FileOutputStream(fileImage);
                                try {
                                    int bytesRead;
                                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                        output.write(buffer, 0, bytesRead);
                                    }
                                } finally {
                                    output.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for (int i = 0; i < Config.dependentNames.size(); i++) {
                    try {
                        Config.bitmaps.add(libs.getBitmapFromFile(libs.getInternalFileImages(libs.replaceSpace(Config.dependentNames.get(i))).getAbsolutePath(), Config.intWidth, Config.intHeight));
                    } catch (OutOfMemoryError | Exception e) {
                    }
                }

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (intWhichScreen == Config.intDashboardScreen) {
                goToDashboard();
            }
        }
    }

}
