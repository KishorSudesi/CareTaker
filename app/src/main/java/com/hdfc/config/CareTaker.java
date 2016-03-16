package com.hdfc.config;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CareTaker extends Application {

    //private ObjectGraph objectGraph;

    //public static DbCon dbCon = null;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       /* Intent i = new Intent(YourApplication.getInstance(), StartAcitivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(YourApplication.getInstance().getBaseContext(), 0,  i, Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);*/
    }
}
