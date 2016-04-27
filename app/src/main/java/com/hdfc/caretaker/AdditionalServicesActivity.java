package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.AdditionalServicesAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdditionalServicesActivity extends AppCompatActivity {

    public static List<ServiceModel> selectedServiceModels = new ArrayList<>();
    public static AdditionalServicesAdapter additionalServicesAdapter;
    private static ProgressDialog progressDialog;
    private static StorageService storageService;
    private static LinearLayout dynamicUserTab;
    private static int iServiceCount = 0, iServiceHistoryAddedCount = 0, iServiceDependentAddedCount = 0;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);
        ListView listView = (ListView) findViewById(R.id.listViewAdditionalServices);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);

        utils = new Utils(AdditionalServicesActivity.this);
        progressDialog = new ProgressDialog(AdditionalServicesActivity.this);

        dynamicUserTab = (LinearLayout) findViewById(R.id.dynamicUserTab);

        utils = new Utils(AdditionalServicesActivity.this);

        selectedServiceModels.clear();

        if (buttonContinue != null) {

            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedServiceModels.size() > 0) {

                        if (utils.isConnectingToInternet()) {
                            progressDialog.setMessage(getResources().getString(R.string.loading));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            storageService = new StorageService(AdditionalServicesActivity.this);

                            addServices();
                        } else utils.toast(2, 2, getString(R.string.warning_internet));

                    } else utils.toast(2, 2, getResources().getString(R.string.error_service));
                }
            });
        }

        Button backButton = (Button) findViewById(R.id.buttonBack);

        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        additionalServicesAdapter = new AdditionalServicesAdapter(this, Config.serviceModels);

        if (listView != null) {
            listView.setAdapter(additionalServicesAdapter);
            listView.setEmptyView(textViewEmpty);
        }

        if (listView != null) {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //int count = parent.getChildCount();
                    //View v;

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxService);
                    ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                    if (checkBox.isChecked()) {
                        selectedServiceModels.remove(serviceModel);
                        checkBox.setChecked(false);
                        checkBox.setButtonDrawable(getResources().getDrawable(R.mipmap.check_off));
                    } else {

                        //for clearing previously selected check boxes
                        /*for (int i = 0; i < count; i++) {
                            if (i != position) {
                                v = parent.getChildAt(i);
                                CheckBox checkBoxAll = (CheckBox) v.findViewById(R.id.checkBoxService);
                                checkBoxAll.setChecked(false);
                                checkBox.setButtonDrawable(getResources().getDrawable(R.mipmap.check_off));
                            }
                        }
                        selectedServiceModels.clear();*/

                        selectedServiceModels.add(serviceModel);
                        checkBox.setChecked(true);
                        checkBox.setButtonDrawable(getResources().getDrawable(R.mipmap.check_on));
                    }

                }
            });
        }
    }

    public void goBack() {
        Intent dashboardIntent = new Intent(AdditionalServicesActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intAccountScreen;
        startActivity(dashboardIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();

        utils.populateHeaderDependents(dynamicUserTab, Config.intServiceScreen);

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageService storageService = new StorageService(AdditionalServicesActivity.this);

        storageService.findAllDocs(Config.collectionService,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {

                        if (o != null) {

                            Storage storage = (Storage) o;

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            for (int i = 0; i < jsonDocList.size(); i++) {

                                Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                String strDocumentId = jsonDocument.getDocId();

                                String strServices = jsonDocument.getJsonDoc();

                                try {

                                    JSONObject jsonObjectServcies = new JSONObject(strServices);

                                    if (jsonObjectServcies.has("unit"))
                                        utils.createServiceModel(strDocumentId, jsonObjectServcies);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                        refreshAdapter();
                    }

                    @Override
                    public void onException(Exception e) {

                        try {
                            refreshAdapter();
                            if (e != null) {
                                JSONObject jsonObject = new JSONObject(e.getMessage());
                                JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                String strMess = jsonObjectError.getString("details");

                                utils.toast(2, 2, strMess);
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public void refreshAdapter() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        additionalServicesAdapter.notifyDataSetChanged();
    }

    public void addServiceHistory(final JSONObject jsonObject) {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response != null) {
                                    iServiceHistoryAddedCount++;

                                    if ((iServiceHistoryAddedCount - 1) == iServiceCount
                                            && (iServiceDependentAddedCount - 1) == iServiceCount)
                                        iServiceCount++;

                                    if (iServiceCount == selectedServiceModels.size())
                                        serviceAdded();
                                    else
                                        addServices();

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            try {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (ex != null) {
                                    JSONObject jsonObject = new JSONObject(ex.getMessage());
                                    JSONObject jsonObjectError = jsonObject.
                                            getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");

                                    utils.toast(2, 2, strMess);
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    },
                    Config.collectionServiceHistory);

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void updateServiceHistory(final String strDocumentId, final String strDocument,
                                     final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectServices = new JSONObject();
            String strDate = utils.convertDateToString(new Date());

            try {

                JSONObject jsonObject = new JSONObject(strDocument);

                jsonObjectServices.put("unit", serviceModel.getiUnit());

                //todo check to update this
                /*jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
                jsonObjectServices.put("service_desc", serviceModel.getStrServiceDesc());
                jsonObjectServices.put("service_features", serviceModel);*/

                jsonObjectServices.put("updated_date", strDate);
                jsonObjectServices.put("unit", jsonObject.getInt("unit") + serviceModel.getiUnit());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            storageService.updateDocs(jsonObjectServices, strDocumentId,
                    Config.collectionServiceDependent,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {
                                    iServiceDependentAddedCount++;

                                    JSONObject jsonObjectHistory = createServiceHistory(serviceModel);

                                    addServiceHistory(jsonObjectHistory);

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onException(Exception ex) {
                            try {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (ex != null) {
                                    JSONObject jsonObject = new JSONObject(ex.getMessage());
                                    JSONObject jsonObjectError = jsonObject.
                                            getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");

                                    utils.toast(2, 2, strMess);
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public JSONObject createServiceHistory(ServiceModel serviceModel) {

        JSONObject jsonObjectHistory = null;

        try {
            jsonObjectHistory = new JSONObject();

            jsonObjectHistory.put("dependent_id", Config.dependentModels.
                    get(Config.intSelectedDependent).getStrDependentID());

            jsonObjectHistory.put("unit", serviceModel.getiUnit());
            jsonObjectHistory.put("service_name", serviceModel.getStrServiceName());
            jsonObjectHistory.put("service_desc",
                    serviceModel.getStrServiceDesc());
            jsonObjectHistory.put("service_features", serviceModel.getStrFeatures());
            jsonObjectHistory.put("service_id", serviceModel.getStrServiceId());

            String strDate = utils.convertDateToString(new Date());

            jsonObjectHistory.put("purchased_date", strDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectHistory;
    }

    public void addServiceDependent(final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            //
            JSONObject jsonObjectServices = null;

            try {
                jsonObjectServices = new JSONObject();
                String strDate = utils.convertDateToString(new Date());

                jsonObjectServices.put("unit", serviceModel.getiUnit());
                jsonObjectServices.put("unit_consumed", 0);
                jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
                jsonObjectServices.put("service_desc", serviceModel.getStrServiceDesc());
                jsonObjectServices.put("service_id", serviceModel.getStrServiceId());
                jsonObjectServices.put("updated_date", strDate);
                jsonObjectServices.put("service_features", serviceModel);
                jsonObjectServices.put("dependent_id", Config.dependentModels.
                        get(Config.intSelectedDependent).getStrDependentID());

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            //

            storageService.insertDocs(jsonObjectServices,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            if (response != null) {
                                iServiceDependentAddedCount++;

                                JSONObject jsonObjectHistory = createServiceHistory(serviceModel);

                                addServiceHistory(jsonObjectHistory);

                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            try {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (ex != null) {
                                    JSONObject jsonObject = new JSONObject(ex.getMessage());
                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");

                                    utils.toast(2, 2, strMess);
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    },
                    Config.collectionServiceDependent);

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void checkServiceExists(final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            String key1 = "service_id";
            String value1 = serviceModel.getStrServiceId();

            String key2 = "dependent_id";
            String value2 = Config.dependentModels.get(Config.intSelectedDependent).
                    getStrDependentID();

            Query q1 = QueryBuilder.build(key1, value1, QueryBuilder.Operator.EQUALS);
            Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
            Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

            storageService.findDocsByQueryOrderBy(Config.collectionServiceDependent, q3, 1, 0,
                    "updated_date", 1,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    //
                                    Storage response = (Storage) o;

                                    if (response.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                        String strDocument = jsonDocument.getJsonDoc();

                                        String strDocumentId = jsonDocument.getDocId();

                                        updateServiceHistory(strDocumentId, strDocument, serviceModel);
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            try {
                                if (e != null) {
                                    App42Exception exception = (App42Exception) e;

                                    int appErrorCode = exception.getAppErrorCode();

                                    if (appErrorCode == 2601 || appErrorCode == 2608)
                                        addServiceDependent(serviceModel);
                                    else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void addServices() {

        if (iServiceCount < selectedServiceModels.size()) {

            storageService = new StorageService(AdditionalServicesActivity.this);
            ServiceModel serviceModel = selectedServiceModels.get(iServiceCount);

            try {
                checkServiceExists(serviceModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (iServiceCount == selectedServiceModels.size())
                serviceAdded();
        }
    }

    public void serviceAdded() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        utils.toast(1, 1, getString(R.string.service_added));
        goBack();
    }
}
