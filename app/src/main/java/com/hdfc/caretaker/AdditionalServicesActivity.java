package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdditionalServicesActivity extends AppCompatActivity {

    public static List<ServiceModel> serviceModels = new ArrayList<>();
    public static List<ServiceModel> selectedServiceModels = new ArrayList<>();
    public static AdditionalServicesAdapter additionalServicesAdapter;
    private static ProgressDialog progressDialog;
    private static StorageService storageService;
    private static Storage findObj;
    private Utils utils;
    private JSONObject jsonObjectAct, responseJSONDoc;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);
        ListView listView = (ListView) findViewById(R.id.listViewAdditionalServices);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);

        utils = new Utils(AdditionalServicesActivity.this);
        progressDialog = new ProgressDialog(AdditionalServicesActivity.this);

        if (buttonContinue != null) {
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedServiceModels.size() > 0) {

                        if (utils.isConnectingToInternet()) {

                            storageService = new StorageService(AdditionalServicesActivity.this);

                            jsonObjectAct = new JSONObject();

                            progressDialog.setMessage(getResources().getString(R.string.loading));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if (selectedServiceModels.size() == 1) {

                                ServiceModel serviceModel = selectedServiceModels.get(0);

                                //if(serviceModel.get) //TODO check units updated
                                try {

                                    jsonObjectAct.put("unit", serviceModel.getiUnit());
                                    jsonObjectAct.put("service_name", serviceModel.getStrServiceName());
                                    jsonObjectAct.put("service_desc",
                                            serviceModel.getStrServiceDesc());
                                    jsonObjectAct.put("service_id", serviceModel.getStrServiceId());

                                    String strDate = utils.convertDateToString(new Date());

                                    jsonObjectAct.put("purchased_date", strDate);

                                    //String[] arr = serviceModel.getJsonArrayFeatures();

                                    //jsonArray = serviceModel.getStrFeatures();

                                    //for (String anArr : arr) jsonArray.put(anArr);

                                    jsonObjectAct.put("service_features", jsonArray);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    jsonObjectAct = null;
                                }
                                //
                        }

                            if (jsonObjectAct != null) {

                                storageService.findDocsByIdApp42CallBack(Config.customerModel.getStrCustomerID(),
                                        Config.collectionCustomer, new App42CallBack() {
                                            @Override
                                            public void onSuccess(Object o) {

                                                if (o != null) {

                                                    findObj = (Storage) o;

                                                    try {
                                                        responseJSONDoc = new JSONObject(
                                                                findObj.getJsonDocList().get(0).getJsonDoc());
                                                        if (responseJSONDoc.has("dependents")) {
                                                            JSONArray dependantsA = responseJSONDoc.
                                                                    getJSONArray("dependents");

        /*if (Config.intSelectedDependent <= dependantsA.length()) {
            JSONObject dependantsObject = dependantsA.getJSONObject(Config.intSelectedDependent);
            if (dependantsObject.has("services")) {
                JSONArray activitiesA = dependantsObject.getJSONArray("services");
                activitiesA.put(jsonObjectAct);
            }
        }*/
                                                            for (int i = 0; i < dependantsA.length(); i++) {
                                                                JSONObject dependantsObject = dependantsA.
                                                                        getJSONObject(i);
                                                                if (dependantsObject.has("services_history")) {
                                                                    JSONArray activitiesA = dependantsObject.
                                                                            getJSONArray("services_history");
                                                                    activitiesA.put(jsonObjectAct);
                                                                }

                                                                //service buy
                                                                int iNew = 0;
                                                                if (dependantsObject.has("services")) {

                                                                    JSONArray jsonArrayServices =
                                                                            dependantsObject.
                                                                                    getJSONArray("services");

                                                                    int iSize = jsonArrayServices.length();

                                                                    if (iSize > 0) {

                                                                        for (int j = 0; j < iSize; j++) {

                                                                            JSONObject jsonObjectService =
                                                                                    jsonArrayServices.
                                                                                            getJSONObject(j);

                                                                            if (jsonObjectService.getInt("service_id") == jsonObjectAct.getInt("service_id")) {

                                                                                //TODO update other info later
                                                                                Double aDouble = Utils.round(jsonObjectService.getDouble("unit") + jsonObjectAct.getDouble("unit"), 2);
                                                                                jsonObjectService.put("unit",
                                                                                        aDouble);
                                                                                jsonArrayServices.remove(j);
                                                                                jsonArrayServices.put(jsonObjectService);
                                                                                iNew++;
                                                                            }
                                                            }
                                                        }

                                                                    if (iNew == 0) {

                                                                        JSONObject jsonObjectServices = new JSONObject();
                                                                        jsonObjectServices.put("unit", jsonObjectAct.getDouble("unit"));
                                                                        jsonObjectServices.put("unit_consumed", 0);
                                                                        jsonObjectServices.put("service_name", jsonObjectAct.getString("service_name"));
                                                                        jsonObjectServices.put("service_desc", jsonObjectAct.getString("service_desc"));
                                                                        jsonObjectServices.put("service_id", jsonObjectAct.getInt("service_id"));
                                                                        jsonObjectServices.put("updated_date", jsonObjectAct.getString("purchased_date"));
                                                                        jsonObjectServices.put("service_features", jsonArray);

                                                                        jsonArrayServices.put(jsonObjectServices);
                                                                    }
                                                    }
                                                }
                                            }
                                                    } catch (JSONException jSe) {
                                                        jSe.printStackTrace();
                                                        progressDialog.dismiss();
                                        }

                                                    if (utils.isConnectingToInternet()) {

                                                        storageService.updateDocs(responseJSONDoc,
                                                                Config.customerModel.getStrCustomerID(),
                                                                Config.collectionCustomer,
                                                                new App42CallBack() {
                                                                    @Override
                                                                    public void onSuccess(Object o) {

                                                                        if (o != null) {
                                                                            //Config.jsonObject = responseJSONDoc;
                                                        progressDialog.dismiss();
                                                                            utils.toast(2, 2,
                                                                                    getString(R.string.service_added));
                                                                            Intent dashboardIntent = new Intent(AdditionalServicesActivity.this, DashboardActivity.class);
                                                                            Config.intSelectedMenu = Config.intAccountScreen;
                                                                            startActivity(dashboardIntent);
                                                                            finish();
                                                                        } else {
                                                                            if (progressDialog.isShowing())
                                                                                progressDialog.dismiss();
                                                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                                                        }
                                                }

                                                                    @Override
                                                                    public void onException(Exception e) {
                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();
                                                                        if (e != null) {
                                                                            utils.toast(2, 2, e.getMessage());
                                                                        } else {
                                                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                                                        }
                                                }
                                                                });

                                                    } else {
                                                        if (progressDialog.isShowing())
                                                            progressDialog.dismiss();
                                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                                    }

                                    }else{
                                        if(progressDialog.isShowing())
                                            progressDialog.dismiss();
                                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                                if (e != null) {
                                                    utils.toast(2, 2, e.getMessage());
                                                } else {
                                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                }
                                        });

                            } else utils.toast(2, 2, getString(R.string.error));

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

        additionalServicesAdapter = new AdditionalServicesAdapter(this, serviceModels);
        if (listView != null) {
            listView.setAdapter(additionalServicesAdapter);
        }
        if (listView != null) {
            listView.setEmptyView(textViewEmpty);
        }

        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int count = parent.getChildCount();
                    View v;

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxService);
                    ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                    if (checkBox.isChecked()) {
                        selectedServiceModels.remove(serviceModel);
                        checkBox.setChecked(false);
                    } else {

                        //for clearing previously selected check boxes
                        for (int i = 0; i < count; i++) {
                            if (i != position) {
                                v = parent.getChildAt(i);
                                CheckBox checkBoxAll = (CheckBox) v.findViewById(R.id.checkBoxService);
                                checkBoxAll.setChecked(false);
                            }
                        }
                        selectedServiceModels.clear();

                        selectedServiceModels.add(serviceModel);
                        checkBox.setChecked(true);
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

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageService storageService = new StorageService(AdditionalServicesActivity.this);

        storageService.findDocsById(Config.customerModel.getStrCustomerID(),
                Config.collectionServices, new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) {

                serviceModels.clear();

                if (response.getJsonDocList().size() > 0) {

                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                    String strDocument = jsonDocument.getJsonDoc();

                    try {

                        JSONObject jsonObjectServcies = new JSONObject(strDocument);

                        if (jsonObjectServcies.has("services")) {

                            JSONArray jsonArray = jsonObjectServcies.getJSONArray("services");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                //JSONObject jsonObject = jsonArray.getJSONObject(i);

                                //JSONArray jsonArr= jsonObject.getJSONArray("features");
                                /*String[] arr=new String[jsonArr.length()];

                                for(int j=0;j<jsonArr.length();j++)
                                    arr[j]=jsonArr.getString(j);*/

                                /*ServiceModel serviceModel = new ServiceModel(
                                        jsonObject.getString("service_name"),
                                        jsonObject.getString("service_desc"),
                                        jsonArr,
                                        jsonObject.getDouble("cost"),
                                        jsonObject.getInt("unit"),
                                        jsonObject.getInt("service_id")
                                );*/

                                //serviceModels.add(serviceModel);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    additionalServicesAdapter.notifyDataSetChanged();

                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    utils.toast(2, 2, getString(R.string.error));
                }
            }

            @Override
            public void onInsertionFailed(App42Exception ex) {
            }

            @Override
            public void onFindDocFailed(App42Exception ex) {
                progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.error));
                Utils.log(ex.getMessage(), "");
            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {

            }
        });
    }
}
