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
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    public static String message, time;
    private static int intWhichScreen;
    private EditText editTextDate, editTextMessage;
    private Libs libs;
    private ProgressDialog progressDialog;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            SimpleDateFormat fmt = new SimpleDateFormat("DD-MM-yyyy HH:mm:ss", Locale.ENGLISH);
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

                        /*JSONObject jsonObjectAct = new JSONObject();

                        progressDialog.setMessage(getResources().getString(R.string.loading));
                        progressDialog.setCancelable(false);;
                        progressDialog.show();

                        for(int i=0;i<AddNewActivityActivity.selectedDependantServiceModels.size();i++){

                            DependantServiceModel dependantServiceModel = AddNewActivityActivity.selectedDependantServiceModels.get(i);
                            try {
                                jsonObjectAct.put("provider_email", "provider@gmail.com");
                                jsonObjectAct.put("provider_contact_no", "1230432432");
                                jsonObjectAct.put("provider_description", "description");
                                jsonObjectAct.put("provider_name", "calra");
                                jsonObjectAct.put("activity_message", message);
                                jsonObjectAct.put("status", "upcoming");
                                jsonObjectAct.put("activity_name", dependantServiceModel.getStrDependantServiceName());
                                jsonObjectAct.put("activity_date", time);

                                Libs.log(dependantServiceModel.getStrDependantServiceName(), " NEW ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        JSONObject jsonObject = new JSONObject();
                        try {

                            jsonObject.put("customer_email", Config.customerModel.getStrEmail());

                            JSONArray jsonArray = new JSONArray();

                            JSONObject jsonObjectDeps = new JSONObject();

                            JSONArray jsonArrayActivities = new JSONArray();


                            jsonArrayActivities.put(jsonObjectAct);

                            jsonObjectDeps.put("dependant_name", Config.dependantNames.get(Config.intSelectedDependant));

                            jsonObjectDeps.put("activities", jsonArrayActivities);

                            jsonArray.put(jsonObjectDeps);

                            jsonObject.put("dependants", jsonArray);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        StorageService storageService = new StorageService(AddNewActivityStep2Activity.this);

                        storageService.updateDocs(jsonObject, Config.jsonDocId, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                             Intent newIntent = new Intent(AddNewActivityStep2Activity.this, DashboardActivity.class);
                             newIntent.putExtra("WHICH_SCREEN", intWhichScreen);
                             startActivity(newIntent);
                             finish();

                            }

                            @Override
                            public void onException(Exception e) {
                                progressDialog.dismiss();
                                libs.toast(2,2, e.getMessage());
                            }
                        });*/


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
