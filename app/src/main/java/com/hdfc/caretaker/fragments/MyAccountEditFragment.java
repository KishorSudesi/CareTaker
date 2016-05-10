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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.RoundedImageView;
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

public class MyAccountEditFragment extends Fragment {

    public static String strCustomerImgNameCamera;
    public static String strCustomerImgName = "";
    public static Bitmap bitmap = null;
    public static Uri uri;
    private static Utils utils;
    private static RoundedImageView roundedImageView;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static boolean isImageChanged = false;
    private EditText name, number, editTextOldPassword, editTextPassword,
            editTextConfirmPassword, editDob, editAreaCode, editCountryCode;

    //city
    private String strName;
    private String strContactNo;
    private String strPass;
    private String strAddress;
    private String strOldPass;
    private String strCustomerImagePath = "", strAreaCode;

    private RadioButton mobile;
    private Spinner citizenship;

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

    public static MyAccountEditFragment newInstance() {
        MyAccountEditFragment fragment = new MyAccountEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        mail.setText(Config.customerModel.getStrEmail());

        //RelativeLayout loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        strCustomerImagePath = getActivity().getFilesDir() + "/images/" + Config.customerModel.getStrCustomerID();

        progressDialog = new ProgressDialog(getActivity());

        roundedImageView = (RoundedImageView) view.findViewById(R.id.imageView5);

        name = (EditText) view.findViewById(R.id.editTextGuruName);

        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);

        editTextOldPassword = (EditText) view.findViewById(R.id.editOldPassword);
        editTextPassword = (EditText) view.findViewById(R.id.editPassword);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.editConfirmPassword);
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

        citizenship = (Spinner) view.findViewById(R.id.input_citizenship);

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
        });

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", MyAccountEditFragment.this, null);
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

        citizenship.setSelection(Config.strCountries.indexOf(Config.customerModel.getStrCountryCode()));
        //

        txtViewHeader.setText(getActivity().getString(R.string.my_account_edit));
        Button goToDashBoard = (Button) view.findViewById(R.id.buttonSaveSetting);

        utils = new Utils(getActivity());

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //loadingPanel.setVisibility(View.VISIBLE);
        //

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
                editTextOldPassword.setError(null);
                editTextPassword.setError(null);
                editTextConfirmPassword.setError(null);

                editAreaCode.setError(null);
                editDob.setError(null);

                if (editAreaCode.getVisibility() == View.VISIBLE) {
                    strAreaCode = editAreaCode.getText().toString().trim();
                }

                final String strCountryCode = editCountryCode.getText().toString().trim();

                //strAddress = editAddress.getText().toString().trim();
                final String strDob = editDob.getText().toString().trim();
                final String strCountry = citizenship.getSelectedItem().toString().trim();

                strName = name.getText().toString().trim();
                strContactNo = number.getText().toString();
                //strAddress = city.getText().toString().trim();
                strOldPass = editTextOldPassword.getText().toString().trim();
                strPass = editTextPassword.getText().toString().trim();
                String strConfirmPass = editTextConfirmPassword.getText().toString().trim();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(strContactNo)) {
                    number.setError(getString(R.string.error_field_required));
                    focusView = number;
                    cancel = true;
                } else if (!utils.validCellPhone(strContactNo)) {
                    number.setError(getString(R.string.error_invalid_contact_no));
                    focusView = number;
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

                if (TextUtils.isEmpty(strCountry) || strCountry.equalsIgnoreCase("Select Country")) {
                    //editAddress.setError(getString(R.string.error_field_required));
                    focusView = citizenship;
                    cancel = true;
                    utils.toast(2, 2, getString(R.string.select_country));
                }

                if (!Utils.isEmpty(strOldPass) && !Utils.isEmpty(strPass) &&
                        !Utils.isEmpty(strConfirmPass)) {

                    if (!strPass.equalsIgnoreCase(strConfirmPass)) {
                        editTextConfirmPassword.setError(getString(R.string.error_confirm_password));
                        focusView = editTextConfirmPassword;
                        cancel = true;
                    }

                    if (strOldPass.equalsIgnoreCase(strPass)) {
                        editTextPassword.setError(getString(R.string.error_same_password));
                        focusView = editTextPassword;
                        cancel = true;
                    }
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
                }


                if (TextUtils.isEmpty(strName)) {
                    name.setError(getString(R.string.error_field_required));
                    focusView = name;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (utils.isConnectingToInternet()) {

                        progressDialog.setMessage(getActivity().getString(R.string.uploading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

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

                                if (editAreaCode.getVisibility() == View.VISIBLE)
                                    Config.customerModel.setStrCountryAreaCode(strAreaCode);

                                if (strPass != null && !strPass.equalsIgnoreCase("")) {

                                    if (utils.isConnectingToInternet()) {

                                        progressDialog.setMessage(getActivity().getString(R.string.verify_identity_password));

                                       /* try {
                                            strOldPass = AESCrypt.encrypt(Config.string, strOldPass);
                                            strPass = AESCrypt.encrypt(Config.string, strPass);
                                        } catch (GeneralSecurityException e) {
                                            e.printStackTrace();
                                        }*/

                                        UserService userService = new UserService(getActivity());

                                        userService.onChangePassword(Config.strUserName, strOldPass
                                                , strPass, new App42CallBack() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                progressDialog.dismiss();
                                                utils.toast(1, 1, getActivity().getString(R.string.account_updated));
                                                goToAccount();
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                                progressDialog.dismiss();
                                                try {
                                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                                    String strMess = jsonObjectError.getString("details");

                                                    utils.toast(2, 2, strMess);
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        });

                                    } else utils.toast(2, 2, getString(R.string.warning_internet));

                                } else {
                                    progressDialog.dismiss();
                                    utils.toast(1, 1, getActivity().getString(R.string.account_updated));
                                    goToAccount();
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                progressDialog.dismiss();
                                utils.toast(2, 2, e.getMessage());
                            }
                        });

                    } else utils.toast(2, 2, getString(R.string.warning_internet));
                }
            }
        });
        return view;
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

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                //Utils.toast(1, 1, "Getting Image...");
                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Utils.customerImageUri.getPath();
                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            Thread backgroundThreadGallery = new BackgroundThreadGallery();
                            backgroundThreadGallery.start();
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

                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();

                UploadService uploadService = new UploadService(getActivity());

               /* if (progressDialog.isShowing())
                    progressDialog.setProgress(1);
*/
                uploadService.removeImage(Config.strCustomerImageName,
                        Config.customerModel.getStrEmail(),
                            new App42CallBack() {
                        public void onSuccess(Object response) {

                            if(response!=null){
                                uploadImage();
                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                        @Override
                        public void onException(Exception e) {

                            if(e!=null) {

                                App42Exception exception = (App42Exception) e;
                                int appErrorCode = exception.getAppErrorCode();

                                if (appErrorCode != 1401 ) {
                                    uploadImage();
                                } else {
                                    utils.toast(2, 2, getString(R.string.error));
                                }

                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void uploadImage(){

        try {

            if (utils.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(getActivity());

                uploadService.uploadImageCommon(
                        strCustomerImgName,
                        Config.strCustomerImageName, "Profile Picture",
                        Config.customerModel.getStrEmail(),
                    UploadFileType.IMAGE, new App42CallBack() {
                        public void onSuccess(Object response) {

                            if(response!=null) {

                                Upload upload = (Upload) response;
                                ArrayList<Upload.File> fileList = upload.getFileList();

                                if (fileList.size() > 0 && bitmap != null) {

                                    Upload.File file = fileList.get(0);

                                    final String url = file.getUrl();

                                    JSONObject jsonToUpdate = new JSONObject();

                                    try {
                                        jsonToUpdate.put("customer_profile_url", url);

                                        StorageService storageService =
                                                new StorageService(getActivity());

                                        storageService.updateDocs(jsonToUpdate,
                                                Config.customerModel.getStrCustomerID(),
                                                Config.collectionCustomer, new App42CallBack() {

                                            @Override
                                            public void onSuccess(Object o) {

                                                if (o != null) {

                                                    try {
                                                        File f = utils.getInternalFileImages(
                                                                Config.customerModel.getStrCustomerID());

                                                        if (f.exists())
                                                            f.delete();

                                                        File newFile = new File(strCustomerImgName);
                                                        File renameFile = new File(
                                                                strCustomerImagePath);

                                                        utils.moveFile(newFile, renameFile);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    roundedImageView.setImageBitmap(bitmap);

                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();

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
                                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();

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

                                    //TODO Check Logic
                                } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.error));
                                    }
                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if(e!=null) {
                                Utils.log(e.toString(), "response");
                                utils.toast(2, 2, e.getMessage());
                            }else{
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (!isImageChanged) {
                if (bitmap != null)
                    roundedImageView.setImageBitmap(bitmap);
                else {
                    roundedImageView.setBackgroundDrawable(getActivity().getResources().
                            getDrawable(R.mipmap.camera_icon));
                    utils.toast(2, 2, getString(R.string.error));
                }
            }

            if (isImageChanged && bitmap != null) {
                try {
                    checkImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //loadingPanel.setVisibility(View.GONE);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
                Utils.log(f.getAbsolutePath(), " FP ");
                bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //
    public class BackgroundThreadGallery extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, galleryFile);
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                    isImageChanged = true;
                }
                threadHandler.sendEmptyMessage(0);
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
                    bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                    isImageChanged = true;
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
