package com.hdfc.config;

import android.graphics.Bitmap;

import com.hdfc.libs.Libs;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.NotificationModel;
import com.hdfc.models.ServiceModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String dbName = "newzeal";
    public static final String collectionNameServices = "services";
    public static final String collectionNameProviders = "providers";
    public static final String collectionName = "customer";
    public static final String strServiceDocId = "56f280b0e4b003cbd56dcb38";//remove this
    //for UAT 56f280b0e4b003cbd56dcb38
    //for development 56c70aefe4b0067c8c7658bf
    public static final String string = Libs.getStringJni();

    public static final int START_CAMERA_REQUEST_CODE = 1;
    public static final int START_GALLERY_REQUEST_CODE = 2;
    public static final int CACHE_EXPIRE = 1;//In Minutes
    public static final int intWidth = 300, intHeight = 300;

    public static final String strCustomerImageName = "customer_image";

    public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static final boolean isDebuggable = false;

    public static final boolean release = true;

    public static int intNotificationScreen = 2;
    public static int intActivityScreen = 5;
    public static int intListActivityScreen = 6;
    public static int intAccountScreen = 3;
    public static int intDashboardScreen = 1;
    public static int intReportsScreen = 4;

    public static int intScreenWidth = 0;
    public static int intScreenHeight = 0;

    //User Specific clear at logout or whenever needed
    public static JSONObject jsonObject = null;
    //public static JSONObject jsonObjectCarla = null;
    public static JSONObject jsonServer = null;
    public static String jsonDocId = "";

    public static int intSelectedMenu = 0;
    public static int intDependentsCount = 0;

    public static ArrayList<ServiceModel> serviceModels = new ArrayList<>();
    public static ArrayList<String> dependentNames = new ArrayList<>();
    public static ArrayList<ArrayList<NotificationModel>> notificationModels = new ArrayList<ArrayList<NotificationModel>>();

    public static int intSelectedDependent = 0;

    public static boolean boolIsLoggedIn = false;

    public static CustomerModel customerModel = null;
    public static String strUserName = "";

    public static ArrayList<FileModel> fileModels = new ArrayList<>();

    public static List<Bitmap> bitmaps = new ArrayList<>();

}