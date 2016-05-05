package com.hdfc.caretaker.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.CustomerModel;
import com.hdfc.views.CustomViewPager;
import com.hdfc.views.RoundedImageView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class GuruDetailsFragment extends Fragment {

    public static RoundedImageView imgButtonCamera;

    public static String strCustomerImgName = "";
    public static Bitmap bitmap = null;
    public static Uri uri;
    private static Thread backgroundThreadCamera;
    private static Handler backgroundThreadHandler;
    private static String strName, strEmail, strConfirmPass, strContactNo;//strAddress
    private static ProgressDialog mProgress = null;
    private EditText editName, editEmail, editPass, editConfirmPass, editContactNo, editTextDate, editAreaCode, editCountryCode;

    //editAddress
    private Utils utils;
    private RadioButton mobile;
    private Spinner citizenship;
    private String strAreaCode = "";

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            String strDate = Utils.writeFormatActivityYear.format(date);
            String _strDate = Utils.readFormat.format(date);
            editTextDate.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };
    public GuruDetailsFragment() {
    }

    public static GuruDetailsFragment newInstance() {
        GuruDetailsFragment fragment = new GuruDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_guru_details, container, false);
        imgButtonCamera = (RoundedImageView) rootView.findViewById(R.id.imageButtonCamera);

        editName = (EditText) rootView.findViewById(R.id.editName);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        editPass = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPass = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editContactNo = (EditText) rootView.findViewById(R.id.editContactNo);
        editAreaCode = (EditText)rootView.findViewById(R.id.editAreaCode);
        editCountryCode = (EditText)rootView.findViewById(R.id.editCountryCode);

        mobile = (RadioButton)rootView.findViewById(R.id.radioMobile);
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


       ///
        /*TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        editContactNo.setText( telephonyManager.getNetworkCountryIso());*/

        editTextDate = (EditText)rootView.findViewById(R.id.editDOB);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        Button buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);
        //editAddress = (EditText) rootView.findViewById(R.id.editAddress);

        mProgress = new ProgressDialog(getActivity());

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                utils.selectImage(String.valueOf(calendar.getTimeInMillis()) + ".jpeg", GuruDetailsFragment.this, null);
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });


        citizenship = (Spinner) rootView.findViewById(R.id.input_citizenship);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item, Config.countryNames);
        citizenship.setAdapter(adapter);
        citizenship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editCountryCode.setText(Config.countryAreaCodes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Config.customerModel != null && Config.customerModel.getStrName() != null
                && !Config.customerModel.getStrName().equalsIgnoreCase("")) {

            editName.setText(Config.customerModel.getStrName());
            editEmail.setText(Config.customerModel.getStrEmail());
            editContactNo.setText(Config.customerModel.getStrContacts());
            //editAddress.setText(Config.customerModel.getStrAddress());

            editTextDate.setText(Config.customerModel.getStrDob());
            editCountryCode.setText(Config.customerModel.getStrCountryIsdCode());


            if (Config.customerModel.getStrCountryAreaCode().equalsIgnoreCase("")) {
                mobile.setChecked(true);
                editAreaCode.setVisibility(View.GONE);
            } else {
                editAreaCode.setVisibility(View.VISIBLE);
                editAreaCode.setText(Config.customerModel.getStrCountryAreaCode());
            }

            citizenship.setSelection(Config.strCountries.indexOf(Config.customerModel.getStrCountryCode()));

            backgroundThreadHandler = new BackgroundThreadHandler();
            backgroundThreadCamera = new BackgroundThreadCamera();
            backgroundThreadCamera.start();
        }
    }

    private void validateUser() {

        editName.setError(null);
        editEmail.setError(null);
        editPass.setError(null);
        editConfirmPass.setError(null);
        editContactNo.setError(null);
        //editAddress.setError(null);
        editTextDate.setError(null);
        //citizenship.setError(null);
        editAreaCode.setError(null);
        //editCountryCode.setError(null);

        strName = editName.getText().toString().trim();
        strEmail = editEmail.getText().toString().trim();
        String strPass = editPass.getText().toString().trim();
        strConfirmPass = editConfirmPass.getText().toString().trim();
        strContactNo = editContactNo.getText().toString().trim();


        if (editAreaCode.getVisibility() == View.VISIBLE) {
            strAreaCode = editAreaCode.getText().toString().trim();
        }

        final String strCountryCode = editCountryCode.getText().toString().trim();

        //strAddress = editAddress.getText().toString().trim();
        final String strDob = editTextDate.getText().toString().trim();
        final String strCountry = citizenship.getSelectedItem().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strCustomerImgName)) {
            /* && Config.customerModel != null
                && Config.customerModel.getStrName().equalsIgnoreCase("")*/
            utils.toast(1, 1, getString(R.string.warning_profile_pic));
            focusView = imgButtonCamera;
            cancel = true;

        } else {

            if (TextUtils.isEmpty(strContactNo)) {
                editContactNo.setError(getString(R.string.error_field_required));
                focusView = editContactNo;
                cancel = true;
            } else if (!utils.validCellPhone(strContactNo)) {
                editContactNo.setError(getString(R.string.error_invalid_contact_no));
                focusView = editContactNo;
                cancel = true;
            }

            if (editAreaCode.getVisibility() == View.VISIBLE) {

                if (TextUtils.isEmpty(strAreaCode)) {
                    editAreaCode.setError(getString(R.string.error_field_required));
                    focusView = editAreaCode;
                    cancel = true;
                } else if (!utils.validCellPhone(strAreaCode)) {
                    editAreaCode.setError(getString(R.string.error_invalid_area_code));
                    focusView = editAreaCode;
                    cancel = true;
                }
            }

           /* if (TextUtils.isEmpty(strCountryCode)||strCountryCode.equalsIgnoreCase("0")) {
                editCountryCode.setError(getString(R.string.error_field_required));
                focusView = editCountryCode;
                cancel = true;
            }*/

            if (TextUtils.isEmpty(strCountry) || strCountry.equalsIgnoreCase("Select Country")) {
                //editAddress.setError(getString(R.string.error_field_required));
                focusView = citizenship;
                cancel = true;
                utils.toast(2, 2, getString(R.string.select_country));
            }

         /*   if (TextUtils.isEmpty(strAddress)) {
                editAddress.setError(getString(R.string.error_field_required));
                focusView = editAddress;
                cancel = true;
            }*/

            if (!TextUtils.isEmpty(strPass) && utils.isPasswordValid(strPass)
                    && !TextUtils.isEmpty(strConfirmPass) && utils.isPasswordValid(strConfirmPass)) {

                if (!strPass.trim().equalsIgnoreCase(strConfirmPass.trim())) {
                    editConfirmPass.setError(getString(R.string.error_confirm_password));
                    focusView = editConfirmPass;
                    cancel = true;
                }
            }

            if (TextUtils.isEmpty(strConfirmPass)) {
                editConfirmPass.setError(getString(R.string.error_field_required));
                focusView = editConfirmPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strPass)) {
                editPass.setError(getString(R.string.error_field_required));
                focusView = editPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(strDob)) {
                editTextDate.setError(getString(R.string.error_field_required));
                focusView = editTextDate;
                cancel = true;
            }

            if (TextUtils.isEmpty(strEmail)) {
                editEmail.setError(getString(R.string.error_field_required));
                focusView = editEmail;
                cancel = true;
            } else if (!utils.isEmailValid(strEmail)) {
                editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;
            }

            if (TextUtils.isEmpty(strName)) {
                editName.setError(getString(R.string.error_field_required));
                focusView = editName;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                //
                if (utils.isConnectingToInternet()) {

                    mProgress.setMessage(getResources().getString(R.string.loading));
                    mProgress.setCancelable(false);
                    mProgress.show();

                    UserService userService = new UserService(getActivity());

                    userService.getUser(strEmail, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            if (mProgress.isShowing())
                                mProgress.dismiss();
                            if(o!=null) {
                                utils.toast(2, 2, getString(R.string.email_exists));
                            }else{
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if(e!=null) {
                                String strMess = "";

                                try {
                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError =
                                            jsonObject.getJSONObject("app42Fault");
                                    strMess = jsonObjectError.getString("message");
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                if (!strMess.equalsIgnoreCase("")
                                        && strMess.equalsIgnoreCase("Not Found")) {

                                    CustomViewPager.setPagingEnabled(true);

                                    if (Config.customerModel != null
                                            && Config.customerModel.getStrName() != null
                                            && !Config.customerModel.getStrName().equalsIgnoreCase("")) {

                                        Config.customerModel.setStrName(strName);

                                        if (!Config.customerModel.getStrAddress().
                                                equalsIgnoreCase(""))
                                            //Config.customerModel.setStrAddress(strAddress);

                                        Config.customerModel.setStrContacts(strContactNo);
                                        Config.customerModel.setStrEmail(strEmail);

                                        if (!strCustomerImgName.equalsIgnoreCase(""))
                                            Config.customerModel.setStrImgPath(strCustomerImgName);

                                    } else {

                                        Config.customerModel = new CustomerModel(strName, "", "",
                                                "", strContactNo, strEmail, "",
                                                strCustomerImgName);

                                        //strAddress
                                    }

                                    Config.customerModel.setStrDob(strDob);
                                    Config.customerModel.setStrCountryCode(strCountry);
                                    Config.customerModel.setStrAddress(strCountry);
                                    Config.customerModel.setStrCountryIsdCode(strCountryCode);
                                    Config.customerModel.setStrCountryAreaCode(strAreaCode);

                                    if (strConfirmPass != null && !strConfirmPass.equalsIgnoreCase(""))
                                        SignupActivity.strCustomerPass = strConfirmPass;

                                    //chk this
                                    utils.retrieveDependants();//strEmail

                                    if (AddDependentFragment.adapter != null)
                                        AddDependentFragment.adapter.notifyDataSetChanged();

                                    utils.retrieveConfirmDependants();
                                    ConfirmFragment.adapter.notifyDataSetChanged();

                                    if (mProgress.isShowing())
                                        mProgress.dismiss();

                                    utils.toast(1, 1, getString(R.string.your_details_saved));

                                    SignupActivity._mViewPager.setCurrentItem(1);

                                }else {
                                    if (mProgress.isShowing())
                                        mProgress.dismiss();
                                    utils.toast(2, 2, getString(R.string.error));
                                }
                            }else{
                                if (mProgress.isShowing())
                                    mProgress.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });
                } else {
                    if (mProgress.isShowing())
                        mProgress.dismiss();
                    utils.toast(2, 2, getString(R.string.warning_internet));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                backgroundThreadHandler = new BackgroundThreadHandler();
                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Utils.customerImageUri.getPath();
                        backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            if (imgButtonCamera != null && strCustomerImgName != null
                    && !strCustomerImgName.equalsIgnoreCase("") && bitmap != null)
                imgButtonCamera.setImageBitmap(bitmap);
        }
    }

    //
    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {

                    Calendar calendar = Calendar.getInstance();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, galleryFile);
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
