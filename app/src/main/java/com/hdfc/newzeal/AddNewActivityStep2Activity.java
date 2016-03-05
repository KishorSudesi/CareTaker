package com.hdfc.newzeal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.DependentServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    public static String message, time;
    private static int intWhichScreen;
    private static Storage findObj;
    private static StorageService storageService;
    private EditText editTextDate, editTextMessage;
    private Libs libs;
    private ProgressDialog progressDialog;
    private JSONObject jsonObjectAct, responseJSONDoc;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            String strDate = fmt.format(date);
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

        Bundle b = getIntent().getExtras();
        intWhichScreen = b.getInt("WHICH_SCREEN", Config.intDashboardScreen);

        libs = new Libs(AddNewActivityStep2Activity.this);
        progressDialog = new ProgressDialog(AddNewActivityStep2Activity.this);

        Button cancelButton = (Button) findViewById(R.id.buttonBack);
        Button submitButtton = (Button) findViewById(R.id.button);
        editTextMessage = (EditText) findViewById(R.id.editText2);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityStep2Activity.this, AddNewActivityActivity.class);
                newIntent.putExtra("WHICH_SCREEN", intWhichScreen);
                startActivity(newIntent);
                finish();
            }
        });

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

                    if (libs.isConnectingToInternet()) {

                        storageService = new StorageService(AddNewActivityStep2Activity.this);

                        jsonObjectAct = new JSONObject();

                        progressDialog.setMessage(getResources().getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        if (AddNewActivityActivity.selectedDependentServiceModels.size() == 1) {

                            DependentServiceModel dependentServiceModel = AddNewActivityActivity.selectedDependentServiceModels.get(0);
                            try {
                                jsonObjectAct.put("provider_email", "provider@gmail.com");
                                jsonObjectAct.put("provider_contact_no", "1230432432");
                                jsonObjectAct.put("provider_description", "description");
                                jsonObjectAct.put("provider_name", "calra");
                                jsonObjectAct.put("activity_message", message);
                                jsonObjectAct.put("status", "upcoming");
                                jsonObjectAct.put("activity_name", dependentServiceModel.getStrDependantServiceName());
                                jsonObjectAct.put("activity_date", time);

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
                                            if (Config.intSelectedDependent <= dependantsA.length()) {
                                                JSONObject dependantsObject = dependantsA.getJSONObject(Config.intSelectedDependent);
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

                                    storageService.updateDocs(responseJSONDoc, Config.jsonDocId, Config.collectionName, new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Config.jsonObject = responseJSONDoc;
                                        progressDialog.dismiss();
                                            Intent newIntent = new Intent(AddNewActivityStep2Activity.this, DashboardActivity.class);
                                            newIntent.putExtra("WHICH_SCREEN", intWhichScreen);
                                            startActivity(newIntent);
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
                                    libs.toast(2, 2, e.getMessage());
                                    //libs.toast(2, 2, getString(R.string.error));
                                }
                            });
                        } else libs.toast(2, 2, getString(R.string.error));

                    } else libs.toast(2, 2, getString(R.string.warning_internet));

                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
