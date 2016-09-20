package com.hdfc.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hdfc.app42service.StorageService;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sudesi infotech on 7/4/2016.
 */
public class UpdateService extends Service {
    private SessionManager sessionManager = null;
    private Utils utils;
    private NotificationModel notificationModel;
    //private Context mContext;
    //private Handler handler;
    private String strCustomerId;
    private int j = 0;
    private boolean updateAll = false;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = new Utils(UpdateService.this);
        sessionManager = new SessionManager(UpdateService.this);
        //mContext = this;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        try {
            utils = new Utils(UpdateService.this);
            sessionManager = new SessionManager(UpdateService.this);
            //mContext = this;

            try {
                if (intent != null && intent.hasExtra("message")) {
                    String message = intent.getStringExtra("message");

                    JSONObject jObj = new JSONObject(message);

                    notificationModel = new NotificationModel(jObj.optString("message"), jObj.optString("time"), jObj.optString("user_type"), jObj.optString("created_by"), jObj.optString("user_id"), jObj.optString("created_by"), jObj.optString("activity_id"));
                }

                if (intent != null && intent.hasExtra("updateAll")) {
                    updateAll = intent.getBooleanExtra("updateAll", false);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            new UpdateTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateCustomers() {
        if (utils.isConnectingToInternet()) {
            StorageService storageService = new StorageService(UpdateService.this);

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
                                    Log.i("TAG", "Service update customer");

                                    Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                            get(0);

                                    String strDocument = jsonDocument.getJsonDoc();
                                    String values[] = {jsonDocument.getDocId(), jsonDocument.getUpdatedAt(), strDocument, Config.collectionCustomer, "", "1", "", ""};
                                    try {
                                        //Config.jsonCustomer = new JSONObject(strDocument);

                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {
                                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";
                                            // WHERE clause arguments
                                            String[] selectionArgs = {jsonDocument.getDocId()};
                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                        } else {

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else {

                                }
                            } else {

                            }
                            updateDependents();
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            updateDependents();
                        }


                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            updateDependents();
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                            updateDependents();
                        }
                    });
        } else {

        }
    }

    public void updateDependents() {
        try {
            if (utils.isConnectingToInternet()) {
                String strCustomerId = sessionManager.getCustomerId();

                String defaultDate = Utils.defaultDate;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionDependent);

                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                }/* else {
                    defaultDate = ;
                }*/

                Query finalQuery = null;
                Query q1 = QueryBuilder.build("customer_id", strCustomerId, QueryBuilder.Operator.EQUALS);
                // Build query q2
                if (sessionManager.getDependentsStatus()) {

                    if (defaultDate.equalsIgnoreCase("")) {
                        defaultDate = Utils.defaultDate;
                    }

                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                } else {
                    finalQuery = q1;
                }


                StorageService storageService = new StorageService(UpdateService.this);

                storageService.findDocsByQuery(Config.collectionDependent, finalQuery, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        Storage storage = (Storage) o;
                        if (o != null) {
                            Log.i("TAG", "Service update dependents");
                            if (storage.getJsonDocList().size() > 0) {

                                try {

                                    CareTaker.dbCon.beginDBTransaction();
                                    for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                        Storage.JSONDocument jsonDocument = storage.
                                                getJsonDocList().get(i);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strDependentDocId = jsonDocument.getDocId();
                                        String values[] = {strDependentDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionDependent, "", "1", "", ""};

                                        if (sessionManager.getDependentsStatus()) {
                                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                            // WHERE clause arguments
                                            String[] selectionArgs = {strDependentDocId};
                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                        } else {

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

                        updateProviders();
                    }

                    @Override
                    public void onException(Exception e) {
                        updateProviders();
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

                    StorageService storageService = new StorageService(UpdateService.this);


                    //String value2 = Config.strDependentIds.get(iActivityCount);

                    Query q1 = QueryBuilder.build(key2, Config.strDependentIds, QueryBuilder.Operator.INLIST);

//                    Query q2 = QueryBuilder.build("activity_date", strStartDate, QueryBuilder.
//                            Operator.GREATER_THAN_EQUALTO);
//
//                    // Build query q1 for key1 equal to name and value1 equal to Nick
//
//                    // Build query q2 for key2 equal to age and value2
//
//                    Query q3 = QueryBuilder.build("activity_date", strEndDate, QueryBuilder.Operator.LESS_THAN_EQUALTO);
//
//                    Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);
//
//                    Query q5 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);
                    Query q2 = null;
                    if (sessionManager.getActivityStatus()) {
                        String defaultDate = null;
                        Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionActivity);
                        if (cursorData != null && cursorData.getCount() > 0) {
                            cursorData.moveToFirst();
                            defaultDate = cursorData.getString(0);
                            if (defaultDate == null || defaultDate.length() == 0) {
                                defaultDate = Utils.defaultDate;
                            }
                            cursorData.close();
                        } else {
                            defaultDate = Utils.defaultDate;
                        }

                        q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);
                        q1 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q1);
                    } else {

                    }

                    storageService.findDocsByQueryOrderBy(Config.collectionActivity, q1, 3000, 0,
                            "activity_date", 1, new App42CallBack() {

                                @Override
                                public void onSuccess(Object o) {
                                    Storage response = (Storage) o;

                                    if (response != null) {
                                        Log.i("TAG", "Service update activities");
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
                                                    String values[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionActivity, "", "1", jsonObjectActivity.optString("activity_date"), ""};
                                                    Log.i("TAG", "Date :" + jsonObjectActivity.optString("activity_date"));
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
                                                        String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date"), ""};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, valuesMilestone, Config.names_collection_table, selectionArgsMile);


                                                    }
                                                    sessionManager.saveActivityStatus(true);

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }


                                    if (updateAll) {
                                        updateServices();
                                    } else {
                                        updateCheckInCare();
                                    }

                                }

                                @Override
                                public void onException(Exception e) {
                                    if (updateAll) {
                                        updateServices();
                                    } else {
                                        updateCheckInCare();
                                    }

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

                StorageService storageService = new StorageService(UpdateService.this);

                storageService.findAllDocs(Config.collectionService,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {
                                    Log.i("TAG", "Service update service");
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
                                                        String values[] = {strDocumentId, jsonDocument.getUpdatedAt(), strServices, Config.collectionService, "1", "", "", ""};
                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strDocumentId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                        sessionManager.saveServiceStatus(true);

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

                                updateServiceCustomer();
                            }

                            @Override
                            public void onException(Exception e) {
                                updateServiceCustomer();
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
                StorageService storageService = new StorageService(UpdateService.this);
                Query finalQuery;
                Query q1 = QueryBuilder.build("customer_id", strCustomerId, QueryBuilder.Operator.EQUALS);
                if (sessionManager.getServiceCustomer()) {
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

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
                                Log.i("TAG", "Service update servicecustomer");
                                ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                                CareTaker.dbCon.beginDBTransaction();

                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                    String strDocumentId = jsonDocument.getDocId();
                                    String strServices = jsonDocument.getJsonDoc();

                                    try {
                                        JSONObject jsonObjectServcies = new JSONObject(strServices);
                                        //if (jsonObjectServcies.has("unit")) {
                                        String values[] = {strDocumentId, jsonDocument.getUpdatedAt(), strServices, Config.collectionServiceCustomer, "", "1", "", ""};

                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {strDocumentId};
                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                        sessionManager.saveServiceCustomer(true);
                                        //}

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
                            updateCheckInCare();
                        }

                    }

                    @Override
                    public void onException(Exception e) {
                        updateCheckInCare();
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


                StorageService storageService = new StorageService(UpdateService.this);
                Query finalQuery = null;
                Query query = QueryBuilder.build("_id", Config.strProviderIds,
                        QueryBuilder.Operator.INLIST);
                if (sessionManager.getProviderStatus()) {
                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

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

                                        Log.i("TAG", "Service update providers");

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

                                                    String values[] = {strProviderDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionProvider, "", "1", "", ""};
                                                    if (sessionManager.getProviderStatus()) {

                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strProviderDocId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                    } else {

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
                                updateActivities();
                            }

                            @Override
                            public void onException(Exception e) {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                updateActivities();
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCheckInCare() {
        try {
            if (utils.isConnectingToInternet()) {

                Calendar c = Calendar.getInstance();
                String iyear = String.valueOf(c.get(Calendar.YEAR));
                String imonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionCheckInCare);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }

                StorageService storageService = new StorageService(UpdateService.this);

                Query q1 = QueryBuilder.build("year", iyear, QueryBuilder.
                        Operator.EQUALS);
                Query q2 = QueryBuilder.build("month", imonth, QueryBuilder.
                        Operator.EQUALS);
                Query q3 = QueryBuilder.build("customer_id", strCustomerId, QueryBuilder.
                        Operator.EQUALS);

                // Build query q1 for key1 equal to name and value1 equal to Nick

                // Build query q2 for key2 equal to age and value2

                Query q4 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                Query q5 = QueryBuilder.compoundOperator(q3, QueryBuilder.Operator.AND, q4);
                if (sessionManager.getCheckInCareStatus()) {
                    // Build query q2
                    Query q6 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);
                    q5 = QueryBuilder.compoundOperator(q5, QueryBuilder.Operator.AND, q6);
                }

                storageService.findDocsByQueryOrderBy(Config.collectionCheckInCare, q5, 3000, 0, "created_date", 1, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {


                                Storage response = (Storage) o;

                                if (response != null) {

                                    Utils.log(response.toString(), " S ");
                                    Utils.log("Size : " + response.getJsonDocList().size(), " S ");
                                    if (response.getJsonDocList().size() > 0) {
                                        try {
                                            try {
                                                for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                                    Storage.JSONDocument jsonDocument = response.
                                                            getJsonDocList().get(i);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strActivityId = jsonDocument.getDocId();
                                                    //JSONObject jsonObjectActivity = new JSONObject(strDocument);

                                                    String values[] = {strActivityId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionCheckInCare, "", "1", "", ""};


                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {strActivityId};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                updateNotifications();
                            }

                            @Override
                            public void onException(Exception e) {
                                updateNotifications();
                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateNotifications() {
        try {
            if (utils.isConnectingToInternet()) {
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionNotification);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    if (defaultDate == null || defaultDate.length() == 0) {
                        defaultDate = Utils.defaultDate;
                    }
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }
                Query finalQuery, q1 = null;
                StorageService storageService = null;
                if (notificationModel != null) {

                    storageService = new StorageService(UpdateService.this);
                    q1 = QueryBuilder.build("user_id", notificationModel.getStrUserID(), QueryBuilder.Operator.EQUALS);
                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);


                    storageService.findDocsByQueryOrderBy(Config.collectionNotification, finalQuery, 3000, 0,
                            "time", 1, new App42CallBack() {

                                @Override
                                public void onSuccess(Object o) {

                                    if (o != null) {

                                        Storage storage = (Storage) o;

                                        //Utils.log(storage.toString(), "not ");
                                        try {
                                            Log.i("TAG", "Service update notifications");

                                            if (storage.getJsonDocList().size() > 0) {

                                                CareTaker.dbCon.beginDBTransaction();
                                                ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                                        getJsonDocList();

                                                for (int i = 0; i < jsonDocList.size(); i++) {
                                                    String values[] = {jsonDocList.get(i).getDocId(), jsonDocList.get(i).getUpdatedAt(), jsonDocList.get(i).getJsonDoc(), Config.collectionNotification, notificationModel.getStrUserID(), "1", "", ""};
                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {jsonDocList.get(i).getDocId(), notificationModel.getStrUserID()};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                                }
                                                CareTaker.dbCon.dbTransactionSucessFull();
                                                //fetchProviders(progressDialog, 1);

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            CareTaker.dbCon.endDBTransaction();
                                            dismissDialog();
                                            stopSelf();
                                        }

                                    } else {
                                        dismissDialog();
                                        stopSelf();
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    dismissDialog();
                                    stopSelf();

                                }
                            });

                } else if (Config.strDependentIds != null && Config.strDependentIds.size() > 0) {

                    // for (j = 0; j < Config.strDependentIds.size(); j++) {
                    storageService = null;
                    storageService = new StorageService(UpdateService.this);
                    q1 = QueryBuilder.build("user_id", Config.strDependentIds, QueryBuilder.Operator.INLIST);

                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);


                    storageService.findDocsByQueryOrderBy(Config.collectionNotification, finalQuery, 3000, 0,
                            "time", 1, new App42CallBack() {

                                @Override
                                public void onSuccess(Object o) {

                                    if (o != null) {

                                        Storage storage = (Storage) o;

                                        //Utils.log(storage.toString(), "not ");
                                        try {
                                            Log.i("TAG", "Service update notifications");

                                            if (storage.getJsonDocList().size() > 0) {
                                                CareTaker.dbCon.beginDBTransaction();
                                                ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                                        getJsonDocList();

                                                for (int i = 0; i < jsonDocList.size(); i++) {
                                                    JSONObject jsonObjectProvider = new JSONObject(jsonDocList.get(i).getJsonDoc());

                                                    String values[] = {jsonDocList.get(i).getDocId(), jsonDocList.get(i).getUpdatedAt(), jsonDocList.get(i).getJsonDoc(), Config.collectionNotification, jsonObjectProvider.getString("user_id"), "1", ""};
                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {jsonDocList.get(i).getDocId(), jsonObjectProvider.getString("user_id")};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                    if (!(sessionManager.getNotificationIds().contains(jsonObjectProvider.getString("user_id")))) {
                                                        List<String> idsList = new ArrayList<String>();

                                                        if (sessionManager.getNotificationIds().size() > 0) {
                                                            idsList.addAll(sessionManager.getNotificationIds());
                                                        }
                                                        idsList.add(jsonObjectProvider.getString("user_id"));
                                                        sessionManager.saveNotificationIds(idsList);


                                                    }
                                                }
                                                CareTaker.dbCon.dbTransactionSucessFull();

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            CareTaker.dbCon.endDBTransaction();
                                            dismissDialog();
                                            stopSelf();

                                        }

                                    } else {
                                        dismissDialog();
                                        stopSelf();
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    dismissDialog();
                                    stopSelf();
                                }
                            });

                    //}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Send an Intent with an action named "custom-event-name". The Intent sent should
// be received by the ReceiverActivity.
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Config.viewRefreshAction);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void dismissDialog() {

        Log.i("TAG", "In dissmiss dialog");
        try {
            sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class UpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {


                if (CareTaker.dbCon == null) {
                    CareTaker.dbCon = DbCon.getInstance(UpdateService.this);
                }

                Config.strUserName = sessionManager.getEmail();
                if (Config.customerModel != null) {
                    Config.customerModel.setStrCustomerID(sessionManager.getCustomerId());
                }
                strCustomerId = sessionManager.getCustomerId();

                if (Config.strDependentIds == null || Config.strDependentIds.size() <= 0) {
                    Config.strDependentIds.addAll(sessionManager.getDependentsIds());

                }

                if (Config.strProviderIds == null || Config.strProviderIds.size() <= 0) {
                    Config.strProviderIds.addAll(sessionManager.getProvidersIds());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                //updateCustomers();
                //updateDependents();
                //updateProviders();
                if (updateAll) {
                    updateCustomers();
                } else {

                    updateActivities();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
