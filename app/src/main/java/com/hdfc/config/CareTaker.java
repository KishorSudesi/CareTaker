package com.hdfc.config;

import android.app.Application;
import android.content.res.Configuration;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hdfc.caretaker.R;
import com.hdfc.dbconfig.DbCon;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CareTaker extends Application {

    //private ObjectGraph objectGraph;

    //public static DbCon dbCon = null;

    public static DbCon dbCon = null;
    private Tracker mTracker;
    //public static DisplayImageOptions defaultOptions;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       /* Intent i = new Intent(YourApplication.getInstance(), StartAcitivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(YourApplication.getInstance().getBaseContext(), 0,  i, Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);*/


    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
