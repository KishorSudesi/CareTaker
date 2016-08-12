package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
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

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    private static final String[] CARLAS = new String[]{"carla1@gmail.com"};
    private static final int iActivityCreated = 1;
    public static String message, time;
    public static JSONObject jsonObjectCarla, jsonObject;
    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    private static ImageView imageViewCarla;
    private static Bitmap bitmapImg;
    private static Handler threadHandler;
    //private static String strSelectedCarla;
    private static int iUpdateFlag = 0;
    private static String strInsertedDocumentId;
    private static String strProviderId;
    private EditText editTextMessage;
    private TextView textView6, textView7, editTextDate;
    private Utils utils;
    private String strCarlaImagepath, _strDate, strDate, strAlert;
    private String getStrSelectedCarla, strPushMessage;
    private SessionManager sessionManager;
    private Context mContext;
    private Date selectedDate = null;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.
            selectedDate = date;
            strDate = Utils.writeFormat.format(date);
            _strDate = Utils.readFormat.format(date);
            editTextDate.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };
    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            bitmapImg = bitmap;

            progressDialog.dismiss();

            if (bitmap != null)
                imageViewCarla.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_activity_step2);

        iUpdateFlag = 0;
        mContext = this;

        utils = new Utils(AddNewActivityStep2Activity.this);
        progressDialog = new ProgressDialog(AddNewActivityStep2Activity.this);
        sessionManager = new SessionManager(AddNewActivityStep2Activity.this);

        ImageButton cancelButton = (ImageButton) findViewById(R.id.buttonBack);
        Button submitButtton = (Button) findViewById(R.id.button);
        editTextMessage = (EditText) findViewById(R.id.editText2);
        editTextDate = (TextView) findViewById(R.id.editTextDate);

        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);
        imageViewCarla = (ImageView) findViewById(R.id.carlaImage);

        TextView textViewLabel = (TextView) findViewById(R.id.textViewLabel);

        try {

            if (textViewLabel != null)
                textViewLabel.append(AddNewActivityActivity.selectedServiceModel.getStrServiceName());

            //    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.milestoneLayout);

            for (MilestoneModel milestoneModel : AddNewActivityActivity.selectedServiceModel.
                    getMilestoneModels()) {

                TextView textViewName = new TextView(AddNewActivityStep2Activity.this);
                //    textViewName.setTextAppearance(this, R.style.MilestoneStyle);
                textViewName.setText(milestoneModel.getStrMilestoneName());
                //      textViewName.setTextColor(getResources().getColor(R.color.colorWhite));
           /*     textViewName.setPadding(10, 10, 10, 10);
                textViewName.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_success));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                params.setMargins(10, 10, 10, 10);

                textViewName.setLayoutParams(params);
*/

                Utils.log(milestoneModel.getStrMilestoneName(), " MS ");
/*
                if (linearLayout != null) {
                    linearLayout.addView(textViewName);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getStrSelectedCarla = CARLAS[0]; //new Random().nextInt((1 - 0) + 1) + 0
//Date lastSeelectedDate=Utils.writeFormat.parse

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        if (submitButtton != null) {
            submitButtton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editTextMessage.setError(null);
                    editTextDate.setError(null);

                    boolean cancel = false;
                    View focusView = null;

                    message = editTextMessage.getText().toString();
                    time = editTextDate.getText().toString();

                    if (TextUtils.isEmpty(message)) {
                        editTextMessage.setError(getString(R.string.error_field_required));
                        focusView = editTextMessage;
                        cancel = true;
                        return;
                    }

                    if (TextUtils.isEmpty(time)) {
                        editTextDate.setError(getString(R.string.error_field_required));
                        focusView = editTextDate;
                        cancel = true;
                        return;
                    }


                    if (new Date().after(selectedDate)) {
                        editTextDate.setError(getString(R.string.error_wrong_date));
                        focusView = editTextDate;
                        cancel = true;
                        return;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {

                        if (utils.isConnectingToInternet()) {

                            storageService = new StorageService(AddNewActivityStep2Activity.this);

                            progressDialog.setMessage(getString(R.string.text_loader_processing));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if (iUpdateFlag != iActivityCreated)
                                addActivity(AddNewActivityActivity.selectedServiceModel);
                            if (iUpdateFlag == iActivityCreated)
                                fetchService(AddNewActivityActivity.selectedServiceModel);

                        } else utils.toast(2, 2, getString(R.string.warning_internet));

                    }
                }
            });
        }
    }

    private void goBack() {

        if (iUpdateFlag < iActivityCreated) {
            Intent newIntent = new Intent(AddNewActivityStep2Activity.this, AddNewActivityActivity.class);
            startActivity(newIntent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddNewActivityStep2Activity.this);
            builder.setTitle(getString(R.string.activity_not_saved));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (strInsertedDocumentId != null && !strInsertedDocumentId.equalsIgnoreCase(""))
                        deleteCreatedActivity();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (jsonObjectCarla == null || strProviderId == null ||
                (strProviderId != null && strProviderId.equalsIgnoreCase(""))) {
            progressDialog.setMessage(getResources().getString(R.string.text_loader_processing));
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageService storageService = new StorageService(AddNewActivityStep2Activity.this);

            storageService.findDocsByKeyValue(Config.collectionProvider, "provider_email",
                    getStrSelectedCarla, new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                    String strDocument = jsonDocument.getJsonDoc();

                                    try {
                                        jsonObjectCarla = new JSONObject(strDocument);
                                        textView6.setText(jsonObjectCarla.getString("provider_name"));
                                        textView7.setText(jsonObjectCarla.getString("provider_email"));
                                        //getStrSelectedCarla = jsonObjectCarla.getString("provider_email");
                                        //strSelectedCarla = utils.replaceSpace(jsonObjectCarla.getString("provider_name"));
                                        strCarlaImagepath = jsonObjectCarla.getString("provider_profile_url").trim();

                                        strProviderId = jsonDocument.getDocId();

                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();

//                                    threadHandler = new ThreadHandler();
//                                    Thread backgroundThread = new BackgroundThread();
//                                    backgroundThread.start();
                                        loadImageSimpleTarget(strCarlaImagepath);

                                        progressDialog.setMessage(getResources().getString(R.string.text_loader_processing));
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (ex != null) {
                                utils.toast(2, 2, ex.getMessage());
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        } else {
            try {
                textView6.setText(jsonObjectCarla.getString("provider_name"));
                textView7.setText(jsonObjectCarla.getString("provider_email"));
                //getStrSelectedCarla = jsonObjectCarla.getString("provider_email");
                //strSelectedCarla = utils.replaceSpace(jsonObjectCarla.getString("provider_name"));
                strCarlaImagepath = jsonObjectCarla.getString("provider_profile_url").trim();

                loadImageSimpleTarget(strCarlaImagepath);
                //strProviderId = jsonObjectCarla.getString("provider_id").trim();
                Utils.log(strProviderId, " strProviderId ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    public void addActivity(final ServiceModel serviceModel) {

        JSONObject jsonObjectServices = null;

        try {
            jsonObjectServices = new JSONObject();
            //String strDate = utils.convertDateToString(new Date());

            jsonObjectServices.put("service_id", serviceModel.getStrServiceId());
            jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
            jsonObjectServices.put("service_no", serviceModel.getiServiceNo());
            jsonObjectServices.put("service_type", serviceModel.getStrServiceType());
            jsonObjectServices.put("category_name", serviceModel.getStrCategoryName());

            jsonObjectServices.put("customer_id", Config.customerModel.getStrCustomerID());
            jsonObjectServices.put("dependent_id", Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID());
            jsonObjectServices.put("provider_id", strProviderId);
            jsonObjectServices.put("created_by", "customer");

            //todo check for unit_value update

            jsonObjectServices.put("status", "new");
            jsonObjectServices.put("provider_status", "new");
            jsonObjectServices.put("provider_message", "");

            jsonObjectServices.put("activity_date", _strDate);
            jsonObjectServices.put("activity_done_date", "");
            jsonObjectServices.put("activity_name", serviceModel.getStrServiceName());
            jsonObjectServices.put("activity_desc", message);
            jsonObjectServices.put("overdue", "false");

            JSONArray jsonArray = new JSONArray();

            jsonArray.put("{\"0\":\"empty\"}");

            jsonObjectServices.put("feedbacks", jsonArray);
            jsonObjectServices.put("videos", jsonArray);
            jsonObjectServices.put("images", jsonArray);

            String strDateNow = "";
            Calendar calendar = Calendar.getInstance();
            Date dateNow = calendar.getTime();
            strDateNow = utils.convertDateToString(dateNow);

            strPushMessage = getString(R.string.activity_create_notification_1)
                    + serviceModel.getStrCategoryName()
                    //+ getString(R.string.activity_create_notification_2)
                    + getString(R.string.activity_create_notification_3)
                    + serviceModel.getStrServiceName()
                    + getString(R.string.to)
                    + Config.dependentModels.get(Config.intSelectedDependent).getStrName()
                    + getString(R.string.has_created)
                    + getString(R.string.at) + strDate;

            jsonObject = new JSONObject();

            try {

                jsonObject.put("created_by", Config.customerModel.getStrCustomerID());
                jsonObject.put("time", strDateNow);
                jsonObject.put("user_type", "provider");
                jsonObject.put("user_id", strProviderId);
                jsonObject.put("created_by_type", "customer");
                jsonObject.put(App42GCMService.ExtraMessage, strPushMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /////////////////////


            JSONArray jsonArrayMilestones = new JSONArray();

            for (MilestoneModel milestoneModel : serviceModel.getMilestoneModels()) {

                JSONObject jsonObjectMilestone = new JSONObject();

                jsonObjectMilestone.put("id", milestoneModel.getiMilestoneId());
                jsonObjectMilestone.put("status", milestoneModel.getStrMilestoneStatus());
                jsonObjectMilestone.put("name", milestoneModel.getStrMilestoneName());
                jsonObjectMilestone.put("date", milestoneModel.getStrMilestoneDate());
                // jsonObjectMilestone.put("show",milestoneModel.isVisible());

                jsonObjectMilestone.put("show", milestoneModel.isVisible());
                jsonObjectMilestone.put("reschedule", milestoneModel.isReschedule());
                jsonObjectMilestone.put("scheduled_date", milestoneModel.getStrMilestoneScheduledDate());


                /*JSONArray jsonArray = new JSONArray();

                jsonArray.put("{\"0\":\"empty\"}");*/

                jsonObjectMilestone.put("files", jsonArray);

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
                    ////

                    jsonArrayFields.put(jsonObjectField);

                    jsonObjectMilestone.put("fields", jsonArrayFields);
                }
                jsonArrayMilestones.put(jsonObjectMilestone);
            }

            jsonObjectServices.put("milestones", jsonArrayMilestones);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (utils.isConnectingToInternet()) {
            storageService.insertDocs(jsonObjectServices,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response != null) {

                                    if (response.getJsonDocList().size() > 0) {
                                        strInsertedDocumentId = response.getJsonDocList().get(0).getDocId();
                                        iUpdateFlag = iActivityCreated;
                                        String strDoc = response.getJsonDocList().get(0).getJsonDoc();
                                        jsonObject.put("activity_id", strInsertedDocumentId);//todo add to care taker


                                        //
                                        Calendar calendar = Calendar.getInstance();

                                        Date startDate = null, endDate = null;
                                        String strStartDateCopy, strEndDateCopy;
                                        String strDateNow = "";
                                        Date activityDate = null;

                                        try {
                                            Date dateNow = calendar.getTime();
                                            Calendar cal = Calendar.getInstance();
                                            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

                                            Date lastDayOfMonth = cal.getTime();
                                            strEndDateCopy = Utils.writeFormatDateDB.format(lastDayOfMonth) + "T23:59:59.999Z";
                                            strStartDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T00:00:00.000Z";
                                            activityDate = utils.convertStringToDate(_strDate);

                                            endDate = utils.convertStringToDate(strEndDateCopy);
                                            startDate = utils.convertStringToDate(strStartDateCopy);

                                            Utils.log(String.valueOf(endDate + " ! " + startDate + " ! " + activityDate), " CRATED ");
                                            String values[] = {strInsertedDocumentId, response.getJsonDocList().get(0).getUpdatedAt(), strDoc, Config.collectionActivity, "", "1", _strDate, ""};


                                            CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                            sessionManager.saveActivityStatus(true);
                                            Storage.JSONDocument jsonDocument = response.
                                                    getJsonDocList().get(0);

                                            String strDocument = jsonDocument.getJsonDoc();
                                            String strActivityId = jsonDocument.getDocId();
                                            JSONObject jsonObjectActivity =
                                                    new JSONObject(strDocument);
                                            JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                            jsonObjectActivity.remove("milestones");
                                            strDocument = jsonObjectActivity.toString();
                                            for (int j = 0; j < jArray.length(); j++) {
                                                JSONObject jObj = jArray.optJSONObject(j);
                                                String strDocumentMilestone = jObj.toString();

                                                String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(0).getUpdatedAt(), strDocumentMilestone, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                CareTaker.dbCon.insert(DbHelper.strTableNameCollection, valuesMilestone, Config.names_collection_table);
                                            }
                                            if (activityDate.before(endDate) && activityDate.after(startDate)) {
                                                utils.createActivityModel(
                                                        strActivityId,
                                                        strDocument, 1,
                                                        jArray);

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //

                                        fetchService(serviceModel);
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.error));
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
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
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
                                utils.toast(2, 2, getString(R.string.error));
                            }
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    },
                    Config.collectionActivity);
        } else {

        }

    }

    public void fetchService(final ServiceModel serviceModel) {

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
                                    //57221947e4b0fa5b108f35dc
                                    //572b4f14e4b0492b68fbc9b1

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
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e1) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.error));
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
                                if (e != null) {
                                    utils.toast(2, 2, getString(R.string.error));
                                } else {
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

    public void updateServiceDependent(final String strDocumentId, final String strDocument,
                                       ServiceModel serviceModel) {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectServices = new JSONObject();
            String strDate = utils.convertDateToString(new Date());

            try {

                JSONObject jsonObject = new JSONObject(strDocument);

                //todo check single item units
                jsonObjectServices.put("updated_date", strDate);
                jsonObjectServices.put("unit", jsonObject.getInt("unit") -
                        serviceModel.getiUnitValue());

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
                                    insertNotification();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                utils.toast(2, 2, getString(R.string.error));
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onException(Exception ex) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
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
                                utils.toast(2, 2, getString(R.string.error));
                            }
                        }
                    });
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }
    ///////////////////////

    ///////////////////////
    private void insertNotification() {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    sendPushToProvider(getStrSelectedCarla, jsonObject.toString());
                                } else {
                                    strAlert = getString(R.string.no_push_actiity_added);
                                    goToActivityList(strAlert);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                goToActivityList(strAlert);
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
                            strAlert = getString(R.string.activity_added);

                            if (ex == null)
                                strAlert = getString(R.string.no_push_actiity_added);
                            goToActivityList(strAlert);
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    },
                    Config.collectionNotification);
        } else {
            strAlert = getString(R.string.no_push_actiity_added);

            goToActivityList(strAlert);
        }
    }

    public void sendPushToProvider(String strUserName, String strMessage) {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(AddNewActivityStep2Activity.this);

            pushNotificationService.sendPushToUser(strUserName, strMessage,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            strAlert = getString(R.string.activity_added);

                            if (o == null)
                                strAlert = getString(R.string.no_push_actiity_added);

                            goToActivityList(strAlert);
                        }

                        @Override
                        public void onException(Exception ex) {
                            strAlert = getString(R.string.no_push_actiity_added);

                            if (ex == null)
                                strAlert = getString(R.string.activity_added);

                            goToActivityList(strAlert);
                        }
                    });
        } else {
            strAlert = getString(R.string.no_push_actiity_added);

            goToActivityList(strAlert);
        }
    }

    public void deleteCreatedActivity() {

        if (utils.isConnectingToInternet()) {

            storageService.deleteDocById(Config.collectionActivity, strInsertedDocumentId,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (o != null) {
                                String selection = DbHelper.COLUMN_OBJECT_ID;
                                String selectionArgs[] = {strInsertedDocumentId};
                                CareTaker.dbCon.delete(Config.collectionActivity, selection, selectionArgs);
                                utils.toast(2, 2, getString(R.string.activity_deleted));
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                utils.toast(2, 2, getString(R.string.error));
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
    }

//    public static class ThreadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            progressDialog.dismiss();
//
//            if (bitmap != null)
//                imageViewCarla.setImageBitmap(bitmap);
//        }
//    }

    public void goToActivityList(String strMess) {

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        utils.toast(2, 2, strMess);

        Intent newIntent = new Intent(AddNewActivityStep2Activity.this, DashboardActivity.class);
        //Config.intSelectedMenu = Config.intListActivityScreen;

        Bundle args = new Bundle();
        args.putBoolean(Config.strReload, false);
        newIntent.putExtras(args);
        startActivity(newIntent);
        finish();
    }

    private void loadImageSimpleTarget(String url) {

        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.person_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

//    public class BackgroundThread extends Thread {
//        @Override
//        public void run() {
//            try {
//
//                if (strCarlaImagepath != null && !strCarlaImagepath.equalsIgnoreCase("")) {
//
//                    File f1 = utils.getInternalFileImages(strProviderId);
//
//                    if (f1.length() <= 0)
//                        utils.loadImageFromWeb(strProviderId, strCarlaImagepath);
//
//                    bitmap = utils.getBitmapFromFile(f1.getAbsolutePath(), Config.intScreenWidth, Config.intHeight);
//
//                }
//                threadHandler.sendEmptyMessage(0);
//            } catch (Exception | OutOfMemoryError e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
