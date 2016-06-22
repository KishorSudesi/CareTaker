package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.AdditionalServicesAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.CategoryServiceModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdditionalServicesActivity extends AppCompatActivity {

    public static List<ServiceModel> selectedServiceModels = new ArrayList<>();
    public static List<ServiceModel> selectedServiceHistoryModels = new ArrayList<>();

    public static AdditionalServicesAdapter additionalServicesAdapter;
    public static RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    private static StorageService storageService;
    //private static LinearLayout dynamicUserTab;
    private static int iServiceCount;
    private static boolean isUpdating;
    private Utils utils;
    private Button buttonContinue;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<ServiceModel>> listDataChild = new HashMap<>();
    public static ArrayList<String> serviceIds= new ArrayList<String>();

    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services);

        //Config.intSelectedDependent = 0;

        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        listView = (ExpandableListView) findViewById(R.id.listViewAdditionalServices);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);

        utils = new Utils(AdditionalServicesActivity.this);
        progressDialog = new ProgressDialog(AdditionalServicesActivity.this);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        //dynamicUserTab = (LinearLayout) findViewById(R.id.dynamicUserTab);

        // utils = new Utils(AdditionalServicesActivity.this);

        selectedServiceModels.clear();
        selectedServiceHistoryModels.clear();
        serviceIds.clear();

        isUpdating = false;
        buttonContinue.setTextColor(getResources().getColor(R.color.colorBlackDark));

        if (buttonContinue != null) {

            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedServiceModels.size() > 0) {

                        if (!isUpdating) {

                            if (utils.isConnectingToInternet()) {
                               /* progressDialog.setMessage(getResources().getString(R.string.loading));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
*/

                                loadingPanel.setVisibility(View.VISIBLE);
                                storageService = new StorageService(AdditionalServicesActivity.this);

                                iServiceCount = 0;
                                isUpdating = true;

                                addServices();
                            } else utils.toast(2, 2, getString(R.string.warning_internet));

                        } else utils.toast(2, 2, getString(R.string.existing_request));

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

        if (listView != null) {
            listView.setEmptyView(textViewEmpty);

            additionalServicesAdapter = new AdditionalServicesAdapter(this, listDataChild,
                    listDataHeader);
            listView.setAdapter(additionalServicesAdapter);


        }

        if (listView != null) {

            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!isUpdating) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxService);
                        ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                        if (checkBox.isChecked()) {
                            selectedServiceModels.remove(serviceModel);
                            checkBox.setChecked(false);
                            checkBox.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.check_off));
                        } else {
                            selectedServiceModels.add(serviceModel);
                            checkBox.setChecked(true);
                            checkBox.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.check_on));
                        }

                        if (selectedServiceModels.size() > 0 || selectedServiceHistoryModels.size() > 0)
                            buttonContinue.setEnabled(true);
                        else
                            buttonContinue.setEnabled(false);

                    } else resetUpdate();
                }
            });*/

            //
            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    if (!isUpdating) {
                        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBoxService);
                        ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                        if (checkBox.isChecked()) {
                            serviceIds.remove(serviceModel.getStrServiceId());
                            selectedServiceModels.remove(serviceModel);
                            checkBox.setChecked(false);
                            checkBox.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.tick_disable));
                        } else {
                            serviceIds.add(serviceModel.getStrServiceId());
                            selectedServiceModels.add(serviceModel);
                            checkBox.setChecked(true);
                            checkBox.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.tick));
                        }

                        if (selectedServiceModels.size() > 0 || selectedServiceHistoryModels.size() > 0) {
                            buttonContinue.setEnabled(true);
                            buttonContinue.setTextColor(getResources().getColor(R.color.colorWhite));
                        } else {
                            buttonContinue.setEnabled(false);
                            buttonContinue.setTextColor(getResources().getColor(R.color.colorBlackDark));
                        }

                    } else resetUpdate();

                    return false;
                }
            });
        }
    }

    public void resetUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalServicesActivity.this);
        builder.setTitle(getString(R.string.confirm_reset_update));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearCheckBox();
                isUpdating = false;
                selectedServiceModels.clear();
                selectedServiceHistoryModels.clear();
                buttonContinue.setEnabled(false);
                buttonContinue.setTextColor(getResources().getColor(R.color.colorBlackDark));
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                utils.toast(2, 2, getString(R.string.service_continue));
                buttonContinue.setEnabled(true);
                buttonContinue.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });
        builder.show();
    }

    public void goBack() {
        Intent dashboardIntent = new Intent(AdditionalServicesActivity.this,
                DashboardActivity.class);
        Config.intSelectedMenu = Config.intAccountScreen;
        startActivity(dashboardIntent);
        finish();
    }

    public void clearCheckBox() {
        try {
            CheckBox cb;

            for (int i = 0; i < listView.getChildCount(); i++) {
                cb = (CheckBox) listView.getChildAt(i).findViewById(R.id.checkBoxService);
                cb.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //utils.populateHeaderDependents(dynamicUserTab, Config.intServiceScreen);
/*
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        loadingPanel.setVisibility(View.VISIBLE);

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

        try {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            loadingPanel.setVisibility(View.GONE);

            if (listView != null) {

                listDataHeader.clear();
                listDataChild.clear();

                for (CategoryServiceModel categoryServiceModel : Config.categoryServiceModels) {
                    listDataHeader.add(categoryServiceModel.getStrCategoryName());
                    listDataChild.put(categoryServiceModel.getStrCategoryName(), categoryServiceModel.getServiceModels());
                }

                additionalServicesAdapter.notifyDataSetChanged();
                int count = additionalServicesAdapter.getGroupCount();
                for (int i = 0; i < count; i++)
                    listView.expandGroup(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addServiceHistory(final JSONObject jsonObject) {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response != null) {

                                    Utils.log(String.valueOf(selectedServiceHistoryModels.
                                            remove(iServiceCount)), " REMOVED ");

                                    if (selectedServiceHistoryModels.size() <= 0)
                                        serviceAdded();
                                    else
                                        addServicesHistory();

                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    loadingPanel.setVisibility(View.GONE);
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
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                loadingPanel.setVisibility(View.GONE);
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
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void updateServiceDependent(final String strDocumentId, final String strDocument,
                                       final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectServices = new JSONObject();
            String strDate = utils.convertDateToString(new Date());

            try {

                JSONObject jsonObject = new JSONObject(strDocument);

                //todo check to update this
                jsonObjectServices.put("updated_date", strDate);
                jsonObjectServices.put("unit", jsonObject.getInt("unit") + serviceModel.getiUnit());
                jsonObjectServices.put("unit_value", serviceModel.getiUnitValue());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            storageService.updateDocs(jsonObjectServices, strDocumentId,
                    Config.collectionServiceCustomer,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    Utils.log(String.valueOf(selectedServiceModels.
                                            remove(iServiceCount)), " REMOVED ");

                                    selectedServiceHistoryModels.add(serviceModel);

                                    //iServiceCount++;

                                    if (selectedServiceModels.size() <= 0) //iServiceCount ==
                                        addServicesHistory();
                                    else
                                        addServices();

                                } else {
                                   /* if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onException(Exception ex) {
                            try {
                               /* if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                loadingPanel.setVisibility(View.GONE);
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
           /* if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public JSONObject createServiceHistory(ServiceModel serviceModel) {

        JSONObject jsonObjectHistory = null;

        try {
            jsonObjectHistory = new JSONObject();

            String strDate = utils.convertDateToString(new Date());

            jsonObjectHistory.put("purchased_date", strDate);
            jsonObjectHistory.put("unit", serviceModel.getiUnit());
            jsonObjectHistory.put("unit_value", serviceModel.getiUnitValue());
            jsonObjectHistory.put("service_name", serviceModel.getStrServiceName());
            jsonObjectHistory.put("service_no", serviceModel.getiServiceNo());
            jsonObjectHistory.put("service_id", serviceModel.getStrServiceId());
            jsonObjectHistory.put("purchased_date", strDate);
            jsonObjectHistory.put("cost", serviceModel.getDoubleCost());
            jsonObjectHistory.put("category_name", serviceModel.getStrCategoryName());
            jsonObjectHistory.put("service_type", serviceModel.getStrServiceType());
            jsonObjectHistory.put("customer_id", Config.customerModel.getStrCustomerID());

            JSONArray jsonArrayMilestones = new JSONArray();

            for (MilestoneModel milestoneModel : serviceModel.getMilestoneModels()) {

                JSONObject jsonObjectMilestone = new JSONObject();

                jsonObjectMilestone.put("id", milestoneModel.getiMilestoneId());
                jsonObjectMilestone.put("status", milestoneModel.getStrMilestoneStatus());
                jsonObjectMilestone.put("name", milestoneModel.getStrMilestoneName());
                jsonObjectMilestone.put("date", milestoneModel.getStrMilestoneDate());
                jsonObjectMilestone.put("show",milestoneModel.isVisible());
                jsonObjectMilestone.put("scheduled_date",milestoneModel.getStrMilestoneScheduledDate());
                jsonObjectMilestone.put("reschedule",milestoneModel.isReschedule());

                JSONArray jsonArrayFields = new JSONArray();


                for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                    JSONObject jsonObjectField = new JSONObject();

                    jsonObjectField.put("id", fieldModel.getiFieldID());

                    if (fieldModel.isFieldView())
                        jsonObjectField.put("hide", fieldModel.isFieldView());

                    jsonObjectField.put("required", fieldModel.isFieldRequired());
                    jsonObjectField.put("data", fieldModel.getStrFieldData());
                    jsonObjectField.put("label", fieldModel.getStrFieldLabel());
                    jsonObjectField.put("type", fieldModel.getStrFieldType());

                    if (fieldModel.getStrFieldValues() != null && fieldModel.getStrFieldValues().length > 0)
                        jsonObjectField.put("values", utils.stringToJsonArray(fieldModel.getStrFieldValues()));

                    if (fieldModel.isChild()) {

                        jsonObjectField.put("child", fieldModel.isChild());

                        if (fieldModel.getStrChildType() != null && fieldModel.getStrChildType().length > 0)
                            jsonObjectField.put("child_type", utils.stringToJsonArray(fieldModel.getStrChildType()));

                        if (fieldModel.getStrChildValue() != null && fieldModel.getStrChildValue().length > 0)
                            jsonObjectField.put("child_value", utils.stringToJsonArray(fieldModel.getStrChildValue()));

                        if (fieldModel.getStrChildCondition() != null && fieldModel.getStrChildCondition().length > 0)
                            jsonObjectField.put("child_condition", utils.stringToJsonArray(fieldModel.getStrChildCondition()));

                        if (fieldModel.getiChildfieldID() != null && fieldModel.getiChildfieldID().length > 0)
                            jsonObjectField.put("child_field", utils.intToJsonArray(fieldModel.getiChildfieldID()));
                    }

                    //
                    if (fieldModel.getiArrayCount() > 0) {
                        jsonObjectField.put("array_fields", fieldModel.getiArrayCount());
                        jsonObjectField.put("array_type", utils.stringToJsonArray(fieldModel.getStrArrayType()));
                        jsonObjectField.put("array_data", fieldModel.getStrArrayData());
                    }
                    //

                    jsonArrayFields.put(jsonObjectField);

                    jsonObjectMilestone.put("fields", jsonArrayFields);
                }
                jsonArrayMilestones.put(jsonObjectMilestone);
            }

            jsonObjectHistory.put("milestones", jsonArrayMilestones);

            addServiceHistory(jsonObjectHistory);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectHistory;
    }


    public void addServiceDependent(final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectServices = null;

            try {
                jsonObjectServices = new JSONObject();
                String strDate = utils.convertDateToString(new Date());

                jsonObjectServices.put("unit", serviceModel.getiUnit());
                jsonObjectServices.put("unit_consumed", 0);
                jsonObjectServices.put("unit_value", serviceModel.getiUnitValue());
                jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
                jsonObjectServices.put("service_no", serviceModel.getiServiceNo());
                jsonObjectServices.put("service_id", serviceModel.getStrServiceId());
                jsonObjectServices.put("updated_date", strDate);
                jsonObjectServices.put("cost", serviceModel.getDoubleCost());
                jsonObjectServices.put("category_name", serviceModel.getStrCategoryName());
                jsonObjectServices.put("service_type", serviceModel.getStrServiceType());
                jsonObjectServices.put("customer_id", Config.customerModel.getStrCustomerID());

                JSONArray jsonArrayMilestones = new JSONArray();

                for (MilestoneModel milestoneModel : serviceModel.getMilestoneModels()) {

                    JSONObject jsonObjectMilestone = new JSONObject();

                    jsonObjectMilestone.put("id", milestoneModel.getiMilestoneId());
                    jsonObjectMilestone.put("status", milestoneModel.getStrMilestoneStatus());
                    jsonObjectMilestone.put("name", milestoneModel.getStrMilestoneName());
                    jsonObjectMilestone.put("date", milestoneModel.getStrMilestoneDate());
                    jsonObjectMilestone.put("show", milestoneModel.isVisible());
                    jsonObjectMilestone.put("scheduled_date",milestoneModel.getStrMilestoneScheduledDate());
                    jsonObjectMilestone.put("reschedule",milestoneModel.isReschedule());

                    JSONArray jsonArrayFields = new JSONArray();

                    for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                        JSONObject jsonObjectField = new JSONObject();

                        jsonObjectField.put("id", fieldModel.getiFieldID());

                        if (fieldModel.isFieldView())
                            jsonObjectField.put("hide", fieldModel.isFieldView());

                        jsonObjectField.put("required", fieldModel.isFieldRequired());
                        jsonObjectField.put("data", fieldModel.getStrFieldData());
                        jsonObjectField.put("label", fieldModel.getStrFieldLabel());
                        jsonObjectField.put("type", fieldModel.getStrFieldType());

                        if (fieldModel.getStrFieldValues() != null && fieldModel.getStrFieldValues().length > 0)
                            jsonObjectField.put("values", utils.stringToJsonArray(fieldModel.getStrFieldValues()));

                        if (fieldModel.isChild()) {

                            jsonObjectField.put("child", fieldModel.isChild());

                            if (fieldModel.getStrChildType() != null && fieldModel.getStrChildType().length > 0)
                                jsonObjectField.put("child_type", utils.stringToJsonArray(fieldModel.getStrChildType()));

                            if (fieldModel.getStrChildValue() != null && fieldModel.getStrChildValue().length > 0)
                                jsonObjectField.put("child_value", utils.stringToJsonArray(fieldModel.getStrChildValue()));

                            if (fieldModel.getStrChildCondition() != null && fieldModel.getStrChildCondition().length > 0)
                                jsonObjectField.put("child_condition", utils.stringToJsonArray(fieldModel.getStrChildCondition()));

                            if (fieldModel.getiChildfieldID() != null && fieldModel.getiChildfieldID().length > 0)
                                jsonObjectField.put("child_field", utils.intToJsonArray(fieldModel.getiChildfieldID()));
                        }

                        //
                        if (fieldModel.getiArrayCount() > 0) {
                            jsonObjectField.put("array_fields", fieldModel.getiArrayCount());
                            jsonObjectField.put("array_type", utils.stringToJsonArray(fieldModel.getStrArrayType()));
                            jsonObjectField.put("array_data", fieldModel.getStrFieldData());
                        }
                        //

                        jsonArrayFields.put(jsonObjectField);

                        jsonObjectMilestone.put("fields", jsonArrayFields);
                    }
                    jsonArrayMilestones.put(jsonObjectMilestone);
                }

                jsonObjectServices.put("milestones", jsonArrayMilestones);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            storageService.insertDocs(jsonObjectServices,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            if (response != null) {

                                /*Utils.log(String.valueOf(selectedServiceModels.
                                        remove(iServiceCount)), " REMOVED ");*/

                                selectedServiceHistoryModels.add(serviceModel);

                                if (selectedServiceModels.size() <= 0)
                                    addServicesHistory();
                                else
                                    addServices();

                            } else {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                loadingPanel.setVisibility(View.GONE);
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
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                loadingPanel.setVisibility(View.GONE);
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
                    Config.collectionServiceCustomer);

        } else {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void checkServiceExists(final ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            String key1 = "service_id";
            String value1 = serviceModel.getStrServiceId();

            String key2 = "customer_id";
            String value2 = Config.customerModel.getStrCustomerID();

            Query q1 = QueryBuilder.build(key1, value1, QueryBuilder.Operator.EQUALS);
            Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
            Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

            storageService.findDocsByQueryOrderBy(Config.collectionServiceCustomer, q3, 1, 0,
                    "updated_date", 1,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    Storage response = (Storage) o;

                                    if (response.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = response.
                                                getJsonDocList().get(0);

                                        String strDocument = jsonDocument.getJsonDoc();

                                        String strDocumentId = jsonDocument.getDocId();

                                        updateServiceDependent(strDocumentId, strDocument,
                                                serviceModel);
                                    }
                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    loadingPanel.setVisibility(View.GONE);
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
                                        /*if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

        } else {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void addServices() {

        if (selectedServiceModels.size() > 0) {

            storageService = new StorageService(AdditionalServicesActivity.this);
            ServiceModel serviceModel = selectedServiceModels.get(iServiceCount);

            try {
                checkServiceExists(serviceModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (selectedServiceModels.size() == 0)
                addServicesHistory();
        }
    }

    public void addServicesHistory() {

        if (selectedServiceHistoryModels.size() > 0) {

            storageService = new StorageService(AdditionalServicesActivity.this);
            ServiceModel serviceModel = selectedServiceHistoryModels.get(iServiceCount);

            try {
                createServiceHistory(serviceModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (selectedServiceHistoryModels.size() == 0)
                serviceAdded();
        }
    }


    public void serviceAdded() {
        /*if (progressDialog.isShowing())
            progressDialog.dismiss();*/
        loadingPanel.setVisibility(View.GONE);
        isUpdating = false;
        //buttonContinue.setEnabled(false);
        utils.toast(1, 1, getString(R.string.service_added));
        goBack();
    }



}
