package com.hdfc.caretaker.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.mikelau.countrypickerx.Country;
import com.mikelau.countrypickerx.CountryPickerCallbacks;
import com.mikelau.countrypickerx.CountryPickerDialog;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.tajchert.nammu.PermissionCallback;

public class MyAccountEditFragment extends Fragment {

    //public static String strCustomerImgNameCamera;
    public static String strCustomerImgName = "";
    public static Bitmap bitmapImg = null;
    public static Uri uri;
    private static Utils utils;
    //private static Handler threadHandler;
    private static boolean isImageChanged = false;
    private ImageView roundedImageView;
    private EditText name;
    private EditText number;
    private EditText editDob;
    private EditText editAreaCode;
    private EditText editCountryCode;

    private PermissionHelper permissionHelper;

    //city
    private String strName;
    private String strContactNo;
    //private String strPass;
    private String strCountryCode;

    //strAddress = editAddress.getText().toString().trim();
    private String strDob;
    private String strCountry;
    private String strAddress;
    //private String strOldPass;
    private String strAreaCode, imageUrl = ""; //strCustomerImagePath = "",

    private RadioButton mobile;
    //private Spinner citizenship;
    private TextView txtcountryname;
    private SessionManager sessionManager;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            String strDate = Utils.writeFormatActivityYear.format(date);
            editDob.setText(strDate);
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
            if (uri != null) {
                bitmapImg = utils.rotateBitmap(utils.getRealPathFromURI(uri), bitmapImg);
            }

            if (!isImageChanged) {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                if (bitmap != null)
                    roundedImageView.setImageBitmap(bitmap);
                else {
                    roundedImageView.setBackgroundDrawable(getActivity().getResources().
                            getDrawable(R.mipmap.camera));
                    utils.toast(2, 2, getString(R.string.error));
                }
            }
            if (isImageChanged) {
                if (bitmap != null)
                    checkImage();
                else {
                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                    roundedImageView.setBackgroundDrawable(getActivity().getResources().
                            getDrawable(R.mipmap.camera));
                    utils.toast(2, 2, getString(R.string.error));
                }
            }

        }
    };

    public static MyAccountEditFragment newInstance() {
        MyAccountEditFragment fragment = new MyAccountEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        permissionHelper.finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_edit, container, false);
        TextView txtViewHeader = (TextView) view.findViewById(R.id.header);
        TextView mail = (TextView) view.findViewById(R.id.textViewEmail);
        sessionManager = new SessionManager(getActivity());
        mail.setText(Config.customerModel.getStrEmail());

        permissionHelper = PermissionHelper.getInstance(getActivity());

        //RelativeLayout loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        //strCustomerImagePath = getActivity().getFilesDir() + "/images/" + Config.customerModel.getStrCustomerID();

        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        roundedImageView = (ImageView) view.findViewById(R.id.imageView5);

        name = (EditText) view.findViewById(R.id.editTextGuruName);

        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonBack);

      /*  EditText editTextOldPassword = (EditText) view.findViewById(R.id.editOldPassword);
        EditText editTextPassword = (EditText) view.findViewById(R.id.editPassword);
        EditText editTextConfirmPassword = (EditText) view.findViewById(R.id.editConfirmPassword);*/
        editDob = (EditText) view.findViewById(R.id.editDob);
        editAreaCode = (EditText) view.findViewById(R.id.editAreaCode);
        editCountryCode = (EditText) view.findViewById(R.id.editCountryCode);

        number = (EditText) view.findViewById(R.id.editContactNo);
        //city = (EditText) view.findViewById(R.id.editTextCity);

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setTimeShownStatus(false)
                        .build()
                        .show();
            }
        });


        mobile = (RadioButton) view.findViewById(R.id.radioMobile);
        //RadioButton landline = (RadioButton) rootView.findViewById(R.id.radioLandline);

        mobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mobile.isChecked()) {
                    editAreaCode.setVisibility(View.GONE);
                } else {
                    editAreaCode.setVisibility(View.VISIBLE);
                }
            }
        });

       /* citizenship = (Spinner) view.findViewById(R.id.input_citizenship);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, Config.countryNames);
        citizenship.setAdapter(adapter);
        citizenship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editCountryCode.setText(Config.countryAreaCodes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        txtcountryname = (TextView)view.findViewById(R.id.txtcountryname);

        txtcountryname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(getActivity(), new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                editCountryCode.setText(country.getDialingCode());
                                txtcountryname.setText(country.getCountryName(getActivity()));
                            }
                        }, false, 0);
                countryPicker.show();
            }
        });


        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    permissionHelper.verifyPermission(
                            new String[]{getString(R.string.permission_storage_rationale)},
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            new PermissionCallback() {
                                @Override
                                public void permissionGranted() {
                                    utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                                            + ".jpeg", MyAccountEditFragment.this, null);
                                }

                                @Override
                                public void permissionRefused() {
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        name.setText(Config.customerModel.getStrName());
        number.setText(Config.customerModel.getStrContacts());

       /* if (Config.customerModel.getStrAddress() != null)
            city.setText(Config.customerModel.getStrAddress());*/

        //
        editDob.setText(Config.customerModel.getStrDob());
        editCountryCode.setText(Config.customerModel.getStrCountryIsdCode());


        if (Config.customerModel.getStrCountryAreaCode() != null &&
                Config.customerModel.getStrCountryAreaCode().equalsIgnoreCase("")) {
            mobile.setChecked(true);
            editAreaCode.setVisibility(View.GONE);
        } else {
            editAreaCode.setVisibility(View.VISIBLE);
            editAreaCode.setText(Config.customerModel.getStrCountryAreaCode());
        }

        //citizenship.setSelection(Config.strCountries.indexOf(Config.customerModel.getStrCountryCode()));
        //

        txtViewHeader.setText(getActivity().getString(R.string.my_account_edit));
        Button goToDashBoard = (Button) view.findViewById(R.id.buttonSaveSetting);

        utils = new Utils(getActivity());

        //threadHandler = new ThreadHandler();
//        Thread backgroundThread = new BackgroundThread();
//        backgroundThread.start();

       /* progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        //loadingPanel.setVisibility(View.VISIBLE);
        //
        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
        imageUrl = Config.customerModel.getStrImgUrl();
        loadImageSimpleTarget(Config.customerModel.getStrImgUrl());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.intSelectedMenu = Config.intAccountScreen;
                MyAccountFragment fragment = MyAccountFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_dashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        goToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                name.setError(null);
                number.setError(null);
                //city.setError(null);

                editAreaCode.setError(null);
                editDob.setError(null);

                if (editAreaCode.getVisibility() == View.VISIBLE) {
                    strAreaCode = editAreaCode.getText().toString().trim();
                }

                strCountryCode = editCountryCode.getText().toString().trim();

                //strAddress = editAddress.getText().toString().trim();
                strDob = editDob.getText().toString().trim();
                strCountry = txtcountryname.getText().toString().trim();

                strName = name.getText().toString().trim();
                strContactNo = number.getText().toString();
                //strAddress = city.getText().toString().trim();


                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(strContactNo)) {
                    number.setError(getString(R.string.error_field_required));
                    focusView = number;
                    cancel = true;
                    return;
                } else if (!utils.validCellPhone(strContactNo)) {
                    number.setError(getString(R.string.error_invalid_contact_no));
                    focusView = number;
                    cancel = true;
                    return;
                }

                if (editAreaCode.getVisibility() == View.VISIBLE) {

                    if (TextUtils.isEmpty(strAreaCode)) {
                        editAreaCode.setError(getString(R.string.error_field_required));
                        focusView = editAreaCode;
                        cancel = true;
                    } else if (!utils.isValidAreaCode(strAreaCode)) {
                        editAreaCode.setError(getString(R.string.error_invalid_area_code));
                        focusView = editAreaCode;
                        cancel = true;
                    }
                }

                if (TextUtils.isEmpty(strCountry) || strCountry.equalsIgnoreCase("Select Country")) {
                    //editAddress.setError(getString(R.string.error_field_required));
                    focusView = txtcountryname;
                    cancel = true;
                    utils.toast(2, 2, getString(R.string.select_country));
                }



                /*if (TextUtils.isEmpty(strAddress)) {
                    city.setError(getString(R.string.error_field_required));
                    focusView = city;
                    cancel = true;
                }*/

                if (TextUtils.isEmpty(strDob)) {
                    editDob.setError(getString(R.string.error_field_required));
                    focusView = editDob;
                    cancel = true;
                    //return;
                }


                if (TextUtils.isEmpty(strName)) {
                    name.setError(getString(R.string.error_field_required));
                    focusView = name;
                    cancel = true;
                    //return;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (utils.isConnectingToInternet()) {

                        /*progressDialog.setMessage(getActivity().getString(R.string.uploading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();*/

                        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

                        StorageService storageService = new StorageService(getActivity());

                        JSONObject jsonToUpdate = new JSONObject();

                        try {
                            jsonToUpdate.put("customer_name", strName);
                            jsonToUpdate.put("customer_contact_no", strContactNo);
                            jsonToUpdate.put("customer_address", strCountry);

                            jsonToUpdate.put("customer_dob", strDob);
                            jsonToUpdate.put("customer_country", strCountry);
                            jsonToUpdate.put("customer_country_code", strCountryCode);

                            if (editAreaCode.getVisibility() == View.VISIBLE)
                                jsonToUpdate.put("customer_area_code", strAreaCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        storageService.updateDocs(jsonToUpdate,
                                Config.customerModel.getStrCustomerID(),
                                Config.collectionCustomer, new App42CallBack() {
                                    @Override
                                    public void onSuccess(Object o) {

                                        Config.customerModel.setStrAddress(strAddress);
                                        Config.customerModel.setStrContacts(strContactNo);
                                        Config.customerModel.setStrName(strName);

                                        Config.customerModel.setStrDob(strDob);
                                        Config.customerModel.setStrCountryCode(strCountry);
                                        Config.customerModel.setStrAddress(strCountry);
                                        Config.customerModel.setStrCountryIsdCode(strCountryCode);

                                        if (editAreaCode.getVisibility() == View.VISIBLE) {
                                            Config.customerModel.setStrCountryAreaCode(strAreaCode);
                                        }


                                        if (utils.isConnectingToInternet()) {

                                            updateUserDatainDB("false");
                                        }

                                    }

                                    @Override
                                    public void onException(Exception e) {
                                        //progressDialog.dismiss();
                                        updateUserDatainDB("true");
                                    }
                                });

                    }


                    if (!utils.isConnectingToInternet()) {


                        //} else {
                        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
                        updateUserDatainDB("true");
                    }
                }
            }
        });
        return view;
    }

    private void updateUserDatainDB(String isUpdated)

    {
        try {
            String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

            // WHERE clause arguments
            String[] selectionArgs = {Config.collectionCustomer};
            Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {

                    JSONObject jsonReceived = new JSONObject(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));

                    jsonReceived.put("customer_name", strName);
                    jsonReceived.put("customer_profile_url", imageUrl);
                    jsonReceived.put("customer_contact_no", strContactNo);
                    jsonReceived.put("customer_address", strCountry);

                    jsonReceived.put("customer_dob", strDob);
                    jsonReceived.put("customer_country", strCountry);
                    jsonReceived.put("customer_country_code", strCountryCode);

                    if (editAreaCode.getVisibility() == View.VISIBLE)
                        jsonReceived.put("customer_area_code", strAreaCode);
                    String strDocument = jsonReceived.toString();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());


                    String updatedAt = Utils.readFormat.format(c.getTime());

                    String values[] = {cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), updatedAt, strDocument, Config.collectionCustomer, "", "1", "", "" + isUpdated};
                    try {
                        //Config.jsonCustomer = new JSONObject(strDocument);

                        selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                        // WHERE clause arguments
                        String[] selectionArgsUpdate = {cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID))};
                        boolean isUpdatedDB = CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgsUpdate);
                        if (isUpdatedDB) {
                            Config.customerModel.setStrAddress(strAddress);
                            Config.customerModel.setStrContacts(strContactNo);
                            Config.customerModel.setStrName(strName);

                            Config.customerModel.setStrDob(strDob);
                            Config.customerModel.setStrCountryCode(strCountry);
                            Config.customerModel.setStrAddress(strCountry);
                            Config.customerModel.setStrCountryIsdCode(strCountryCode);


                            if (editAreaCode.getVisibility() == View.VISIBLE)
                                Config.customerModel.setStrCountryAreaCode(strAreaCode);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // createCustomerModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                } while (cursor.moveToNext());
                cursor.close();

                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                utils.toast(1, 1, getActivity().getString(R.string.account_updated));
                goToAccount();

            } else if (!utils.isConnectingToInternet()) {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                utils.toast(1, 1, "Not Updated");
                goToAccount();
            } else if (utils.isConnectingToInternet()) {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                utils.toast(1, 1, getActivity().getString(R.string.account_updated));
                goToAccount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageSimpleTarget(String url) {

        Glide.with(getActivity())
                .load(url)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.person_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

    private void loadImageSimpleTarget(Uri url) {

        Glide.with(getActivity())
                .load(url)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.person_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

    private void goToAccount() {
        MyAccountFragment myAccountFragment = MyAccountFragment.newInstance();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_dashboard, myAccountFragment);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        permissionHelper.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
                //Utils.toast(1, 1, "Getting Image...");
                /*progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();*/
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Utils.customerImageUri.getPath();
                        showImageFromCamera();
//                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
//                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            showImageFromGallery();

//                            Thread backgroundThreadGallery = new BackgroundThreadGallery();
//                            backgroundThreadGallery.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkImage() {

        try {

            if (utils.isConnectingToInternet()) {
/*
                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();*/

                UploadService uploadService = new UploadService(getActivity());

               /* if (progressDialog.isShowing())
                    progressDialog.setProgress(1);
*/
                uploadService.removeImage(Config.strCustomerImageName,
                        Config.customerModel.getStrEmail(),
                        new App42CallBack() {
                            public void onSuccess(Object response) {

                                if (response != null) {
                                    uploadImage();
                                } else {
                               /* if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                if (e != null) {

                                    App42Exception exception = (App42Exception) e;
                                    int appErrorCode = exception.getAppErrorCode();

                                    if (appErrorCode != 1401) {
                                        uploadImage();
                                    } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.error));
                                    }

                                } else {
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void uploadImage() {

        try {

            if (utils.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(getActivity());

                uploadService.uploadImageCommon(
                        strCustomerImgName,
                        Config.strCustomerImageName, "Profile Picture",
                        Config.customerModel.getStrEmail(),
                        UploadFileType.IMAGE, new App42CallBack() {
                            public void onSuccess(Object response) {

                                if (response != null) {

                                    Upload upload = (Upload) response;
                                    ArrayList<Upload.File> fileList = upload.getFileList();

                                    if (fileList.size() > 0 && bitmapImg != null) {

                                        Upload.File file = fileList.get(0);

                                        final String url = file.getUrl();

                                        JSONObject jsonToUpdate = new JSONObject();

                                        try {
                                            jsonToUpdate.put("customer_profile_url", url);
                                            imageUrl = url;

                                            StorageService storageService =
                                                    new StorageService(getActivity());

                                            storageService.updateDocs(jsonToUpdate,
                                                    Config.customerModel.getStrCustomerID(),
                                                    Config.collectionCustomer, new App42CallBack() {

                                                        @Override
                                                        public void onSuccess(Object o) {

                                                            if (o != null) {
                                                                Config.customerModel.setStrImgUrl(url);
                                                                utils.updateorInsertCustomerData(2, sessionManager.getPassword(), sessionManager.getEmail());
//                                                                String values[] = {jsonDocument.getDocId(), jsonDocument.getUpdatedAt(), strDocument, Config.collectionCustomer, "", "1", ""};
//                                                                try {
//                                                                    //Config.jsonCustomer = new JSONObject(strDocument);
//
//                                                                    if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {
//                                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";
//
//                                                                        // WHERE clause arguments
//                                                                        String[] selectionArgs = {jsonDocument.getDocId()};
//                                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
//                                                                    } else {
//                                                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
//
//                                                                    }
//
//
//
//
//                                                                } catch (Exception e) {
//                                                                    e.printStackTrace();
//                                                                }
//                                                                try {
//                                                                    File f = utils.getInternalFileImages(
//                                                                            Config.customerModel.getStrCustomerID());
//
//                                                                    if (f.exists())
//                                                                        f.delete();
//
//                                                                    File newFile = new File(strCustomerImgName);
//                                                                    File renameFile = new File(
//                                                                            strCustomerImagePath);
//
//                                                                    utils.moveFile(newFile, renameFile);
//
//                                                                } catch (Exception e) {
//                                                                    e.printStackTrace();
//                                                                }
//
                                                                roundedImageView.setImageBitmap(bitmapImg);

                                                    /*if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                                                DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                                                utils.toast(2, 2, getString(R.string.update_profile_image));

                                                   /* if (Config.jsonObject.has("customer_profile_url")) {

                                                        try {
                                                            Config.customerModel.put("customer_profile_url", url);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }*/
                                                                isImageChanged = false;

                                                            } else {
                                                    /*if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                                utils.toast(2, 2, getString(R.string.warning_internet));
                                                            }
                                                        }

                                                        @Override
                                                        public void onException(Exception e) {
                                                /*if (progressDialog.isShowing())
                                                    progressDialog.dismiss();*/
                                                            DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                                            if (e != null) {
                                                                Utils.log(e.toString(), "response");
                                                                utils.toast(2, 2, e.getMessage());
                                                            } else {
                                                                utils.toast(2, 2, getString(R.string.warning_internet));
                                                            }
                                                        }
                                                    });
                                            //

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        /*if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                               /* if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                            /*if (progressDialog.isShowing())
                                progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                if (e != null) {
                                    Utils.log(e.toString(), "response");
                                    utils.toast(2, 2, e.getMessage());
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                /*if (progressDialog.isShowing())
                    progressDialog.dismiss();*/

                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    private void showImageFromGallery() {
        try {
            if (uri != null) {
                Calendar calendar = new GregorianCalendar();
                String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                File galleryFile = utils.createFileInternalImage(strFileName);
                strCustomerImgName = galleryFile.getAbsolutePath();
                InputStream is = getActivity().getContentResolver().openInputStream(uri);
                utils.copyInputStreamToFile(is, galleryFile);
                //bitmapImg = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                //bitmapImg=utils.roundedBitmap(bitmapImg);
                loadImageSimpleTarget(uri);
                isImageChanged = true;
            }
            // threadHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public class BackgroundThread extends Thread {
//        @Override
//        public void run() {
//            try {
//
//                File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
//                Utils.log(f.getAbsolutePath(), " FP ");
//                bitmapImg = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
//
//                threadHandler.sendEmptyMessage(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //

    private void showImageFromCamera() {
        try {
            if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                //bitmapImg = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                loadImageSimpleTarget(strCustomerImgName);
                isImageChanged = true;
            }
            //threadHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    public class BackgroundThreadGallery extends Thread {
//        @Override
//        public void run() {
//
//            try {
//                if (uri != null) {
//                    Calendar calendar = new GregorianCalendar();
//                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
//                    File galleryFile = utils.createFileInternalImage(strFileName);
//                    strCustomerImgName = galleryFile.getAbsolutePath();
//                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
//                    utils.copyInputStreamToFile(is, galleryFile);
//                    bitmap = utils.getbitmapImgFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
//                    bitmapImg=utils.roundedBitmap(bitmapImg);
//                    isImageChanged = true;
//                }
//                threadHandler.sendEmptyMessage(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /*public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            *//*if (progressDialog.isShowing())
                progressDialog.dismiss();*//*


            if (!isImageChanged) {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                if (bitmapImg != null)
                    roundedImageView.setImageBitmap(bitmapImg);
                else {
                    roundedImageView.setBackgroundDrawable(getActivity().getResources().
                            getDrawable(R.mipmap.camera));
                    utils.toast(2, 2, getString(R.string.error));
                }
            }

            if (isImageChanged) {
                if (bitmapImg != null)
                    checkImage();
                else {
                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                    roundedImageView.setBackgroundDrawable(getActivity().getResources().
                            getDrawable(R.mipmap.camera));
                    utils.toast(2, 2, getString(R.string.error));
                }
            }

            //loadingPanel.setVisibility(View.GONE);
        }
    }*/

  /*  public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                    bitmapImg = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                    isImageChanged = true;
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

}
