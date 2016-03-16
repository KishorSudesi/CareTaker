package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.AddNewActivityAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddNewActivityActivity extends AppCompatActivity {

    public static List<ServiceModel> dependentServiceModels = new ArrayList<>();
    public static List<ServiceModel> selectedDependentServiceModels = new ArrayList<>();
    private static ProgressDialog progressDialog;
    private static AddNewActivityAdapter addNewActivityAdapter;
    private static Button buttonContinue;
    private static Handler threadHandler;
    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        libs = new Libs(AddNewActivityActivity.this);
        ListView listView = (ListView) findViewById(R.id.listView1);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        progressDialog = new ProgressDialog(AddNewActivityActivity.this);

        addNewActivityAdapter = new AddNewActivityAdapter(this, dependentServiceModels);
        listView.setAdapter(addNewActivityAdapter);
        listView.setEmptyView(textViewEmpty);

        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityActivity.this, DashboardActivity.class);
                startActivity(newIntent);
                finish();
            }
        });

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

        // listView.setO
    }

    public void addNewActivityStep2(View v) {

        if (selectedDependentServiceModels.size() > 0) {

            ServiceModel serviceModel = selectedDependentServiceModels.get(0);

            if((serviceModel.getiUnit()-serviceModel.getiUnitUsed())>0) {

                Intent newIntent = new Intent(AddNewActivityActivity.this, AddNewActivityStep2Activity.class);
                startActivity(newIntent);
                finish();
            }else libs.toast(2, 2, getResources().getString(R.string.error_service_zero));

        } else libs.toast(2, 2, getResources().getString(R.string.error_service));
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

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (dependentServiceModels.size() > 0)
                buttonContinue.setVisibility(View.VISIBLE);

            addNewActivityAdapter.notifyDataSetChanged();

            progressDialog.dismiss();
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {

                dependentServiceModels.clear();
                selectedDependentServiceModels.clear();

                if (Config.jsonObject.has("customer_name")) {

                    if (Config.jsonObject.has("dependents")) {

                        JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                        JSONObject jsonObject = jsonArray.getJSONObject(Config.intSelectedDependent);

                        if (jsonObject.has("services")) {

                            JSONArray jsonArrayNotifications = jsonObject.getJSONArray("services");

                            for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                                JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                                if(jsonObjectNotification.has("service_name")) {

                                    ServiceModel serviceModel = new ServiceModel(
                                            jsonObjectNotification.getString("service_name"),
                                            jsonObjectNotification.getInt("service_id"),
                                            jsonObjectNotification.getInt("unit"),
                                            jsonObjectNotification.getInt("unit_consumed"),
                                            jsonObjectNotification.getJSONArray("service_features"),
                                            jsonObjectNotification.getString("service_desc")
                                    );

                                    dependentServiceModels.add(serviceModel);
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }
}
