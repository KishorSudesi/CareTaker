package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hdfc.adapters.ActivityServicesAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.CategoryServiceModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewActivityActivity extends AppCompatActivity {

    public static ServiceModel selectedServiceModel = null;
    private static ProgressDialog progressDialog;
    private static List<String> strServcieIds = new ArrayList<>();
    private static List<CategoryServiceModel> categoryServiceModels = new ArrayList<>();
    private static List<String> strServiceCategoryNames = new ArrayList<>();
    private Utils utils;

    private List<String> listDataHeader = new ArrayList<>();
    private Map<String, List<ServiceModel>> listDataChild = new HashMap<>();

    private Button buttonContinue;

    private ExpandableListView listView;
    private ActivityServicesAdapter activityServicesAdapter;
    private int previousChildPosition = -1, previouGroupPosition = -1;
    private SessionManager sessionManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        utils = new Utils(AddNewActivityActivity.this);
        listView = (ExpandableListView) findViewById(R.id.listView1);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        ImageButton imageButtonBuyServices = (ImageButton) findViewById(R.id.imageButtonBuyServices);
        //ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);

        progressDialog = new ProgressDialog(AddNewActivityActivity.this);
        sessionManager = new SessionManager(this);
        strServcieIds.clear();
        categoryServiceModels.clear();
        strServiceCategoryNames.clear();

        if (listView != null) {
            listView.setEmptyView(textViewEmpty);

            // listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            activityServicesAdapter = new ActivityServicesAdapter(this, listDataChild,
                    listDataHeader);
            listView.setAdapter(activityServicesAdapter);

            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                   /* int count = parent.getChildCount();
                    View v1;

                    View v2 = parent.getChildAt(groupPosition);*/
                    if (!(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getStrServiceName().equalsIgnoreCase("All Check-In Services are Scheduled"))) {
                        if (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).isSelected()) {
                            listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setSelected(false);
                        } else {
                            listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setSelected(true);
                        }

                        if (previousChildPosition < 0 && previouGroupPosition < 0) {

                        } else if (previousChildPosition != childPosition || previouGroupPosition != groupPosition) {
                            listDataChild.get(listDataHeader.get(previouGroupPosition)).get(previousChildPosition).setSelected(false);
                        }
                        activityServicesAdapter.notifyDataSetChanged();
                        RadioButton checkBox = (RadioButton) v.findViewById(R.id.checkBoxService);
                        ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                        if (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).isSelected() || listDataChild.get(listDataHeader.get(previouGroupPosition)).get(previousChildPosition).isSelected()) {
                            selectedServiceModel = serviceModel;
                        } else {

                       /* //
                        for (int i = 0; i < count; i++) {
                                v1 = parent.getChildAt(i);
                                RadioButton checkBoxAll = (RadioButton) v1.findViewById(R.id.checkBoxService);
                                checkBoxAll.setChecked(false);
                        }
                        //
*/


                            selectedServiceModel = null;
                        }

                        previouGroupPosition = groupPosition;
                        previousChildPosition = childPosition;

                        if (selectedServiceModel == null)
                            buttonContinue.setVisibility(View.INVISIBLE);
                        else
                            buttonContinue.setVisibility(View.VISIBLE);


                    } else {
                        utils.toast(2, 2, "In Progress");
                    }
                    return false;
                }
            });
        }

        /*if (listView != null) {
            listView.setAdapter(activityServicesAdapter);
            listView.setEmptyView(textViewEmpty);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int count = parent.getChildCount();
                    View v;

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxService);
                    ServiceModel dependentServiceModel = (ServiceModel) checkBox.getTag();

                    if (checkBox.isChecked()) {
                        selectedDependentServiceModels.remove(dependentServiceModel);
                        checkBox.setChecked(false);
                    } else {

                        for (int i = 0; i < count; i++) {
                            if (i != position) {
                                v = parent.getChildAt(i);
                                CheckBox checkBoxAll = (CheckBox) v.findViewById(R.id.checkBoxService);
                                checkBoxAll.setChecked(false);
                            }
                        }
                        selectedDependentServiceModels.clear();

                        selectedDependentServiceModels.add(dependentServiceModel);
                        checkBox.setChecked(true);
                    }

                }
            });
        }*/

        ImageButton cancelButton = (ImageButton) findViewById(R.id.buttonBack);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        if (imageButtonBuyServices != null) {
            imageButtonBuyServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddNewActivityActivity.this, AdditionalServicesActivity.class);
                    startActivity(intent);
                }
            });
        }


        //
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (sessionManager.getServiceCustomer()) {
            try {

                // WHERE clause
                String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                // WHERE clause arguments
                String[] selectionArgs = {Config.collectionServiceCustomer};
                Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    do {
                        JSONObject jsonObjectServcies = new JSONObject(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));

                        // if (jsonObjectServcies.has("unit")) {
                        createActivityServiceModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), jsonObjectServcies);
                        //}
                    } while (cursor.moveToNext());
                    refreshAdapter();
                    cursor.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!utils.isConnectingToInternet()) {
            progressDialog.dismiss();
        }

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
            StorageService storageService = new StorageService(AddNewActivityActivity.this);
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
                                    // if (jsonObjectServcies.has("unit")) {
                                    String values[] = {strDocumentId, jsonDocument.getUpdatedAt(), strServices, Config.collectionServiceCustomer, "", "1", "", ""};
                                    if (sessionManager.getServiceCustomer()) {
                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {strDocumentId};
                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                    } else {
                                        createActivityServiceModel(strDocumentId,
                                                jsonObjectServcies);
                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                    }


                                    // }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            CareTaker.dbCon.dbTransactionSucessFull();
                        } else {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                        if (!sessionManager.getServiceCustomer()) {
                            sessionManager.saveServiceCustomer(true);

                            refreshAdapter();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        CareTaker.dbCon.endDBTransaction();

                    }
                }

                @Override
                public void onException(Exception e) {
                    try {
                        if (!sessionManager.getServiceCustomer()) {
                            refreshAdapter();
                        }
                        if (e != null) {
                            JSONObject jsonObject = new JSONObject(e.getMessage());
                            JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                            String strMess = jsonObjectError.getString("details");
                            //Utils.toast(2, 2, strMess);
                        } else {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });


//            storageService.findDocsByKeyValue(Config.collectionServiceCustomer, "customer_id",
//                    Config.customerModel.getStrCustomerID(),
//                    new AsyncApp42ServiceApi.App42StorageServiceListener() {
//
//                        @Override
//                        public void onDocumentInserted(Storage response) {
//                        }
//
//                        @Override
//                        public void onUpdateDocSuccess(Storage response) {
//                        }
//
//                        @Override
//                        public void onFindDocSuccess(Storage storage) {
//
//
//                        }
//
//                        @Override
//                        public void onInsertionFailed(App42Exception ex) {
//                        }
//
//                        @Override
//                        public void onFindDocFailed(App42Exception ex) {
//
//                        }
//
//                        @Override
//                        public void onUpdateDocFailed(App42Exception ex) {
//                        }
//                    });

        } else if (!sessionManager.getServiceCustomer()) {
            utils.toast(2, 2, getString(R.string.network_required));
        }


        //
    }

    public void goBack() {
        Intent newIntent = new Intent(AddNewActivityActivity.this, DashboardActivity.class);
        //Config.intSelectedMenu = Config.intActivityScreen;
        Bundle args = new Bundle();
        args.putBoolean(Config.strReload, false);
        newIntent.putExtras(args);
        startActivity(newIntent);
        finish();
    }

    public void refreshAdapter() {

        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (listView != null) {

                listDataHeader.clear();
                listDataChild.clear();

                for (CategoryServiceModel categoryServiceModel : categoryServiceModels) {

                    listDataHeader.add(categoryServiceModel.getStrCategoryName());
                    listDataChild.put(categoryServiceModel.getStrCategoryName(),
                            categoryServiceModel.getServiceModels());
                }

                activityServicesAdapter.notifyDataSetChanged();

                int count = activityServicesAdapter.getGroupCount();
                for (int i = 0; i < count; i++)
                    listView.expandGroup(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewActivityStep2(View v) {

        if (selectedServiceModel != null) {

            if ((selectedServiceModel.getiUnit() - selectedServiceModel.getiUnitUsed()) > 0) {

                if (!utils.isConnectingToInternet()) {
                    utils.toast(2, 2, getString(R.string.warning_internet));
                } else {
                    Intent newIntent = new Intent(AddNewActivityActivity.this, AddNewActivityStep2Activity.class);
                    startActivity(newIntent);
                }

                //finish();

            } else utils.toast(2, 2, getResources().getString(R.string.error_service_zero));

        } else utils.toast(2, 2, getResources().getString(R.string.error_service));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void createActivityServiceModel(String strDocumentId, JSONObject jsonObject) {

        try {
            ServiceModel serviceModel = new ServiceModel();

            serviceModel.setDoubleCost(jsonObject.getDouble("cost"));
            serviceModel.setStrServiceName(jsonObject.getString("service_name"));
            serviceModel.setiServiceNo(jsonObject.getInt("service_no"));
            serviceModel.setStrCategoryName(jsonObject.getString("category_name"));
            serviceModel.setiUnit(jsonObject.getInt("unit"));
            serviceModel.setiUnitUsed(jsonObject.getInt("unit_consumed"));
            //  serviceModel.setStrServiceType(jsonObject.getString("service_type"));
            serviceModel.setiUnitValue(jsonObject.getInt("unit_value"));

            if (jsonObject.has("milestones")) {

                JSONArray jsonArrayMilestones = jsonObject.
                        getJSONArray("milestones");

                for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                    JSONObject jsonObjectMilestone =
                            jsonArrayMilestones.getJSONObject(k);

                    MilestoneModel milestoneModel = new MilestoneModel();

                    milestoneModel.setiMilestoneId(jsonObjectMilestone.getInt("id"));
                    milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.getString("status"));
                    milestoneModel.setStrMilestoneName(jsonObjectMilestone.getString("name"));
                    milestoneModel.setStrMilestoneDate(jsonObjectMilestone.getString("date"));
                    //milestoneModel.setVisible(jsonObjectMilestone.getBoolean("show"));

                    if (jsonObjectMilestone.has("show")) {

                        try {
                            milestoneModel.setVisible(jsonObjectMilestone.getBoolean("show"));
                        } catch (Exception e) {
                            boolean b = true;
                            try {
                                if (jsonObjectMilestone.getInt("show") == 0)
                                    b = false;
                                milestoneModel.setVisible(b);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    if (jsonObjectMilestone.has("reschedule")) {

                        try {
                            milestoneModel.setReschedule(jsonObjectMilestone.getBoolean("reschedule"));
                        } catch (Exception e) {
                            boolean b = true;
                            try {
                                if (jsonObjectMilestone.getInt("reschedule") == 0)
                                    b = false;
                                milestoneModel.setReschedule(b);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    if (jsonObjectMilestone.has("scheduled_date"))
                        milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                getString("scheduled_date"));

                    if (jsonObjectMilestone.has("fields")) {

                        JSONArray jsonArrayFields = jsonObjectMilestone.
                                getJSONArray("fields");

                        for (int l = 0; l < jsonArrayFields.length(); l++) {

                            JSONObject jsonObjectField =
                                    jsonArrayFields.getJSONObject(l);

                            FieldModel fieldModel = new FieldModel();

                            fieldModel.setiFieldID(jsonObjectField.getInt("id"));

                            if (jsonObjectField.has("hide")) {

                                try {
                                    fieldModel.setFieldView(jsonObjectField.getBoolean("hide"));
                                } catch (Exception e) {
                                    int i;
                                    try {
                                        i = jsonObjectField.getInt("hide");
                                        if (i == 1)
                                            fieldModel.setFieldView(true);
                                        else
                                            fieldModel.setFieldView(false);

                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

//                            fieldModel.setFieldRequired(jsonObjectField.getBoolean("required"));
                            fieldModel.setStrFieldData(jsonObjectField.getString("data"));
                            fieldModel.setStrFieldLabel(jsonObjectField.getString("label"));
                            fieldModel.setStrFieldType(jsonObjectField.getString("type"));

                            if (jsonObjectField.has("values")) {

                                fieldModel.setStrFieldValues(utils.jsonToStringArray(jsonObjectField.
                                        getJSONArray("values")));
                            }


                            try {
                                if (jsonObjectField.has("child")) {
                                    boolean hasChild = false;
                                    Object aObj = jsonObjectField.get("child");
                                    if (aObj instanceof Integer) {
                                        hasChild = jsonObjectField.getInt("child") == 1 ? true : false;

                                    } else if (aObj instanceof Boolean) {
                                        hasChild = jsonObjectField.getBoolean("child");

                                    }

                                    fieldModel.setChild(hasChild);


                                    if (jsonObjectField.has("child_type"))
                                        fieldModel.setStrChildType(utils.jsonToStringArray(jsonObjectField.
                                                getJSONArray("child_type")));

                                    if (jsonObjectField.has("child_value"))
                                        fieldModel.setStrChildValue(utils.jsonToStringArray(jsonObjectField.
                                                getJSONArray("child_value")));

                                    if (jsonObjectField.has("child_condition"))
                                        fieldModel.setStrChildCondition(utils.jsonToStringArray(jsonObjectField.
                                                getJSONArray("child_condition")));

                                    if (jsonObjectField.has("child_field"))
                                        fieldModel.setiChildfieldID(utils.jsonToIntArray(jsonObjectField.
                                                getJSONArray("child_field")));
                                }
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (jsonObjectField.has("array_fields")) {

                                try {
                                    fieldModel.setiArrayCount(jsonObjectField.getInt("array_fields"));
                                } catch (Exception e) {
                                    int i = 0;
                                    try {
                                        i = Integer.parseInt(jsonObjectField.getString("array_fields"));
                                        fieldModel.setiArrayCount(i);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                if (jsonObjectField.has("array_type"))
                                    fieldModel.setStrArrayType(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("array_type")));

                                if (jsonObjectField.has("array_data"))
                                    fieldModel.setStrArrayData(jsonObjectField.getString("array_data"));

                            }
//
                            milestoneModel.setFieldModel(fieldModel);
                        }
                    }

                    serviceModel.setMilestoneModels(milestoneModel);
                }
            }

            serviceModel.setStrServiceId(jsonObject.getString("service_id"));

            if (!strServcieIds.contains(strDocumentId)) {
                strServcieIds.add(strDocumentId);

                if (!strServiceCategoryNames.contains(jsonObject.getString("category_name"))) {
                    strServiceCategoryNames.add(jsonObject.getString("category_name"));

                    CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
                    categoryServiceModel.setStrCategoryName(jsonObject.getString("category_name"));
                    categoryServiceModel.setServiceModels(serviceModel);

                    categoryServiceModels.add(categoryServiceModel);

                    //System.out.println("aniketttttttttttttttttttt"+jsonObject.getString("category_name"));
                } else {
                    int iPosition = strServiceCategoryNames.indexOf(jsonObject.getString("category_name"));
                    categoryServiceModels.get(iPosition).setServiceModels(serviceModel);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
