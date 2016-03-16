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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.caretaker.R;
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

    private EditText name, number, city, editTextOldPassword, editTextPassword, editTextConfirmPassword;
    private static Libs libs;

    private String strName;
    private String strContactNo;
    private String strPass;
    private String strAddress;
    private String strOldPass;
    private String strCustomerImagePath="";

    private static RoundedImageView roundedImageView;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    public static String strCustomerImgNameCamera;
    public static String strCustomerImgName = "";

    private static boolean isImageChanged=false;

    public static Bitmap bitmap = null;
    public static Uri uri;

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

        strCustomerImagePath=getActivity().getFilesDir()+"/images/"+Config.strCustomerImageName;

        progressDialog = new ProgressDialog(getActivity());

        roundedImageView = (RoundedImageView) view.findViewById(R.id.imageView5);

        name = (EditText) view.findViewById(R.id.editTextGuruName);

        editTextOldPassword = (EditText) view.findViewById(R.id.editOldPassword);
        editTextPassword = (EditText) view.findViewById(R.id.editPassword);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.editConfirmPassword);

        number = (EditText) view.findViewById(R.id.editTextNumber);
        city = (EditText) view.findViewById(R.id.editTextCity);

        strCustomerImgNameCamera = String.valueOf(new Date().getDate() + "" + new Date().getTime()) + ".jpeg";

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libs.selectImage(strCustomerImgNameCamera, MyAccountEditFragment.this, null);
            }
        });

        name.setText(Config.customerModel.getStrName());
        number.setText(Config.customerModel.getStrContacts());

        if (Config.customerModel.getStrAddress() != null)
            city.setText(Config.customerModel.getStrAddress());

        txtViewHeader.setText(getActivity().getString(R.string.my_account_edit));
        Button goToDashBoard = (Button) view.findViewById(R.id.buttonSaveSetting);

        libs = new Libs(getActivity());

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //loadingPanel.setVisibility(View.VISIBLE);
        //

        goToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                name.setError(null);
                number.setError(null);
                city.setError(null);
                editTextOldPassword.setError(null);
                editTextPassword.setError(null);
                editTextConfirmPassword.setError(null);

                strName = name.getText().toString().trim();
                strContactNo = number.getText().toString();
                strAddress = city.getText().toString().trim();
                strOldPass = editTextOldPassword.getText().toString().trim();
                strPass = editTextPassword.getText().toString().trim();
                String strConfirmPass = editTextConfirmPassword.getText().toString().trim();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(strContactNo)) {
                    number.setError(getString(R.string.error_field_required));
                    focusView = number;
                    cancel = true;
                } else if (!libs.validCellPhone(strContactNo)) {
                    number.setError(getString(R.string.error_invalid_contact_no));
                    focusView = number;
                    cancel = true;
                }

                if (!Libs.isEmpty(strOldPass) && !Libs.isEmpty(strPass) &&
                        !Libs.isEmpty(strConfirmPass)) {

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

                if (TextUtils.isEmpty(strName)) {
                    name.setError(getString(R.string.error_field_required));
                    focusView = name;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (libs.isConnectingToInternet()) {

                        progressDialog.setMessage(getActivity().getString(R.string.uploading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        StorageService storageService = new StorageService(getActivity());

                        JSONObject jsonToUpdate = new JSONObject();

                        try {
                            jsonToUpdate.put("customer_name", strName);
                            jsonToUpdate.put("customer_contact_no", strContactNo);
                            jsonToUpdate.put("customer_address", strAddress);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        storageService.updateDocs(jsonToUpdate, Config.jsonDocId, Config.collectionName, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                Config.customerModel.setStrAddress(strAddress);
                                Config.customerModel.setStrContacts(strContactNo);
                                Config.customerModel.setStrName(strName);

                                if (strPass != null && !strPass.equalsIgnoreCase("")) {

                                    if (libs.isConnectingToInternet()) {

                                        progressDialog.setMessage(getActivity().getString(R.string.verify_identity));

                                       /* try {
                                            strOldPass = AESCrypt.encrypt(Config.string, strOldPass);
                                            strPass = AESCrypt.encrypt(Config.string, strPass);
                                        } catch (GeneralSecurityException e) {
                                            e.printStackTrace();
                                        }*/

                                        UserService userService = new UserService(getActivity());

                                        userService.onChangePassword(Config.strUserName, strOldPass, strPass, new App42CallBack() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                progressDialog.dismiss();
                                                libs.toast(1, 1, getActivity().getString(R.string.account_updated));
                                                goToAccount();
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                                progressDialog.dismiss();
                                                libs.toast(2, 2, e.getMessage());
                                            }
                                        });

                                    } else libs.toast(2, 2, getString(R.string.warning_internet));

                                } else {
                                    progressDialog.dismiss();
                                    libs.toast(1, 1, getActivity().getString(R.string.account_updated));
                                    goToAccount();
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                progressDialog.dismiss();
                                libs.toast(2, 2, e.getMessage());
                            }
                        });

                    } else libs.toast(2, 2, getString(R.string.warning_internet));
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
                //Libs.toast(1, 1, "Getting Image...");
                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Libs.customerImageUri.getPath();
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

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if(!isImageChanged){
                if (bitmap != null)
                    roundedImageView.setImageBitmap(bitmap);
                else
                    libs.toast(2, 2, getString(R.string.error));
            }

            if(isImageChanged&&bitmap != null) {
                try {
                    checkImage();
                }catch (Exception e){
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

                File f = libs.getInternalFileImages(Config.strCustomerImageName);
                Libs.log(f.getAbsolutePath(), " FP ");
                bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

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
                    File galleryFile = libs.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
                    libs.copyInputStreamToFile(is, galleryFile);
                    bitmap = libs.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                    isImageChanged=true;
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
                    bitmap = libs.getBitmapFromFile(strCustomerImgName, Config.intWidth, Config.intHeight);
                    isImageChanged=true;
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkImage() {

        try {

            if (libs.isConnectingToInternet()) {

                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();

                UploadService uploadService = new UploadService(getActivity());

                if (progressDialog.isShowing())
                    progressDialog.setProgress(1);

                    uploadService.removeImage(Config.strCustomerImageName, Config.customerModel.getStrEmail(),
                            new App42CallBack() {
                        public void onSuccess(Object response) {

                            if(response!=null){
                                uploadImage();
                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, getString(R.string.warning_internet));
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
                                    libs.toast(2, 2, getString(R.string.error));
                                }

                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

            } else {
                libs.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error));
        }
    }

    public void uploadImage(){

        try {

            if (libs.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(getActivity());

                uploadService.uploadImageCommon(
                        strCustomerImgName,
                        Config.strCustomerImageName, "Profile Picture", Config.customerModel.getStrEmail(),
                    UploadFileType.IMAGE, new App42CallBack() {
                        public void onSuccess(Object response) {

                            if(response!=null) {
                               // Libs.log(response.toString(), "response");
                                Upload upload = (Upload) response;
                                ArrayList<Upload.File> fileList = upload.getFileList();

                                if (fileList.size() > 0) {

                                    /*Upload.File file = fileList.get(0);

                                    String url = file.getUrl();*/

                                    if (bitmap != null) {
                                        roundedImageView.setImageBitmap(bitmap);
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        libs.toast(2, 2, getString(R.string.update_profile_image));
                                        isImageChanged = false;

                                        try {
                                            File f = libs.getInternalFileImages(Config.strCustomerImageName);

                                            if (f.exists())
                                                f.delete();

                                            File newFile = new File(strCustomerImgName);
                                            File renameFile = new File(strCustomerImagePath);

                                            libs.moveFile(newFile, renameFile);
                                            //TODO Check Logic
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                    else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        libs.toast(2, 2, getString(R.string.error));
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    libs.toast(2, 2, ((Upload) response).getStrResponse());
                                }
                            }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if(e!=null) {
                                Libs.log(e.toString(), "response");
                                libs.toast(2, 2, e.getMessage());
                            }else{
                                libs.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                libs.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error));
        }
    }
}
