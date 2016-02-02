package com.hdfc.config;

import org.json.JSONObject;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String apiKey = "458f7884154a3afc67bd5c5f24df3d1e6c0852e01e4e272ea3ed79a8d8ac6439";
    public static final String apiSecret = "960d9964855c20907d6bd3ff8b16302bd05082cf8e699cc5ee496b88b41e37d9";

    public static final String dbName = "newzeal";
    public static final String collectionName = "customer";

    public static final String dbPass = "";

    public static final String mode = "DES";

    public static final String key = "HDfc@12#";

    public final static int START_CAMERA_REQUEST_CODE = 1;
    public final static int START_GALLERY_REQUEST_CODE = 2;

    public static final int intWidth = 300, intHeight = 300;
    public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static JSONObject jsonServer = null;

}