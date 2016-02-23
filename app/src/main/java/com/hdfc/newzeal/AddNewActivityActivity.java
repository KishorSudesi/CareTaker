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

import com.hdfc.adapters.AddNewAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.DependantServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddNewActivityActivity extends AppCompatActivity {

    public static List<DependantServiceModel> dependantServiceModels = new ArrayList<DependantServiceModel>();
    public static List<DependantServiceModel> selectedDependantServiceModels = new ArrayList<DependantServiceModel>();
    private static ListView listView;
    private static int intWhichScreen;
    private ProgressDialog progressDialog;
    private AddNewAdapter addNewAdapter;
    private TextView textViewEmpty;
    private Button buttonContinue;
    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_add_new);

        libs = new Libs(AddNewActivityActivity.this);
        listView = (ListView) findViewById(R.id.listView1);
        textViewEmpty = (TextView) findViewById(android.R.id.empty);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        Bundle b = getIntent().getExtras();
        intWhichScreen = b.getInt("WHICH_SCREEN", Config.intDashboardScreen);

        progressDialog = new ProgressDialog(AddNewActivityActivity.this);

        addNewAdapter = new AddNewAdapter(this, dependantServiceModels);
        listView.setAdapter(addNewAdapter);
        listView.setEmptyView(textViewEmpty);

        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityActivity.this, DashboardActivity.class);
                newIntent.putExtra("WHICH_SCREEN", intWhichScreen);
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
                DependantServiceModel dependantServiceModel = (DependantServiceModel) checkBox.getTag();

                Libs.log(" IN ", " 1 ");

                if (checkBox.isChecked()) {
                    selectedDependantServiceModels.remove(dependantServiceModel);
                    checkBox.setChecked(false);
                    Libs.log(String.valueOf("O "), " ");
                } else {

                    //for clearing previously selected check boxes
                    Libs.log(String.valueOf(count), " ");
                    for (int i = 0; i < count; i++) {
                        if (i != position) {
                            v = parent.getChildAt(i);
                            CheckBox checkBoxAll = (CheckBox) v.findViewById(R.id.checkBoxService);
                            Libs.log(String.valueOf(i + " " + checkBoxAll.getTag().toString()), " ");
                            checkBoxAll.setChecked(false);
                        }
                    }
                    selectedDependantServiceModels.clear();

                    Libs.log(" IN ", " 2 ");

                    selectedDependantServiceModels.add(dependantServiceModel);
                    checkBox.setChecked(true);
                }

            }
        });

        // listView.setO
    }

    public void addNewActivityStep2(View v) {

        //if(selectedDependantServiceModels.size()>0) {
        Intent newIntent = new Intent(AddNewActivityActivity.this, AddNewActivityStep2Activity.class);
        newIntent.putExtra("WHICH_SCREEN", intWhichScreen);
        startActivity(newIntent);
        finish();
        //}else libs.toast(2,2, getResources().getString(R.string.error_service));
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

        dependantServiceModels.clear();
        selectedDependantServiceModels.clear();

        try {

            if (Config.jsonObject.has("customer_name")) {

                if (Config.jsonObject.has("dependants")) {

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependants");

                    JSONObject jsonObject = jsonArray.getJSONObject(Config.intSelectedDependant);

                    if (jsonObject.has("services")) {

                        JSONArray jsonArrayNotifications = jsonObject.getJSONArray("services");

                        for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                            JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                            DependantServiceModel dependantServiceModel = new DependantServiceModel(
                                    jsonObjectNotification.getString("service_name"),
                                    jsonObjectNotification.getString("service_features"),
                                    jsonObjectNotification.getInt("unit"),
                                    jsonObjectNotification.getInt("unit_consumed")
                            );

                            dependantServiceModels.add(dependantServiceModel);
                        }
                    }
                }
            }

            if (dependantServiceModels.size() > 0)
                buttonContinue.setVisibility(View.VISIBLE);

            addNewAdapter.notifyDataSetChanged();

            progressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
