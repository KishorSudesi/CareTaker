package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.caretaker.fragments.ActivityFragment;
import com.hdfc.caretaker.fragments.AddRatingCompletedActivityFragment;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.MyAccountFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.FileModel;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity {

    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        progressDialog = new ProgressDialog(DashboardActivity.this);

        ImageButton buttonActivity = (ImageButton) findViewById(R.id.buttonCallActivity);
        TextView txtViewActivity = (TextView) findViewById(R.id.textViewActivity);

        ImageButton buttonNotifications = (ImageButton) findViewById(R.id.buttonNotifications);
        TextView textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

        ImageButton buttonAccount = (ImageButton) findViewById(R.id.buttonAccount);
        TextView textViewAccount = (TextView) findViewById(R.id.textViewAccount);

        ImageButton buttonSeniors = (ImageButton) findViewById(R.id.buttonSeniors);
        TextView textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);

        if (txtViewActivity != null) {
            txtViewActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.intSelectedMenu = Config.intActivityScreen;
                    goToActivity();
                }
            });
        }

        if (buttonActivity != null) {
            buttonActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.intSelectedMenu = Config.intActivityScreen;
                    goToActivity();
                }
            });
        }

        if (textViewNotifications != null) {
            textViewNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToNotifications();
                }
            });
        }

        if (buttonNotifications != null) {
            buttonNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToNotifications();
                }
            });
        }

        if (buttonAccount != null) {
            buttonAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToAccount();
                }
            });
        }

        if (textViewAccount != null) {
            textViewAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToAccount();
                }
            });
        }

        if (buttonSeniors != null) {
            buttonSeniors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Config.intSelectedMenu = 0;
                    goToDashboard();
                }
            });
        }

        if (textViewSeniors != null) {
            textViewSeniors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Config.intSelectedMenu = 0;
                    goToDashboard();
                }
            });
        }

        if (Config.intSelectedMenu == Config.intNotificationScreen) {
            // Config.intSelectedMenu = 0;
            goToNotifications();
        }

        if (Config.intSelectedMenu == Config.intAccountScreen) {
            //Config.intSelectedMenu = 0;
            goToAccount();
        }

        if (Config.intSelectedMenu == Config.intActivityScreen ||
                Config.intSelectedMenu == Config.intListActivityScreen) {
           // Config.intSelectedMenu = 0;
            goToActivity();
        }

        utils = new Utils(DashboardActivity.this);

        try {
            if (!AccountSuccessActivity.isCreatedNow) {
                threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();

                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else {
                //Config.intSelectedMenu = 0;
                goToDashboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToNotifications() {
        //if (Config.intSelectedMenu != Config.intNotificationScreen) {
            Config.intSelectedMenu = Config.intNotificationScreen;
            NotificationFragment fragment = NotificationFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        // }
    }

    public void goToAccount() {
        //if (Config.intSelectedMenu != Config.intAccountScreen) {
            Config.intSelectedMenu = Config.intAccountScreen;
            MyAccountFragment fragment = MyAccountFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}
    }

    public void goToActivity() {

        //if (Config.intSelectedMenu == Config.intListActivityScreen ||
        //Config.intSelectedMenu == Config.intActivityScreen) {
        Config.intSelectedMenu = Config.intActivityScreen;
            ActivityFragment fragment = ActivityFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}
    }

    public void goToDashboard() {
        //if (Config.intSelectedMenu != Config.intDashboardScreen) {
            Config.intSelectedMenu = Config.intDashboardScreen;
            DashboardFragment newFragment = DashboardFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //moveTaskToBack(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle(getString(R.string.confirm_logout));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.logout();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void setRating(View v) {
        AddRatingCompletedActivityFragment.setRating(v);
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < Config.fileModels.size(); i++) {
                    FileModel fileModel = Config.fileModels.get(i);

                    if (fileModel != null && fileModel.getStrFileUrl() != null &&
                            !fileModel.getStrFileUrl().equalsIgnoreCase("")) {
                        utils.loadImageFromWeb(fileModel.getStrFileName(),
                                fileModel.getStrFileUrl());
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

            if (Config.intSelectedMenu == Config.intDashboardScreen) {
                //Config.intSelectedMenu = 0;
                goToDashboard();
            }
        }
    }
}
