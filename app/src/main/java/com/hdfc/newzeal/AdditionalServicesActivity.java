package com.hdfc.newzeal;

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
import com.hdfc.libs.Libs;
import com.hdfc.model.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdditionalServicesActivity extends AppCompatActivity {

    public static List<ServiceModel> serviceModels = new ArrayList<>();
    public static List<ServiceModel> selectedServiceModels = new ArrayList<>();
    public static AdditionalServicesAdapter additionalServicesAdapter;
    private static ProgressDialog progressDialog;
    private static StorageService storageService;
    private static Storage findObj;
    private Libs libs;
    private JSONObject jsonObjectAct, responseJSONDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);
        ListView listView = (ListView) findViewById(R.id.listViewAdditionalServices);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);

        libs = new Libs(AdditionalServicesActivity.this);
        progressDialog = new ProgressDialog(AdditionalServicesActivity.this);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (serviceModels.size() > 0) {

                    if (libs.isConnectingToInternet()) {

                        storageService = new StorageService(AdditionalServicesActivity.this);

                        jsonObjectAct = new JSONObject();

                        progressDialog.setMessage(getResources().getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        if (selectedServiceModels.size() == 1) {

                            ServiceModel serviceModel = selectedServiceModels.get(0);
                            try {

                                jsonObjectAct.put("unit", serviceModel.getDoubleUnit());
                                jsonObjectAct.put("unit_consumed", serviceModel.getDoubleUnit());
                                jsonObjectAct.put("service_name", serviceModel.getStrServiceName());
                                jsonObjectAct.put("service_features", serviceModel.getStrServiceFeatures());


                            } catch (JSONException e) {
                                e.printStackTrace();
                                jsonObjectAct = null;
                            }
                        }

                        if (jsonObjectAct != null) {

                            storageService.findDocsByIdApp42CallBack(Config.jsonDocId, Config.collectionName, new App42CallBack() {
                                @Override
                                public void onSuccess(Object o) {

                                    findObj = (Storage) o;

                                    try {
                                        responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());
                                        if (responseJSONDoc.has("dependents")) {
                                            JSONArray dependantsA = responseJSONDoc.getJSONArray("dependents");
                                            /*if (Config.intSelectedDependent <= dependantsA.length()) {
                                                JSONObject dependantsObject = dependantsA.getJSONObject(Config.intSelectedDependent);
                                                if (dependantsObject.has("services")) {
                                                    JSONArray activitiesA = dependantsObject.getJSONArray("services");
                                                    activitiesA.put(jsonObjectAct);
                                                }
                                            }*/
                                            for (int i = 0; i < dependantsA.length(); i++) {
                                                JSONObject dependantsObject = dependantsA.getJSONObject(i);
                                                if (dependantsObject.has("services")) {
                                                    JSONArray activitiesA = dependantsObject.getJSONArray("services");
                                                    activitiesA.put(jsonObjectAct);
                                                }
                                            }
                                        }
                                    } catch (JSONException jSe) {
                                        jSe.printStackTrace();
                                        progressDialog.dismiss();
                                    }

                                    Libs.log(responseJSONDoc.toString(), "DOC");

                                    storageService.updateDocs(responseJSONDoc, Config.jsonDocId, Config.collectionName, new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Config.jsonObject = responseJSONDoc;
                                            progressDialog.dismiss();
                                            Intent dashboardIntent = new Intent(AdditionalServicesActivity.this, DashboardActivity.class);
                                            dashboardIntent.putExtra("WHICH_SCREEN", Config.intAccountScreen);
                                            startActivity(dashboardIntent);
                                            finish();
                                        }

                                        @Override
                                        public void onException(Exception e) {
                                            progressDialog.dismiss();
                                            Libs.log(e.getMessage(), " ");
                                            libs.toast(2, 2, getString(R.string.error));
                                        }
                                    });
                                }

                                @Override
                                public void onException(Exception e) {
                                    progressDialog.dismiss();
                                    Libs.log(e.getMessage(), "");
                                    libs.toast(2, 2, getString(R.string.error));
                                }
                            });

                        } else libs.toast(2, 2, getString(R.string.error));

                    } else libs.toast(2, 2, getString(R.string.warning_internet));

                } else libs.toast(2, 2, getResources().getString(R.string.error_service));
            }
        });

        Button backButton = (Button) findViewById(R.id.buttonBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(AdditionalServicesActivity.this, DashboardActivity.class);
                dashboardIntent.putExtra("WHICH_SCREEN", Config.intAccountScreen);
                startActivity(dashboardIntent);
                finish();
            }
        });

        additionalServicesAdapter = new AdditionalServicesAdapter(this, serviceModels);
        listView.setAdapter(additionalServicesAdapter);
        listView.setEmptyView(textViewEmpty);

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageService storageService = new StorageService(AdditionalServicesActivity.this);

        storageService.findDocsById(Config.strServiceDocId, Config.collectionNameServices, new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ServiceModel serviceModel = new ServiceModel(
                                        jsonObject.getString("service_name"),
                                        jsonObject.getString("service_features"),
                                        jsonObject.getDouble("cost"),
                                        jsonObject.getDouble("unit")
                                );

                                serviceModels.add(serviceModel);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    additionalServicesAdapter.notifyDataSetChanged();

                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    libs.toast(2, 2, getString(R.string.error));
                }

            }

            @Override
            public void onInsertionFailed(App42Exception ex) {

            }

            @Override
            public void onFindDocFailed(App42Exception ex) {
                progressDialog.dismiss();
                libs.toast(2, 2, getString(R.string.error));
                Libs.log(ex.getMessage(), "");
            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {

            }
        });
    }
}
