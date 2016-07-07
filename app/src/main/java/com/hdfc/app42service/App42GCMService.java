package com.hdfc.app42service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMService extends IntentService {


    public static final String DisplayMessageAction = "com.example.app42sample.DisplayMessage";
    public static final String ExtraMessage = "message";
    private static final String App42GeoTag = "app42_geoBase";
    private static final String Alert = "alert";
    public static GoogleCloudMessaging gcm = null;
    // public static final String ExtraMessage = "message";
    private static int msgCount = 0;
    private Utils utils;
    private SessionManager sessionManager;

    public App42GCMService() {
        super("GcmIntentService");
    }

    public static void unRegisterGcm() {
        try {
            if (gcm != null)
                gcm.unregister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        gcm = GoogleCloudMessaging.getInstance(this);
        utils = new Utils(this);
        sessionManager = new SessionManager(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if ("send_error".equals(messageType)) {
                //App42Log.debug("Send error: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("deleted_messages".equals(messageType)) {
                //App42Log.debug("Deleted messages on server: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("gcm".equals(messageType)) {
                String message = intent.getExtras().getString("message");
                //App42Log.debug("Received: " + extras.toString());
                //App42Log.debug("Message: " + message);
                this.validatePushIfRequired(message, intent);
            }
        }

    }

    private void showNotification(String message, Intent intent) {
       /* DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        Date date = Calendar.getInstance().getTime();
        date.getTime();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.US);
        String dt = dateTime.format(date);
        db.createNotification(title, message, dt);
        db.close();*/
        this.broadCastMessage(message);
        this.sendNotification(message);
        App42GCMReceiver.completeWakefulIntent(intent);
    }

    //
    private void validatePushIfRequired(String message, final Intent intent) {
        try {
            final JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.has("created_by")) {

                String strMessage = null;
                try {
                    strMessage = jsonObject.getString(App42GCMService.ExtraMessage)
                            + "\n Created On: " + Utils.writeFormat.format(Utils.readFormat.parse(jsonObject.getString("time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

               /* Intent in=new Intent(App42GCMService.this,UpdateService.class);
                in.putExtra("message",message);
                startService(in);
                showNotification(strMessage, intent);*/

            } else {

                final String geoBaseType = jsonObject.optString(App42GeoTag, null);

                /*if (geoBaseType == null) {
                    showNotification(json.toString(),intent);
                }*/

                final String alertMessage = jsonObject.optString(Alert, null);

                if (alertMessage != null) {
                    showNotification(jsonObject.getString("alert"), intent);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //Log.e("HDFC", "2");
            showNotification(message, intent);
        }
    }

    private void sendNotification(String msg) {
        //check wth vinay
        try {
            long when = System.currentTimeMillis();
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent;
            notificationIntent = new Intent(this, DashboardActivity.class);

            notificationIntent.putExtra("message_delivered", true);
            notificationIntent.putExtra("message", msg);
            notificationIntent.setFlags(603979776);//603979776 Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            //
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

            String title = "NewZeal";
            mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(msg).setWhen(when).setNumber(++msgCount).setAutoCancel(true)
                    .setDefaults(1).setDefaults(2)
                    .setLights(Notification.DEFAULT_LIGHTS, 5000, 5000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(1, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomers() {
        if (utils.isConnectingToInternet()) {
            StorageService storageService = new StorageService(App42GCMService.this);

            storageService.findDocsByKeyValue(Config.collectionCustomer, "customer_email",
                    Config.strUserName, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                        @Override
                        public void onDocumentInserted(Storage response) {
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {

                            if (response != null) {

                                if (response.getJsonDocList().size() > 0) {

                                    Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                            get(0);

                                    String strDocument = jsonDocument.getJsonDoc();
                                    String values[] = {jsonDocument.getDocId(), jsonDocument.getUpdatedAt(), strDocument, Config.collectionCustomer, "", "1", ""};
                                    try {
                                        //Config.jsonCustomer = new JSONObject(strDocument);

                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {
                                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";
                                            // WHERE clause arguments
                                            String[] selectionArgs = {jsonDocument.getDocId()};
                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                        } else {
                                            CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else {

                                }
                            } else {

                            }
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        } else {

        }
    }

    public void updateDependents() {
        try {
            if (utils.isConnectingToInternet()) {
                String strCustomerId = sessionManager.getCustomerId();

                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionDependent);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }
                Query finalQuery = null;
                Query q1 = QueryBuilder.build("customer_id", strCustomerId, QueryBuilder.Operator.EQUALS);
                // Build query q2
                if (sessionManager.getDependentsStatus()) {
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN_EQUALTO);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                } else {
                    finalQuery = q1;
                }


                StorageService storageService = new StorageService(App42GCMService.this);

                storageService.findDocsByQuery(Config.collectionDependent, finalQuery, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        Storage storage = (Storage) o;
                        if (o != null) {

                            if (storage.getJsonDocList().size() > 0) {

                                try {
                                    CareTaker.dbCon.beginDBTransaction();
                                    for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                        Storage.JSONDocument jsonDocument = storage.
                                                getJsonDocList().get(i);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strDependentDocId = jsonDocument.getDocId();
                                        String values[] = {strDependentDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionDependent, "", "1", ""};

                                        if (sessionManager.getDependentsStatus()) {
                                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                            // WHERE clause arguments
                                            String[] selectionArgs = {strDependentDocId};
                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                        } else {

                                            CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                        }
                                    }
                                    CareTaker.dbCon.dbTransactionSucessFull();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    CareTaker.dbCon.endDBTransaction();

                                }


                            } else {

                            }


                        } else {

                        }


                    }

                    @Override
                    public void onException(Exception e) {

                    }
                });


            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateActivities() {

        try {
            if (utils.isConnectingToInternet()) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int iMonth = calendar.get(Calendar.MONTH) + 1;
                int iYear = calendar.get(Calendar.YEAR);


                String strMonth = String.valueOf(iMonth);
                String strMonthDate, strStartDate, strToDate, strEndDate;

                if (iMonth <= 9)
                    strMonth = String.valueOf("0" + iMonth);

                strMonthDate = String.valueOf(iYear + "-" + strMonth + "-01");

                //String strFromDate = strMonthDate + "T05:30:00.000Z";

                strStartDate = utils.convertDateToStringQuery(utils.convertStringToDateQuery(strMonthDate + "T00:00:00.000"));
                //
                strToDate = utils.getMonthLastDate(strMonthDate);


                strEndDate = utils.convertDateToStringQuery(utils.convertStringToDateQuery(strToDate + "T23:59:59.999"));

                String key2 = "dependent_id";


                if (utils.isConnectingToInternet()) {


                    StorageService storageService = new StorageService(App42GCMService.this);


                    //String value2 = Config.strDependentIds.get(iActivityCount);

                    Query q1 = QueryBuilder.build(key2, Config.strDependentIds, QueryBuilder.Operator.INLIST);

                    Query q2 = QueryBuilder.build("activity_date", strStartDate, QueryBuilder.
                            Operator.GREATER_THAN_EQUALTO);

                    // Build query q1 for key1 equal to name and value1 equal to Nick

                    // Build query q2 for key2 equal to age and value2

                    Query q3 = QueryBuilder.build("activity_date", strEndDate, QueryBuilder.Operator.LESS_THAN_EQUALTO);

                    Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

                    Query q5 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

                    storageService.findDocsByQueryOrderBy(Config.collectionActivity, q5, 3000, 0,
                            "activity_date", 1, new App42CallBack() {

                                @Override
                                public void onSuccess(Object o) {
                                    Storage response = (Storage) o;

                                    if (response != null) {
                                        if (response.getJsonDocList().size() > 0) {
                                            try {
                                                for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                                    Storage.JSONDocument jsonDocument = response.
                                                            getJsonDocList().get(i);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strActivityId = jsonDocument.getDocId();
                                                    JSONObject jsonObjectActivity =
                                                            new JSONObject(strDocument);
                                                    JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                                    jsonObjectActivity.remove("milestones");
                                                    strDocument = jsonObjectActivity.toString();
                                                    String values[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionActivity, "", "1", jsonObjectActivity.optString("activity_date")};
                                                    Log.i("TAG", "Date :" + jsonObjectActivity.optString("activity_date"));
                                                    if (sessionManager.getActivityStatus()) {
                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strActivityId, Config.collectionActivity};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                        for (int j = 0; j < jArray.length(); j++) {
                                                            JSONObject jObj = jArray.optJSONObject(j);
                                                            strDocument = jObj.toString();
                                                            selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " =? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                            // WHERE clause arguments
                                                            String[] selectionArgsMile = {strActivityId, Config.collectionMilestones, jObj.optString("id")};
                                                            String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, valuesMilestone, Config.names_collection_table, selectionArgsMile);


                                                        }

                                                    } else {
                                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                                        for (int j = 0; j < jArray.length(); j++) {
                                                            JSONObject jObj = jArray.optJSONObject(j);
                                                            strDocument = jObj.toString();

                                                            String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                            CareTaker.dbCon.insert(DbHelper.strTableNameCollection, valuesMilestone, Config.names_collection_table);
                                                        }


                                                    }


                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }

                                }

                                @Override
                                public void onException(Exception e) {


                                }
                            }
                    );
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateServices() {
        try {
            if (utils.isConnectingToInternet()) {

                StorageService storageService = new StorageService(App42GCMService.this);

                storageService.findAllDocs(Config.collectionService,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {

                                    try {
                                        Storage storage = (Storage) o;

                                        ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
                                        try {

                                            CareTaker.dbCon.beginDBTransaction();

                                            for (int i = 0; i < jsonDocList.size(); i++) {

                                                Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                                String strDocumentId = jsonDocument.getDocId();

                                                String strServices = jsonDocument.getJsonDoc();

                                                try {
                                                    JSONObject jsonObjectServcies = new JSONObject(strServices);

                                                    if (jsonObjectServcies.has("unit")) {
                                                        String values[] = {strDocumentId, jsonDocument.getUpdatedAt(), strServices, Config.collectionService, "1", ""};
                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strDocumentId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);


                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                            CareTaker.dbCon.dbTransactionSucessFull();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            CareTaker.dbCon.endDBTransaction();

                                        }

                                    } catch (Exception e) {

                                    }

                                } else {
                                    // utils.toast(2, 2, getString(R.string.warning_internet));
                                }


                            }

                            @Override
                            public void onException(Exception e) {
                            }
                        }

                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateServiceCustomer() {
        try {
            if (utils.isConnectingToInternet()) {
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionServiceCustomer);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }
                StorageService storageService = new StorageService(App42GCMService.this);
                Query finalQuery;
                Query q1 = QueryBuilder.build("customer_id", Config.customerModel.getStrCustomerID(), QueryBuilder.Operator.EQUALS);
                if (sessionManager.getServiceCustomer()) {
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN_EQUALTO);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                } else {
                    finalQuery = q1;
                }


                storageService.findDocsByQuery(Config.collectionServiceCustomer, finalQuery, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        try {

                            Storage storage = (Storage) o;
                            if (storage != null) {

                                ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                                CareTaker.dbCon.beginDBTransaction();
                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                    String strDocumentId = jsonDocument.getDocId();
                                    String strServices = jsonDocument.getJsonDoc();

                                    try {
                                        JSONObject jsonObjectServcies = new JSONObject(strServices);
                                        if (jsonObjectServcies.has("unit")) {
                                            String values[] = {strDocumentId, jsonDocument.getUpdatedAt(), strServices, Config.collectionServiceCustomer, "", "1", ""};
                                            if (sessionManager.getServiceCustomer()) {
                                                String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                // WHERE clause arguments
                                                String[] selectionArgs = {strDocumentId};
                                                CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                            } else {
                                                CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                            }


                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                CareTaker.dbCon.dbTransactionSucessFull();
                            } else {
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CareTaker.dbCon.endDBTransaction();

                        }
                    }

                    @Override
                    public void onException(Exception e) {
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateProviders() {
        try {
            if (utils.isConnectingToInternet()) {

                  /*  if (!Config.strProviderIdsAdded.contains(Config.strProviderIds.
                            get(iProviderCount))) {*/
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionProvider);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }


                StorageService storageService = new StorageService(App42GCMService.this);
                Query finalQuery = null;
                Query query = QueryBuilder.build("_id", Config.strProviderIds,
                        QueryBuilder.Operator.INLIST);
                if (sessionManager.getProviderStatus()) {
                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN_EQUALTO);

                    finalQuery = QueryBuilder.compoundOperator(query, QueryBuilder.Operator.AND, q2);

                } else {
                    finalQuery = query;
                }


                storageService.findDocsByQuery(Config.collectionProvider, finalQuery,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                try {
                                    if (o != null) {

                                        Utils.log(o.toString(), " Response Success");

                                        Storage storage = (Storage) o;
                                        try {
                                            if (storage.getJsonDocList().size() > 0) {
                                                CareTaker.dbCon.beginDBTransaction();
                                                for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                    Storage.JSONDocument jsonDocument = storage.
                                                            getJsonDocList().get(i);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strProviderDocId = jsonDocument.
                                                            getDocId();

                                                    String values[] = {strProviderDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionProvider, "", "1", ""};
                                                    if (sessionManager.getProviderStatus()) {

                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strProviderDocId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                    } else {
                                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                                    }


                                                }
                                                CareTaker.dbCon.dbTransactionSucessFull();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            CareTaker.dbCon.endDBTransaction();

                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateNotifications() {
        try {
            StorageService storageService = new StorageService(App42GCMService.this);
            Query finalQuery;
            Query q1 = QueryBuilder.build("user_id", Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID(), QueryBuilder.Operator.EQUALS);
            if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionNotification);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }
                // Build query q2
                Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN_EQUALTO);

                finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
            } else {
                finalQuery = q1;
            }


            storageService.findDocsByQueryOrderBy(Config.collectionNotification, finalQuery, 3000, 0,
                    "time", 1, new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            if (o != null) {

                                Storage storage = (Storage) o;

                                //Utils.log(storage.toString(), "not ");
                                try {


                                    if (storage.getJsonDocList().size() > 0) {
                                        CareTaker.dbCon.beginDBTransaction();
                                        ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                                getJsonDocList();

                                        for (int i = 0; i < jsonDocList.size(); i++) {
                                            String values[] = {jsonDocList.get(i).getDocId(), jsonDocList.get(i).getUpdatedAt(), jsonDocList.get(i).getJsonDoc(), Config.collectionNotification, "", "1", ""};
                                            if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                                                String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                // WHERE clause arguments
                                                String[] selectionArgs = {jsonDocList.get(i).getDocId()};
                                                CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                            } else {


                                                CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                            }

                                        }
                                        CareTaker.dbCon.dbTransactionSucessFull();
                                        //fetchProviders(progressDialog, 1);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    CareTaker.dbCon.endDBTransaction();

                                }

                            } else {
                            }

                        }

                        @Override
                        public void onException(Exception e) {


                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message
     */
    public void broadCastMessage(String message) {
        Intent intent = new Intent(DisplayMessageAction);
        intent.putExtra(ExtraMessage, message);
        this.sendBroadcast(intent);
    }


}
