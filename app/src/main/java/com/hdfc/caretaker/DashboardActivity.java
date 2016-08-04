package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.App42GCMController;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.caretaker.fragments.ActivityFragment;
import com.hdfc.caretaker.fragments.AddCareRecipientsFragment;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.MyAccountEditFragment;
import com.hdfc.caretaker.fragments.MyAccountFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.UpdateService;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42API;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity implements App42GCMController.App42GCMListener {

    public static RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    private static AppCompatActivity appCompatActivity;

    private static ImageButton buttonActivity, buttonNotifications, buttonAccount, buttonSeniors, buttonSync;
    private static TextView txtViewActivity, textViewNotifications, textViewAccount, textViewSeniors;

    private static Context context;
    private static boolean isSync = false;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
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
               /* builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
                builder.show();
            }

        }
    };
    private SessionManager sessionManager;
    private Utils utils;

    public static void goToDashboard() {
        if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
            goToAccount();
        } else {
            //if (Config.intSelectedMenu != Config.intDashboardScreen) {
            Config.intSelectedMenu = Config.intDashboardScreen;
            setMenu();

            buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior_active));
            textViewSeniors.setTextColor(context.getResources().getColor(R.color.blue));
            DashboardFragment newFragment = DashboardFragment.newInstance();
            FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            //}
        }


    }

    private static void setMenu() {
        buttonActivity.setImageDrawable(context.getResources().getDrawable(R.mipmap.activity));
        buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
        buttonNotifications.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification));
        buttonAccount.setImageDrawable(context.getResources().getDrawable(R.mipmap.my_account));

        textViewAccount.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        textViewNotifications.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        textViewSeniors.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        txtViewActivity.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
    }

    public static void goToAccount() {
        //if (Config.intSelectedMenu != Config.intAccountScreen) {
        try {
            Config.intSelectedMenu = Config.intAccountScreen;
            MyAccountFragment fragment = MyAccountFragment.newInstance();
            FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            setMenu();
            buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
            buttonAccount.setImageDrawable(context.getResources().getDrawable(R.mipmap.my_account_active));
            textViewAccount.setTextColor(context.getResources().getColor(R.color.blue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
    }

    public static void updateActivityIconMenu() {
        try {
            setMenu();
            buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
            buttonActivity.setImageDrawable(context.getResources().getDrawable(R.mipmap.activity_active));
            txtViewActivity.setTextColor(context.getResources().getColor(R.color.blue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshActivityView() {
        try {

            if (Config.intSelectedMenu == Config.intActivityScreen ||
                    Config.intSelectedMenu == Config.intListActivityScreen) {
                // Config.intSelectedMenu = 0;
                Log.i("TAG", "In refreshActivityView");
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                ActivityFragment.refreshData(month, year);
            } else if (Config.intSelectedMenu == Config.intNotificationScreen) {
                NotificationFragment.refreshNotification();
            } else if (!AccountSuccessActivity.isCreatedNow &&
                    Config.intSelectedMenu == Config.intDashboardScreen) {

                   /* threadHandler = new ThreadHandler();
                    Thread backgroundThread = new BackgroundThread();
                    backgroundThread.start();*/


                loadingPanel.setVisibility(View.VISIBLE);
                /*progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();*/


                Utils.iActivityCount = 0;
                Utils.iProviderCount = 0;
                if (utils == null) {
                    utils = new Utils(DashboardActivity.this);
                }
                utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                        progressDialog, 0);


            } else {
                //Config.intSelectedMenu = 0;
                if (Config.intSelectedMenu == Config.intDashboardScreen)
                    goToDashboard();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (DashboardActivity.loadingPanel.getVisibility() == View.VISIBLE) {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        sessionManager = new SessionManager(DashboardActivity.this);
        buttonActivity = (ImageButton) findViewById(R.id.buttonCallActivity);
        txtViewActivity = (TextView) findViewById(R.id.textViewActivity);

        buttonNotifications = (ImageButton) findViewById(R.id.buttonNotifications);
        textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

        buttonAccount = (ImageButton) findViewById(R.id.buttonAccount);
        textViewAccount = (TextView) findViewById(R.id.textViewAccount);

        buttonSeniors = (ImageButton) findViewById(R.id.buttonSeniors);
        textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);

        buttonSync = (ImageButton) findViewById(R.id.buttonSync);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        utils = new Utils(DashboardActivity.this);
        progressDialog = new ProgressDialog(DashboardActivity.this);

        utils.setStatusBarColor("#2196f3");

        appCompatActivity = DashboardActivity.this;

        context = this;

        // Bundle b = getIntent().getExtras();

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        boolean bReloadActivity = false;

        if (extras != null) {
            bReloadActivity = extras.getBoolean(Config.strReload, false);
        }

        setMenu();
        buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior_active));
        textViewSeniors.setTextColor(context.getResources().getColor(R.color.blue));
        if (txtViewActivity != null) {
            txtViewActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.intSelectedMenu = Config.intListActivityScreen;
                    goToActivity(true);
                }
            });
        }

        if (buttonActivity != null) {
            buttonActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Config.intSelectedMenu = Config.intListActivityScreen;
                    goToActivity(true);
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

        if (buttonSync != null) {
            buttonSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (utils.isConnectingToInternet()) {
                            loadingPanel.setVisibility(View.VISIBLE);
                            Intent in = new Intent(DashboardActivity.this, UpdateService.class);
                            in.putExtra("updateAll", true);
                            startService(in);
                        } else if (!utils.isConnectingToInternet()) {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /*if (Config.intSelectedMenu == Config.intDashboardScreen) {
            //Config.intSelectedMenu = 0;
            goToDashboard();
        }*/

       /* if (Config.intSelectedMenu == Config.intNotificationScreen) {
            // Config.intSelectedMenu = 0;
            goToNotifications();
        }*/


        if (Config.customerModel != null) {
            Config.strUserName = sessionManager.getEmail();
            if (Config.customerModel.getStrEmail() == null || Config.customerModel.getStrEmail().length() == 0 || Config.customerModel.getStrEmail().equalsIgnoreCase("")) {


                App42API.setLoggedInUser(sessionManager.getEmail());
            } else {

                App42API.setLoggedInUser(Config.customerModel.getStrEmail());
            }
        }

        if (Config.intSelectedMenu == Config.intAccountScreen) {
            //Config.intSelectedMenu = 0;
            goToAccount();
        }

        if (Config.intSelectedMenu == Config.intRecipientScreen) {
            //Config.intSelectedMenu = 0;
            goToRecipints();
        }
        if (Config.intSelectedMenu == Config.intActivityScreen ||
                Config.intSelectedMenu == Config.intListActivityScreen) {
            // Config.intSelectedMenu = 0;
            goToActivity(bReloadActivity);
        }

        try {


            if (!AccountSuccessActivity.isCreatedNow &&
                    Config.intSelectedMenu == Config.intDashboardScreen) {

                   /* threadHandler = new ThreadHandler();
                    Thread backgroundThread = new BackgroundThread();
                    backgroundThread.start();*/


                loadingPanel.setVisibility(View.VISIBLE);
                /*progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();*/


                Utils.iActivityCount = 0;
                Utils.iProviderCount = 0;

                utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                        progressDialog, 0);

            } else {
                //Config.intSelectedMenu = 0;
                if (Config.intSelectedMenu == Config.intDashboardScreen)
                    if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
                        goToAccount();
                    } else {
                        goToDashboard();
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void goToAddRecipient() {
        AddCareRecipientsFragment addRecipientFragment = AddCareRecipientsFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_dashboard, addRecipientFragment);
        ft.commit();
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

        try {
            if (App42GCMController.isPlayServiceAvailable(this)) {
                App42GCMController.getRegistrationId(DashboardActivity.this,
                        Config.strAppId, this);//prod. - 272065924531
            } else {
                /*Log.i("App42PushNotification",
                        "No valid Google Play Services APK found.");*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
    }

    public void goToNotifications() {
        if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
            utils.toast(2, 2, getString(R.string.no_recipients));
        } else {
            //if (Config.intSelectedMenu != Config.intNotificationScreen) {
            Config.intSelectedMenu = Config.intNotificationScreen;
            NotificationFragment fragment = NotificationFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            setMenu();
            buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
            buttonNotifications.setImageDrawable(getResources().getDrawable(R.mipmap.notification_active));
            textViewNotifications.setTextColor(getResources().getColor(R.color.blue));
            // }
        }
    }

    public void goToRecipints() {
        //if (Config.intSelectedMenu != Config.intAccountScreen) {
        Config.intSelectedMenu = Config.intRecipientScreen;
        AddCareRecipientsFragment fragment = AddCareRecipientsFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        setMenu();
        buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
        buttonAccount.setImageDrawable(getResources().getDrawable(R.mipmap.my_account_active));
        textViewAccount.setTextColor(getResources().getColor(R.color.blue));
        //}
    }

    private void goToActivity(boolean bReload) {
        if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
            utils.toast(2, 2, getString(R.string.no_recipients));
        } else {
            //if (Config.intSelectedMenu == Config.intListActivityScreen ||
            //Config.intSelectedMenu == Config.intActivityScreen) {
            // Config.intSelectedMenu = Config.intActivityScreen;
            ActivityFragment fragment = ActivityFragment.newInstance(bReload);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            setMenu();
            buttonSeniors.setImageDrawable(context.getResources().getDrawable(R.mipmap.senior));
            buttonActivity.setImageDrawable(getResources().getDrawable(R.mipmap.activity_active));
            txtViewActivity.setTextColor(getResources().getColor(R.color.blue));
            //}

            if (!bReload)
                loadingPanel.setVisibility(View.GONE);
        }
    }

    private void goToDashboardMenu() {
        if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
            utils.toast(2, 2, getString(R.string.no_recipients));
        } else {
            //if (Config.intSelectedMenu != Config.intDashboardScreen) {
            Config.intSelectedMenu = Config.intDashboardScreen;
        /*progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

            setMenu();
            buttonSeniors.setImageDrawable(getResources().getDrawable(R.mipmap.senior_active));
            textViewSeniors.setTextColor(getResources().getColor(R.color.blue));


            loadingPanel.setVisibility(View.VISIBLE);
            Utils.iActivityCount = 0;
            Utils.iProviderCount = 0;

            utils.fetchDependents(Config.customerModel.getStrCustomerID(), progressDialog, 0);
        }
        //}
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //moveTaskToBack(true);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_dashboard);

        if (f instanceof MyAccountEditFragment || f instanceof AddCareRecipientsFragment) {
            Config.intSelectedMenu = Config.intAccountScreen;
            MyAccountFragment fragment = MyAccountFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle(getString(R.string.confirm_logout));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ///
                   /* if (CareTaker.dbCon != null) {
                        CareTaker.dbCon.close();
                    }*/
                    //
                    Utils.logout(DashboardActivity.this);
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

    /*public void setRating(View v) {
        AddRatingFragment.setRating(v);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        if (Config.customerModel == null || Config.customerModel.getStrName() == null) {
            Utils.logout(DashboardActivity.this);
        } else {

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
