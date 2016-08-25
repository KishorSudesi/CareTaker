package com.hdfc.caretaker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
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
import com.hdfc.libs.Utils;
import com.hdfc.models.CheckInCareActivityModel;
import com.hdfc.models.ImageModelCheck;
import com.hdfc.models.PictureModel;
import com.hdfc.models.SubActivityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckInCareActivity extends AppCompatActivity {

    static List data;
    private static ListView checkActivities;
    private static List<CheckInCareActivityModel> activity = new ArrayList<>();
    private static List<SubActivityModel> subActivityModels = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    LinearLayout dialogLinear, linearImages;
    private List<PictureModel> picture = new ArrayList<>();
    private List<ImageModelCheck> image = new ArrayList<>();
    private ScrollView activities;
    private ImageButton backButton;
    private Button buttonHall, buttonKitchen, buttonWashroom, buttonBed, water, gas, electricity, phone, kitchenItems, groceryItems, maidServices, electronics, automobiles, homeAppliances;
    private TextView utilityBill, domesticStatus, workingStatus, homeStatus,
            photoStatus, tvmediaComment, tvActivityByName, utilityBilsStatus, kitchenItemsStatus, groceryStatus, tvDriverStatus, tvCareRecipientsName;
    private String subActivityName, status, dueStatus, dueDate, utilityName, imageUrl, imageDescription, imageTime;
    private int homeEssCount = 0, domesticHelp = 0, equipmentStatus = 0, utilityBills = 0, imageCount = 0;
    private Utils utils;
    private boolean checkBoxStatus;
    private byte START_FROM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_care);

        utils = new Utils(CheckInCareActivity.this);

        if (getIntent().hasExtra(Config.KEY_START_FROM)) {
            START_FROM = getIntent().getByteExtra(Config.KEY_START_FROM, (byte) 0);
        }

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
        water = (Button) findViewById(R.id.tvWaterBill);
        gas = (Button) findViewById(R.id.tvGasBill);
        electricity = (Button) findViewById(R.id.tvElectricityBill);
        phone = (Button) findViewById(R.id.tvPhoneBill);
        kitchenItems = (Button) findViewById(R.id.tvKitchenItems);
        groceryItems = (Button) findViewById(R.id.tvGroceryItems);
        maidServices = (Button) findViewById(R.id.tvMaidServices);
        electronics = (Button) findViewById(R.id.tvElectronics);
        automobiles = (Button) findViewById(R.id.tvAutomobiles);
        homeAppliances = (Button) findViewById(R.id.tvHomeAppliances);
        homeStatus = (TextView) findViewById(R.id.tvHomeStatus);
        workingStatus = (TextView) findViewById(R.id.tvEquipmentStatus);
        domesticStatus = (TextView) findViewById(R.id.tvDomesticStatus);
        photoStatus = (TextView) findViewById(R.id.tvPhotosStatus);
        tvmediaComment = (TextView) findViewById(R.id.tvMediaComment);
        tvActivityByName = (TextView) findViewById(R.id.tvName);
        utilityBilsStatus = (TextView) findViewById(R.id.tvUtilityBills_Status);
        kitchenItemsStatus = (TextView) findViewById(R.id.tvKitchenItemsStatus);
        groceryStatus = (TextView) findViewById(R.id.tvGroceryStatus);
        tvDriverStatus = (Button) findViewById(R.id.tvDriverStatus);
        tvCareRecipientsName = (TextView) findViewById(R.id.tvCareRecipientsName);
        linearImages.setVisibility(View.GONE);
        activities.setVisibility(View.GONE);


        data = Config.checkInCareActivityNames;
        CheckInCareAdapter checkInCareAdapter = new CheckInCareAdapter(CheckInCareActivity.this, data);
        checkActivities.setAdapter(checkInCareAdapter);

        checkActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //picture = Config.roomtypeName.get(position).getImageModels();
                String providerName = "";
                try {
                    int iPosition = Config.strProviderIds.indexOf(Config.checkInCareActivityNames.get(position).getStrProviderID());
                    providerName = Config.providerModels.get(iPosition).getStrName();
                    tvActivityByName.setText(providerName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String dependentName = "";
                try {


                    int iPositionDep = Config.strDependentIds.indexOf(Config.checkInCareActivityNames.get(position).getStrDependentID());
                    dependentName = Config.dependentModels.get(iPositionDep).getStrName();
                    tvCareRecipientsName.setText(dependentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String mediaComment = Config.checkInCareActivityNames.get(position).getStrMediaComment();
                tvmediaComment.setText(mediaComment);
                activity = Config.checkInCareActivityNames.get(position).getCheckInCareActivityModels();
                refreshData();

                if (activity != null) {
                    for (int i = 0; i < activity.size(); i++) {

                        if (activity.get(i).getStrActivityName().equalsIgnoreCase("home_essentials")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            homeEssCount = 0;
                            utilityBills = 0;

                            for (int j = 0; j < subActivityModels.size(); j++) {
                                status = "";
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                dueStatus = subActivityModels.get(j).getStrDueStatus();
                                dueDate = subActivityModels.get(j).getStrDueDate();
                                utilityName = subActivityModels.get(j).getStrUtilityName();
                                status = status != null ? status.trim() : status;
                                checkBoxStatus = subActivityModels.get(j).isCheckBoxStatus();
                                if (utilityName.equalsIgnoreCase("water") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    water.setTag(dueDate);
                                    water.setClickable(false);
                                } else if (utilityName.equalsIgnoreCase("water") && status.equalsIgnoreCase("No")) {
                                    water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    water.setTag(dueDate);
                                    water.setClickable(true);

                                }
                                if (utilityName.equalsIgnoreCase("gas") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    gas.setTag(dueDate);
                                    gas.setClickable(false);
                                } else if (utilityName.equalsIgnoreCase("gas") && status.equalsIgnoreCase("No")) {
                                    gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    gas.setTag(dueDate);
                                    gas.setClickable(true);
                                }
                                if (utilityName.equalsIgnoreCase("electricity") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    electricity.setTag(dueDate);
                                    electricity.setClickable(false);
                                } else if (utilityName.equalsIgnoreCase("electricity") && status.equalsIgnoreCase("No")) {
                                    electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    electricity.setTag(dueDate);
                                    electricity.setClickable(true);
                                }
                                if (utilityName.equalsIgnoreCase("telephone") && status.equalsIgnoreCase("Yes")) {
                                    homeEssCount++;
                                    utilityBills++;
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    phone.setTag(dueDate);
                                    phone.setClickable(false);
                                } else if (utilityName.equalsIgnoreCase("telephone") && status.equalsIgnoreCase("No")) {
                                    phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    phone.setTag(dueDate);
                                    phone.setClickable(true);
                                }
                                if (subActivityName.equalsIgnoreCase("kitchen_equipments") && checkBoxStatus) {
                                    homeEssCount++;
                                    kitchenItemsStatus.setText("Done");
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    kitchenItems.setTag(status);
                                } else if (subActivityName.equalsIgnoreCase("kitchen_equipments") && !checkBoxStatus) {
                                    kitchenItemsStatus.setText("Pending");
                                    kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    kitchenItems.setTag(status);
                                }
                                if (subActivityName.equalsIgnoreCase("grocery") && checkBoxStatus) {
                                    homeEssCount++;
                                    groceryStatus.setText("Done");
                                    groceryItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    groceryItems.setTag(status);
                                } else if (subActivityName.equalsIgnoreCase("grocery") && !checkBoxStatus) {
                                    groceryStatus.setText("Pending");
                                    groceryItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    groceryItems.setTag(status);
                                }
                            }
                            if (homeEssCount == subActivityModels.size()) {
                                homeStatus.setText("Done");
                            } else {
                                homeStatus.setText("Pending");
                            }
                            if (utilityBills == 4) {
                                utilityBilsStatus.setText("Done");
                            } else {
                                utilityBilsStatus.setText("Pending");
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
                                checkBoxStatus = subActivityModels.get(j).isCheckBoxStatus();

                                if (subActivityName.equalsIgnoreCase("maid_services") && checkBoxStatus) {
                                    domesticHelp++;
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    maidServices.setTag(status);

                                } else if (subActivityName.equalsIgnoreCase("maid_services") && !checkBoxStatus) {
                                    maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    maidServices.setTag(status);
                                }

                                if (subActivityName.equalsIgnoreCase("driver_status") && checkBoxStatus) {
                                    domesticHelp++;
                                    tvDriverStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    tvDriverStatus.setTag(status);

                                } else if (subActivityName.equalsIgnoreCase("driver_status") && !checkBoxStatus) {
                                    tvDriverStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    tvDriverStatus.setTag(status);
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
                                checkBoxStatus = subActivityModels.get(j).isCheckBoxStatus();

                                if (subActivityName.equalsIgnoreCase("electronic") && checkBoxStatus) {
                                    equipmentStatus++;
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    electronics.setTag(status);

                                } else if (subActivityName.equalsIgnoreCase("electronic") && !checkBoxStatus) {
                                    electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    electronics.setTag(status);
                                }
                                if (subActivityName.equalsIgnoreCase("home_appliances") && checkBoxStatus) {
                                    equipmentStatus++;
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    homeAppliances.setTag(status);

                                } else if (subActivityName.equalsIgnoreCase("home_appliances") && !checkBoxStatus) {
                                    homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    homeAppliances.setTag(status);
                                }
                                if (subActivityName.equalsIgnoreCase("automobile") && checkBoxStatus) {
                                    equipmentStatus++;
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                    automobiles.setTag(status);

                                } else if (subActivityName.equalsIgnoreCase("automobile") && !checkBoxStatus) {
                                    automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                    automobiles.setTag(status);
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


                picture = Config.checkInCareActivityNames.get(position).getPictureModels();

                if (picture != null) {
                    imageCount = 0;
                    for (int x = 0; x < picture.size(); x++) {

                        if (picture.get(x).getStrRoomName().equalsIgnoreCase("hall")) {

                            final List<ImageModelCheck> hallImageModel = picture.get(x).getImageModels();
                            buttonHall.setClickable(true);
                            if (hallImageModel.size() > 0) {
                                imageCount++;
                                buttonHall.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                buttonHall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (hallImageModel != null && hallImageModel.size() > 0) {
                                            Intent intent = new Intent(CheckInCareActivity.this, CheckInImage.class);
                                            Bundle b = new Bundle();
                                            b.putSerializable("Pass", (Serializable) hallImageModel);
                                            b.putString("name", "Hall Check-In Service Images");
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        } else {
                                            utils.toast(2, 2, getString(R.string.no_photo));
                                        }
                                    }
                                });
                            } else {
                                buttonHall.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                buttonHall.setClickable(false);
                            }

                        }
                        if (picture.get(x).getStrRoomName().equalsIgnoreCase("kitchen")) {
                            final List<ImageModelCheck> kitchenImageModel = picture.get(x).getImageModels();
                            buttonKitchen.setClickable(true);
                            if (kitchenImageModel.size() > 0) {
                                imageCount++;
                                buttonKitchen.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                buttonKitchen.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (kitchenImageModel != null && kitchenImageModel.size() > 0) {
                                            Intent intent = new Intent(CheckInCareActivity.this, CheckInImage.class);
                                            Bundle b = new Bundle();
                                            b.putSerializable("Pass", (Serializable) kitchenImageModel);
                                            b.putString("name", "Kitchen Check-In Service Images");
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        } else {
                                            utils.toast(2, 2, getString(R.string.no_photo));
                                        }
                                    }
                                });
                            } else {
                                buttonKitchen.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                buttonKitchen.setClickable(false);
                            }

                        }
                        if (picture.get(x).getStrRoomName().equalsIgnoreCase("washroom")) {
                            final List<ImageModelCheck> washImageModel = picture.get(x).getImageModels();
                            buttonWashroom.setClickable(true);
                            if (washImageModel.size() > 0) {
                                imageCount++;
                                buttonWashroom.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                buttonWashroom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (washImageModel != null && washImageModel.size() > 0) {
                                            Intent intent = new Intent(CheckInCareActivity.this, CheckInImage.class);
                                            Bundle b = new Bundle();
                                            b.putSerializable("Pass", (Serializable) washImageModel);
                                            b.putString("name", "Washroom Check-In Service Images");
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        } else {
                                            utils.toast(2, 2, getString(R.string.no_photo));
                                        }
                                    }
                                });
                            } else {
                                buttonWashroom.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                buttonWashroom.setClickable(false);
                            }

                        }
                        if (picture.get(x).getStrRoomName().equalsIgnoreCase("bedroom")) {
                            final List<ImageModelCheck> bedImageModel = picture.get(x).getImageModels();

                            buttonBed.setClickable(true);
                            if (bedImageModel.size() > 0) {
                                imageCount++;
                                buttonBed.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_checkin), null);
                                buttonBed.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (bedImageModel != null && bedImageModel.size() > 0) {
                                            Intent intent = new Intent(CheckInCareActivity.this, CheckInImage.class);
                                            Bundle b = new Bundle();
                                            b.putSerializable("Pass", (Serializable) bedImageModel);
                                            b.putString("name", "Bedroom Check-In Service Images");
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        } else {
                                            utils.toast(2, 2, getString(R.string.no_photo));
                                        }
                                    }
                                });
                            } else {
                                buttonBed.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
                                buttonBed.setClickable(false);
                            }

                        }


                    }
                    if (imageCount == picture.size()) {
                        photoStatus.setText("Done");
                    } else {
                        photoStatus.setText("Pending");
                    }
                }


                activities.setVisibility(View.VISIBLE);


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        buttonHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearImages.setVisibility(View.VISIBLE);
                AddImages();
            }
        });
        automobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) v.getTag());
            }
        });
        homeAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) v.getTag());
            }
        });
        electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) electronics.getTag());
            }
        });
        maidServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) maidServices.getTag());
            }
        });

        tvDriverStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) tvDriverStatus.getTag());
            }
        });
        water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatusDueDate((String) water.getTag());
            }
        });
        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatusDueDate((String) gas.getTag());
            }
        });
        electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatusDueDate((String) electricity.getTag());
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatusDueDate((String) phone.getTag());
            }
        });
        groceryItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) groceryItems.getTag());
            }
        });
        kitchenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStatus((String) v.getTag());
            }
        });


    }

    private void refreshData() {

        status = "";

        homeStatus.setText("Pending");
        domesticStatus.setText("Pending");
        workingStatus.setText("Pending");
        utilityBilsStatus.setText("Pending");

        water.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        gas.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        electricity.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        kitchenItemsStatus.setText("Pending");

        groceryStatus.setText("Pending");
        kitchenItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
        groceryItems.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        maidServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);
        tvDriverStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        electronics.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        homeAppliances.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

        automobiles.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.tick_disable_checkin), null);

    }

    public void AddImages() {
        ImageView imageView = new ImageView(CheckInCareActivity.this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.person_icon));
        dialogLinear.addView(imageView);

    }

    public void DialogStatus(String message) {
        final Dialog dialog = new Dialog(CheckInCareActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView textView = (TextView) dialog.findViewById(R.id.tvCustom);
        Button buttonOk = (Button) dialog.findViewById(R.id.btnCustom);
        message = message.trim();
        if (message != null && message.length() != 0 && !(message.equalsIgnoreCase(""))) {
            tvTitle.setVisibility(View.VISIBLE);
            textView.setText(message);
        } else {
            tvTitle.setVisibility(View.GONE);
            textView.setText(getString(R.string.text_no_comments));
        }
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void DialogStatusDueDate(String message) {
        final Dialog dialog = new Dialog(CheckInCareActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_due_date);

        TextView textView = (TextView) dialog.findViewById(R.id.tvCustom);
        Button buttonOk = (Button) dialog.findViewById(R.id.btnCustom);
        textView.setText(message);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void goBack() {

        if (START_FROM == Config.START_FROM_DASHBOARD) {
            Config.intSelectedMenu = Config.intDashboardScreen;
        } else {
            Intent dashboardIntent = new Intent(CheckInCareActivity.this,
                    DashboardActivity.class);
            Config.intSelectedMenu = Config.intNotificationScreen;
            startActivity(dashboardIntent);
        }

        finish();
    }
}
