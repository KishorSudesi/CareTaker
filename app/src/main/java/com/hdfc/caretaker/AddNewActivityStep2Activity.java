package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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

import java.io.File;
import java.util.Date;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    private static final String[] CARLAS = new String[]{"carla1@gmail.com"};
    public static String message, time;
    public static JSONObject jsonObjectCarla;
    private static Storage findObj;
    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    private static ImageView imageViewCarla;
    private static Bitmap bitmap;
    private static Handler threadHandler;
    private static String strSelectedCarla;
    private EditText editTextDate, editTextMessage;
    private TextView textView6, textView7;
    private Utils utils;
    private JSONObject jsonObjectAct, responseJSONDoc, jsonObjectActCarla, responseJSONDocCarla;
    private String _strDate;
    private String strCarlaJsonId, strCarlaImagepath;
    private String getStrSelectedCarla;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            String strDate = Utils.writeFormat.format(date);
            _strDate = Utils.readFormat.format(date);
            editTextDate.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_activity_step2);

        utils = new Utils(AddNewActivityStep2Activity.this);
        progressDialog = new ProgressDialog(AddNewActivityStep2Activity.this);

        Button cancelButton = (Button) findViewById(R.id.buttonBack);
        Button submitButtton = (Button) findViewById(R.id.button);
        editTextMessage = (EditText) findViewById(R.id.editText2);
        editTextDate = (EditText) findViewById(R.id.editTextDate);

        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);
        imageViewCarla = (ImageView) findViewById(R.id.carlaImage);

        getStrSelectedCarla = CARLAS[0]; //new Random().nextInt((1 - 0) + 1) + 0

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                //
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
                    }

                    if (TextUtils.isEmpty(time)) {
                        editTextDate.setError(getString(R.string.error_field_required));
                        focusView = editTextDate;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {

                        if (utils.isConnectingToInternet()) {

                            storageService = new StorageService(AddNewActivityStep2Activity.this);

                            jsonObjectAct = new JSONObject();
                            jsonObjectActCarla = new JSONObject();

                            progressDialog.setMessage(getResources().getString(R.string.loading));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if (AddNewActivityActivity.selectedDependentServiceModels.size() == 1) {

                                ServiceModel dependentServiceModel = AddNewActivityActivity.
                                        selectedDependentServiceModels.get(0);

                                try {

                                    if (jsonObjectCarla != null && jsonObjectCarla.has("provider_email")) {

                                        //for customer
                                        jsonObjectAct.put("provider_email", jsonObjectCarla.getString("provider_email"));
                                        jsonObjectAct.put("provider_contact_no", jsonObjectCarla.getString("provider_contact_no"));
                                        jsonObjectAct.put("provider_description", "description");
                                        jsonObjectAct.put("provider_name", jsonObjectCarla.getString("provider_name"));
                                        jsonObjectAct.put("provider_image_url", strCarlaImagepath);
                                        jsonObjectAct.put("activity_message", message);
                                        jsonObjectAct.put("status", "upcoming");
                                        jsonObjectAct.put("activity_name", dependentServiceModel.getStrServiceName());

                                        jsonObjectAct.put("activity_description", dependentServiceModel.getStrServiceDesc());
                                        jsonObjectAct.put("service_id", dependentServiceModel.getStrServiceId());
                                        //jsonObjectAct.put("features",dependentServiceModel.getJsonArrayFeatures());

                                        JSONArray jsonArray = new JSONArray();

                                        jsonObjectAct.put("features_done", jsonArray);

                                        jsonObjectAct.put("activity_date", _strDate);
                                        jsonObjectAct.put("activity_done_date", _strDate);


                                        jsonObjectAct.put("feedbacks", jsonArray);
                                        jsonObjectAct.put("videos", jsonArray);
                                        jsonObjectAct.put("images", jsonArray);

                                        //for provider
                                        jsonObjectActCarla.put("customer_email", Config.customerModel.getStrEmail());
                                        jsonObjectActCarla.put("activity_message", message);
                                        jsonObjectActCarla.put("activity_name", dependentServiceModel.getStrServiceName());

                                        jsonObjectActCarla.put("activity_description", dependentServiceModel.getStrServiceDesc());
                                        jsonObjectActCarla.put("service_id", dependentServiceModel.getStrServiceId());

                                        jsonObjectActCarla.put("features_done", jsonArray);

                                        jsonObjectActCarla.put("activity_date", _strDate);
                                        jsonObjectActCarla.put("activity_done_date", _strDate);

                                        jsonObjectActCarla.put("dependent_name", Config.dependentNames.get(Config.intSelectedDependent));
                                        jsonObjectActCarla.put("status", "upcoming");

                                        //jsonObjectActCarla.put("features",dependentServiceModel.getJsonArrayFeatures());

                                        jsonObjectActCarla.put("videos", jsonArray);
                                        jsonObjectActCarla.put("images", jsonArray);
                                        jsonObjectActCarla.put("feedbacks", jsonArray);

                                        //
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    jsonObjectAct = null;
                                }
                        }

                            if (jsonObjectAct != null) {

                                storageService.findDocsByIdApp42CallBack(
                                        Config.customerModel.getStrCustomerID(),
                                        Config.collectionCustomer, new App42CallBack() {
                                            @Override
                                            public void onSuccess(Object o) {

                                                if (o != null) {

                                                    findObj = (Storage) o;

                                                    try {
                                                        responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());
                                                        if (responseJSONDoc.has("dependents")) {
                                                            JSONArray dependantsA = responseJSONDoc.getJSONArray("dependents");
                                                            if (Config.intSelectedDependent <= dependantsA.length()) {
                                                                JSONObject dependantsObject = dependantsA.getJSONObject(Config.intSelectedDependent);

                                                                jsonObjectActCarla.put("dependent_image_url", dependantsObject.getString("dependent_profile_url"));

                                                                if (dependantsObject.has("activities")) {
                                                                    JSONArray activitiesA = dependantsObject.getJSONArray("activities");
                                                                    activitiesA.put(jsonObjectAct);

                                                                    //unit update
                                                                    if (dependantsObject.has("services")) {

                                                                        JSONArray jsonArrayServices = dependantsObject.getJSONArray("services");

                                                                        int iSize = jsonArrayServices.length();

                                                                        if (iSize > 0) {

                                                                            for (int j = 0; j < iSize; j++) {

                                                                                JSONObject jsonObjectService = jsonArrayServices.getJSONObject(j);

                                                                                if (jsonObjectService.getInt("service_id") == jsonObjectAct.getInt("service_id")) {

                                                                                    Double aDouble = Utils.round(jsonObjectService.getDouble("unit_consumed") + 1, 2);
                                                                                    jsonObjectService.put("unit_consumed",
                                                                                            aDouble);
                                                                                    jsonArrayServices.remove(j);
                                                                                    jsonArrayServices.put(jsonObjectService);
                                                                                }
                                                                }
                                                            }
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
                                                                Config.customerModel.getStrCustomerID()
                                                                , Config.collectionCustomer,
                                                                new App42CallBack() {
                                                                    @Override
                                                                    public void onSuccess(Object o) {

                                                                        if (o != null) {

                                                                            // Config.jsonObject = responseJSONDoc;

                                                                            storageService.findDocsByKeyValue(Config.collectionProvider, "provider_email", getStrSelectedCarla, new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                                                                            strCarlaJsonId = response.getJsonDocList().get(0).getDocId();

                                                                                            String strDocument = jsonDocument.getJsonDoc();

                                                                                            try {
                                                                                                responseJSONDocCarla = new JSONObject(strDocument);

                                                                                                if (responseJSONDocCarla.has("activities")) {
                                                                                                    JSONArray dependantsA = responseJSONDocCarla.getJSONArray("activities");
                                                                                                    dependantsA.put(jsonObjectActCarla);
                                                                                                }

                                                                                                //
                                                                                                storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionProvider, new App42CallBack() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Object o) {

                                                                                                        if (o != null) {
                                                                                                            Intent newIntent = new Intent(AddNewActivityStep2Activity.this, DashboardActivity.class);
                                                                                                            if (progressDialog.isShowing())
                                                                                                                progressDialog.dismiss();
                                                                                                            startActivity(newIntent);
                                                                                                            finish();

                                                                                                        } else {
                                                                                                            if (progressDialog.isShowing())
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

                                                } else {
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

                    }
                }
            });
        }
    }

    public void goBack(){
        Intent newIntent = new Intent(AddNewActivityStep2Activity.this, AddNewActivityActivity.class);
        startActivity(newIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        //
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

                if(response!=null) {

                    if (response.getJsonDocList().size() > 0) {

                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                        String strDocument = jsonDocument.getJsonDoc();

                        try {
                            jsonObjectCarla = new JSONObject(strDocument);
                            textView6.setText(jsonObjectCarla.getString("provider_name"));
                            textView7.setText(jsonObjectCarla.getString("provider_email"));

                            strSelectedCarla = utils.replaceSpace(jsonObjectCarla.getString("provider_name"));

                            strCarlaImagepath = jsonObjectCarla.getString("provider_profile_url").trim();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            threadHandler = new ThreadHandler();
                            Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();

                            progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }else{
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

                if(ex!=null) {
                    utils.toast(2, 2, ex.getMessage());
                }else{
                    utils.toast(2, 2, getString(R.string.warning_internet));
                }
            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (bitmap != null)
                imageViewCarla.setImageBitmap(bitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                if(strCarlaImagepath!=null&&!strCarlaImagepath.equalsIgnoreCase("")) {
                    utils.loadImageFromWeb(strSelectedCarla, strCarlaImagepath);
                    File f = utils.getInternalFileImages(strSelectedCarla);
                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intScreenWidth, Config.intHeight);
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }
}
