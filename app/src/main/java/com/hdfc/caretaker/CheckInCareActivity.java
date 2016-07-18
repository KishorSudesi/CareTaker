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

    static List data;
    private static ListView checkActivities;
    private static List<CheckInCareActivityModel> activity = new ArrayList<>();
    private static List<SubActivityModel> subActivityModels = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    LinearLayout dialogLinear, linearImages;
    private ScrollView activities;
    private ImageButton backButton;
    private Button buttonHall, buttonKitchen, buttonWashroom, buttonBed;
    private TextView utilityBill, water, gas, electricity, phone, kitchenItems, grocery,
            maidServices, electronics, automobiles, homeAppliances, homeStatus, domesticStatus, workingStatus;
    private String subActivityName, status, dueStatus, dueDate, utilityName;
    private int homeEssCount = 0, domesticHelp = 0, equipmentStatus = 0, utilityBills = 0;

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
        utilityBill = (TextView) findViewById(R.id.tvUtilityBillStatus);
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
        homeStatus = (TextView) findViewById(R.id.tvHomeStatus);
        workingStatus = (TextView) findViewById(R.id.tvEquipmentStatus);
        domesticStatus = (TextView) findViewById(R.id.tvDomesticStatus);
        linearImages.setVisibility(View.GONE);
        activities.setVisibility(View.GONE);

        data = Config.checkInCareActivityNames;
        CheckInCareAdapter checkInCareAdapter = new CheckInCareAdapter(CheckInCareActivity.this, data);
        checkActivities.setAdapter(checkInCareAdapter);

        checkActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activity = Config.checkInCareActivityNames.get(position).getCheckInCareActivityModels();
                refreshData();
                if (activity != null) {
                    for (int i = 0; i < activity.size(); i++) {

                        if (activity.get(i).getStrActivityName().equalsIgnoreCase("home_essentials")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            homeEssCount = 0;
                            utilityBills = 0;
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();
                                if (utilityName.equalsIgnoreCase("water ") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (utilityName.equalsIgnoreCase("water") && status.equalsIgnoreCase("No")) {
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("gas") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (utilityName.equalsIgnoreCase("gas") && status.equalsIgnoreCase("No")) {
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("electricity") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (utilityName.equalsIgnoreCase("electricity") && status.equalsIgnoreCase("No")) {
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (utilityName.equalsIgnoreCase("telephone") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (utilityName.equalsIgnoreCase("telephone") && status.equalsIgnoreCase("No")) {
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("kitchen_equipments") && status != null && status.length() != 0) {
                                    homeEssCount++;
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("kitchen_equipments") && status == null && status.length() == 0) {
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("grocery") && status != null && status.length() != 0) {
                                    homeEssCount++;
                                    grocery.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("grocery") && status == null && status.length() == 0) {
                                    grocery.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                            if (homeEssCount == subActivityModels.size()) {
                                homeStatus.setText("Done");
                            } else {
                                homeStatus.setText("Pending");
                            }
                            if (utilityBills == 4) {
                                utilityBill.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                            } else {
                                utilityBill.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);
                            }
                        } else if (activity.get(i).getStrActivityName().equalsIgnoreCase("domestic_help_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            domesticHelp = 0;
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();

                                if (subActivityName.equalsIgnoreCase("maid_services") && status != null && status.length() != 0) {
                                    domesticHelp++;
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("maid_services") && status == null && status.length() == 0) {
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                            if (domesticHelp == subActivityModels.size()) {
                                domesticStatus.setText("Done");
                            } else {
                                domesticStatus.setText("Pending");
                            }
                        } else if (activity.get(i).getStrActivityName().equalsIgnoreCase("equipment_working_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            equipmentStatus = 0;
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();

                                if (subActivityName.equalsIgnoreCase("electronic") && status != null && status.length() != 0) {
                                    equipmentStatus++;
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("electronic") && status == null && status.length() == 0) {
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("home_appliances") && status != null && status.length() != 0) {
                                    equipmentStatus++;
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("home_appliances") && status == null && status.length() == 0) {
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                                if (subActivityName.equalsIgnoreCase("automobile") && status != null && status.length() != 0) {
                                    equipmentStatus++;
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick), null);
                                } else if (subActivityName.equalsIgnoreCase("automobile") && status == null && status.length() == 0) {
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

                                }
                            }
                            if (equipmentStatus == subActivityModels.size()) {
                                workingStatus.setText("Done");
                            } else {
                                workingStatus.setText("Pending");
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

    private void refreshData() {
        homeStatus.setText("Pending");
        domesticStatus.setText("Pending");
        workingStatus.setText("Pending");
        utilityBill.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);
        water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        grocery.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);


        maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

        automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.tick_disable), null);

    }

    public void AddImages() {
        ImageView imageView = new ImageView(CheckInCareActivity.this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.shri1));


        dialogLinear.addView(imageView);

    }
}
