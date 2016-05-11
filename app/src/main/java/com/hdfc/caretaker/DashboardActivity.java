package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.app42service.App42GCMController;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.caretaker.fragments.ActivityFragment;
import com.hdfc.caretaker.fragments.AddRatingCompletedActivityFragment;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.MyAccountFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42API;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity implements App42GCMController.App42GCMListener {

    private static ProgressDialog progressDialog;
    private static AppCompatActivity appCompatActivity;

    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);
            /*Log.i("mBroadcastReceiver", "" + " : "
                    + message);*/

            if (message != null && !message.equalsIgnoreCase("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(message);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

        }
    };

    private Utils utils;

    public static void goToDashboard() {
        //if (Config.intSelectedMenu != Config.intDashboardScreen) {
        Config.intSelectedMenu = Config.intDashboardScreen;
        DashboardFragment newFragment = DashboardFragment.newInstance();
        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

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
                    goToDashboardMenu();
                }
            });
        }

        if (textViewSeniors != null) {
            textViewSeniors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Config.intSelectedMenu = 0;
                    goToDashboardMenu();
                }
            });
        }

        /*if (Config.intSelectedMenu == Config.intDashboardScreen) {
            //Config.intSelectedMenu = 0;
            goToDashboard();
        }*/

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
        progressDialog = new ProgressDialog(DashboardActivity.this);

        appCompatActivity = DashboardActivity.this;

        App42API.setLoggedInUser(Config.customerModel.getStrEmail());
    }

    @Override
    public void onError(String var1) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        //Log.e("Registr" , gcmRegId);
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if (!App42GCMController.isApp42Registerd(DashboardActivity.this))
            App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
    }

    @Override
    public void onApp42Response(String var1) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(DashboardActivity.this,
                    Config.strAppId, this);//prod. - 272065924531
        } else {
            /*Log.i("App42PushNotification",
					"No valid Google Play Services APK found.");*/
        }
    }

    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
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

    public void goToDashboardMenu() {
        //if (Config.intSelectedMenu != Config.intDashboardScreen) {
        Config.intSelectedMenu = Config.intDashboardScreen;
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Utils.iActivityCount = 0;
        Utils.iProviderCount = 0;

        utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                progressDialog, 0);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (Config.customerModel == null || Config.customerModel.getStrName() == null) {
            Utils.logout();
        } else {

            try {
                if (!AccountSuccessActivity.isCreatedNow &&
                        Config.intSelectedMenu == Config.intDashboardScreen) {

                   /* threadHandler = new ThreadHandler();
                    Thread backgroundThread = new BackgroundThread();
                    backgroundThread.start();*/

                    progressDialog.setMessage(getResources().getString(R.string.loading));
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    Utils.iActivityCount = 0;
                    Utils.iProviderCount = 0;

                    utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                            progressDialog, 0);

                } else {
                    //Config.intSelectedMenu = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                IntentFilter filter = new IntentFilter(
                        App42GCMService.DisplayMessageAction);
                filter.setPriority(2);
                registerReceiver(mBroadcastReceiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                utils.loadAllFiles();
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
    }*/
}
