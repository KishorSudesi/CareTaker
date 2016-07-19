package com.hdfc.config;

import android.app.Application;
import android.content.res.Configuration;

import com.hdfc.dbconfig.DbCon;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CareTaker extends Application {

    //private ObjectGraph objectGraph;

    //public static DbCon dbCon = null;

    public static DbCon dbCon = null;
    //public static DisplayImageOptions defaultOptions;


    @Override
    public void onCreate() {
        super.onCreate();

       // initImageLoaderConfiguration();
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

//    public void initImageLoaderConfiguration() {
//        // UNIVERSAL IMAGE LOADER SETUP
//        defaultOptions = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.person_icon)
//                .showImageOnFail(R.drawable.person_icon)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .imageScaleType(ImageScaleType.EXACTLY)
//
//                .considerExifParams(true)
//                .displayer(new FadeInBitmapDisplayer(300))
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                getApplicationContext())
//                .defaultDisplayImageOptions(defaultOptions)
//                .memoryCache(new WeakMemoryCache())
//                .discCacheSize(100 * 1024 * 1024).build();
//
//        ImageLoader.getInstance().init(config);
//    }
}
