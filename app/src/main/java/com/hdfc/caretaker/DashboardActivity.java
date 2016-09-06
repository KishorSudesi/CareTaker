package com.hdfc.caretaker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hdfc.app42service.App42GCMController;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.fragments.ActivityFragment;
import com.hdfc.caretaker.fragments.AddCareRecipientsFragment;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.MyAccountEditFragment;
import com.hdfc.caretaker.fragments.MyAccountFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.NetworkStateReceiver;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.service.UpdateService;
import com.shephertz.app42.paas.sdk.android.App42API;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity implements
        App42GCMController.App42GCMListener, NetworkStateReceiver.NetworkStateReceiverListener {

    public static RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    private static AppCompatActivity appCompatActivity;
    private static ImageView buttonActivity;
    private static ImageView buttonNotifications;
    private static ImageView buttonAccount;
    private static ImageView buttonSeniors;
    private static TextView txtViewActivity, textViewNotifications, textViewAccount, textViewSeniors, textViewSync;
    private static Context context;
    private static boolean isSync = false;
    private LinearLayout net_error_layout;
    private NetworkStateReceiver networkStateReceiver;
    private Tracker mTracker;
    private SessionManager sessionManager;
    private Utils utils;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);
            /*Log.i("mBroadcastReceiver", "" + " : "
                    + message);*/
            sessionManager = sessionManager == null ? new SessionManager(context) : sessionManager;
            utils = utils == null ? new Utils(context) : utils;
            isSync = true;
            try {
                if (message != null && !message.equalsIgnoreCase("")) {
                    if (Config.intSelectedMenu != Config.intNotificationScreen)
                        showPushDialog(message);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    private final BroadcastReceiver mBroadcastReceiverToUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView(context);
        }
    };

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
        textViewSync.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
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

    private void showPushDialog(final String strMessage) {

        Utils.clearNotifications(DashboardActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(strMessage);
        builder.setPositiveButton(getString(R.string.text_notification), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToNotifications();
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

    public void refreshView(Context context) {
        try {

            if (Config.intSelectedMenu == Config.intActivityScreen ||
                    Config.intSelectedMenu == Config.intListActivityScreen) {
                // Config.intSelectedMenu = 0;
                Log.i("TAG", "In refreshView");
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
                    if (context == null) {
                        context = this;
                    }
                    utils = new Utils(context);
                }
                try {
                    if (utils == null) {
                        if (Config.intSelectedMenu == Config.intDashboardScreen)
                            goToDashboard();
                    } else {
                        utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                                progressDialog, 0);
                    }
                } catch (Exception e) {
                    if (Config.intSelectedMenu == Config.intDashboardScreen)
                        goToDashboard();
                }


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
        try {

            CareTaker careTaker = (CareTaker) getApplication();
            mTracker = careTaker.getDefaultTracker();

            sessionManager = new SessionManager(DashboardActivity.this);
            buttonActivity = (ImageView) findViewById(R.id.buttonCallActivity);
            txtViewActivity = (TextView) findViewById(R.id.textViewActivity);

            net_error_layout = (LinearLayout) findViewById(R.id.pnd_net_error);

            buttonNotifications = (ImageView) findViewById(R.id.buttonNotifications);
            textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

            buttonAccount = (ImageView) findViewById(R.id.buttonAccount);
            textViewAccount = (TextView) findViewById(R.id.textViewAccount);

            buttonSeniors = (ImageView) findViewById(R.id.buttonSeniors);
            textViewSeniors = (TextView) findViewById(R.id.textViewSeniors);

            ImageView buttonSync = (ImageView) findViewById(R.id.buttonSync);
            textViewSync = (TextView) findViewById(R.id.textViewSync);

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

            try {
                networkStateReceiver = new NetworkStateReceiver();
                networkStateReceiver.addListener(this);
                this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.
                        ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (buttonSync != null) {
                buttonSync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            textViewSync.setTextColor(context.getResources().getColor(R.color.blue));
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


            if (Config.customerModel != null) {
                Config.strUserName = sessionManager.getEmail();
                if (Config.customerModel.getStrEmail() == null || Config.customerModel.getStrEmail().length() == 0 || Config.customerModel.getStrEmail().equalsIgnoreCase("")) {
                    App42API.setLoggedInUser(sessionManager.getEmail());
                } else {

                    App42API.setLoggedInUser(Config.customerModel.getStrEmail());
                }
            } else {
                // this case will run when we click on notification and activity is in kill state, then new instance will be launched ..starting from DashBoard Activity
                try {
                    if (CareTaker.dbCon == null) {
                        CareTaker.dbCon = DbCon.getInstance(getApplicationContext());
                    }
                    try {

                        if (sessionManager.isLoggedIn()) {
                            Config.strDependentIds.clear();
                            Config.strProviderIds.clear();
                            Config.strUserName = sessionManager.getEmail();
                            StorageService storageService = new StorageService(context);
                            utils.fetchCustomer(new ProgressDialog(context), 4, sessionManager.getPassword(), sessionManager.getEmail());
                            App42API.setLoggedInUser(sessionManager.getEmail());
                            isSync = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                    //exportDB();
                } catch (Exception e) {
                    e.getMessage();
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
            if (Config.intSelectedMenu == Config.intNotificationScreen) {
                // Config.intSelectedMenu = 0;
                goToNotifications();
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
                    if (Config.intSelectedMenu == Config.intDashboardScreen) {
                        if (!Config.customerModel.isCustomerRegistered() && Config.dependentModels.size() == 0) {
                            goToAccount();
                        } else {
                            if (Config.dependentModels.size() == 0) {
                                utils.fetchDependents(Config.customerModel.getStrCustomerID(),
                                        progressDialog, 0);
                            } else {
                                utils.fetchProviders(progressDialog, 0);
                            }

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }

    /*public void goToAddRecipient() {
        try {
            AddCareRecipientsFragment addRecipientFragment = AddCareRecipientsFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_dashboard, addRecipientFragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onError(String var1) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        //Log.e("Registr" , gcmRegId);
        try {
            App42GCMController.storeRegistrationId(this, gcmRegId);
            if (!App42GCMController.isApp42Registerd(DashboardActivity.this)) {
                App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
                sessionManager.setDeviceToken(gcmRegId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegisterApp42(String var1) {
        try {
            App42GCMController.storeApp42Success(DashboardActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToNotifications() {
        try {
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goToRecipints() {
        //if (Config.intSelectedMenu != Config.intAccountScreen) {
        try {
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        //}
    }

    private void goToActivity(boolean bReload) {
        try {
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void goToDashboardMenu() {
        try {
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        //}
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //moveTaskToBack(true);

        try {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_dashboard);

            if (f instanceof MyAccountEditFragment || f instanceof AddCareRecipientsFragment) {
                Config.intSelectedMenu = Config.intAccountScreen;
                MyAccountFragment fragment = MyAccountFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_dashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Exit")
                        .build());

                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();

                /*if (utils.isConnectingToInternet()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.alert_dialog_text, null);
                    builder.setCustomTitle(view);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ///
                       *//* if (CareTaker.dbCon != null) {
                            CareTaker.dbCon.close();
                        }*//*
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
                } else {
                    utils.toast(1, 1, context.getString(R.string.warning_internet));
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*public void setRating(View v) {
        AddRatingFragment.setRating(v);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (Config.customerModel != null && Config.customerModel.getStrName() != null
                    && !(Config.customerModel.getStrName().equalsIgnoreCase(""))) {
                try {
                    mTracker.setScreenName("CT - DashboardActivity "
                            + String.valueOf(Config.intSelectedMenu));
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                    IntentFilter filter = new IntentFilter(
                            App42GCMService.DisplayMessageAction);
                    filter.setPriority(2);
                    registerReceiver(mBroadcastReceiver, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.
                            CONNECTIVITY_ACTION));

                    LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverToUpdate,
                            new IntentFilter(Config.viewRefreshAction));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.logout(DashboardActivity.this);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Utils.log(String.valueOf(intent.getStringExtra(App42GCMService.ExtraMessage)),
                    " PUSH ");
            if (intent.getStringExtra(App42GCMService.ExtraMessage) != null) {

                String strMess = intent.getStringExtra(App42GCMService.ExtraMessage);

                if (CareTaker.dbCon != null) {
                    showPushDialog(strMess);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (networkStateReceiver != null)
                unregisterReceiver(networkStateReceiver);

            if (mBroadcastReceiverToUpdate != null)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverToUpdate);

            if (mBroadcastReceiver != null)
                unregisterReceiver(mBroadcastReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void networkUnavailable() {
        net_error_layout.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);

        net_error_layout.postDelayed(
                new Runnable() {
                    public void run() {
                        net_error_layout.setVisibility(View.GONE);
                    }
                }, 3000);
    }

    @Override
    public void networkAvailable() {
        net_error_layout.setVisibility(View.GONE);
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
