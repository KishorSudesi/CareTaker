package com.hdfc.config;

import com.hdfc.libs.Libs;

import org.json.JSONObject;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String dbName = "newzeal";
    public static final String collectionName = "customer";

    public static final String string = Libs.getStringJni();

    public final static int START_CAMERA_REQUEST_CODE = 1;
    public final static int START_GALLERY_REQUEST_CODE = 2;

    public final static int CACHE_EXPIRE = 1;//In Minutes

    public static final int intWidth = 300, intHeight = 300;
    public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static JSONObject jsonServer = null;

    public static boolean isDebuggable = true;
}