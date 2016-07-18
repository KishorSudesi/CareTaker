package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hdfc.adapters.CheckInCareAdapter;
import com.hdfc.config.Config;
import com.hdfc.models.CheckInCareActivityModel;
import com.hdfc.models.SubActivityModel;

import java.util.ArrayList;
import java.util.List;

public class CheckInCareActivity extends AppCompatActivity {

    public static ListView checkActivities;
    public static List<CheckInCareActivityModel> activity = new ArrayList<>();
    public static List<SubActivityModel> subActivityModels = new ArrayList<>();
    static List data;
    public ScrollView activities;
    public ImageButton backButton;
    public Button buttonHall, buttonKitchen, buttonWashroom, buttonBed;
    public TextView utilityBill, water, gas, electricity, phone, kitchenItems, grocery, maidServices, electronics, automobiles, homeAppliances;
    public String subActivityName, status, dueStatus, dueDate, utilityName;
    ArrayAdapter arrayAdapter;
    LinearLayout dialogLinear, linearImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_care);

        checkActivities = (ListView) findViewById(R.id.listCheckInCare);
        activities = (ScrollView) findViewById(R.id.scrollCheckInCare);
        backButton = (ImageButton) findViewById(R.id.buttonBackCheck);
        buttonHall = (Button) findViewById(R.id.btnHall);
        buttonKitchen = (Button) findViewById(R.id.btnKitchen);
        buttonWashroom = (Button) findViewById(R.id.btnWashroom);
        buttonBed = (Button) findViewById(R.id.btnBed);
        dialogLinear = (LinearLayout) findViewById(R.id.dialogLinear);
        linearImages = (LinearLayout) findViewById(R.id.LinearImages);
        utilityBill = (TextView) findViewById(R.id.tvWorkingStatus);
        water = (TextView) findViewById(R.id.tvWaterBill);
        gas = (TextView) findViewById(R.id.tvGasBill);
        electricity = (TextView) findViewById(R.id.tvElectricityBill);
        phone = (TextView) findViewById(R.id.tvPhoneBill);
        kitchenItems = (TextView) findViewById(R.id.tvKitchenItems);
        grocery = (TextView) findViewById(R.id.tvGrocery);
        maidServices = (TextView) findViewById(R.id.tvMaidServices);
        electronics = (TextView) findViewById(R.id.tvElectronics);
        automobiles = (TextView) findViewById(R.id.tvAutomobiles);
        homeAppliances = (TextView) findViewById(R.id.tvHomeAppliances);
        linearImages.setVisibility(View.GONE);
        activities.setVisibility(View.GONE);

        data = Config.checkInCareActivityNames;
        CheckInCareAdapter checkInCareAdapter = new CheckInCareAdapter(CheckInCareActivity.this, data);
        checkActivities.setAdapter(checkInCareAdapter);

        checkActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activity = Config.checkInCareActivityNames.get(position).getCheckInCareActivityModels();

                if (activity != null) {
                    for (int i = 0; i < activity.size(); i++) {

                        if (activity.get(i).getStrActivityName().equalsIgnoreCase("home_essentials")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();
                                if (utilityName.equalsIgnoreCase("water") && status != null) {
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("gas") && status != null) {
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("electricity") && status != null) {
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("telephone") && status != null) {
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("kitchen_equipments") && status != null) {
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("grocery") && status != null) {
                                    grocery.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    grocery.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                        }
                        if (activity.get(i).getStrActivityName().equalsIgnoreCase("domestic_help_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();

                                if (subActivityName.equalsIgnoreCase("maid_services") && status != null) {
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                        }
                        if (activity.get(i).getStrActivityName().equalsIgnoreCase("equipment_working_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();

                                if (subActivityName.equalsIgnoreCase("electronic") && status != null) {
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("home_appliances") && status != null) {
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("automobile") && status != null) {
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else {
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                        }

                    }
                }

                activities.setVisibility(View.VISIBLE);



            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInCareActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        buttonHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearImages.setVisibility(View.VISIBLE);
                AddImages();
            }
        });

    }

    public void AddImages() {
        ImageView imageView = new ImageView(CheckInCareActivity.this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.shri1));


        dialogLinear.addView(imageView);

    }
}
