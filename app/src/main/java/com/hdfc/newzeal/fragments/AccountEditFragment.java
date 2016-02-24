package com.hdfc.newzeal.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.R;
import com.hdfc.views.RoundedImageView;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.GeneralSecurityException;

public class AccountEditFragment extends Fragment {

    private static EditText name, number, city, editTextOldPassword, editTextPassword, editTextConfirmPassword;
    private static Libs libs;

    private static String strName;
    private static String strContactNo;
    private static String strPass;
    private static String strAddress;
    private static String strOldPass;

    private static Bitmap bitmap;
    private static RoundedImageView roundedImageView;
    private static Thread backgroundThread;
    private static Handler threadHandler;
    private RelativeLayout loadingPanel;
    private ProgressDialog progressDialog;


    public static AccountEditFragment newInstance() {
        AccountEditFragment fragment = new AccountEditFragment();
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

        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        progressDialog = new ProgressDialog(getActivity());

        roundedImageView = (RoundedImageView) view.findViewById(R.id.imageView5);

        name = (EditText) view.findViewById(R.id.editTextGuruName);

        editTextOldPassword = (EditText) view.findViewById(R.id.editOldPassword);
        editTextPassword = (EditText) view.findViewById(R.id.editPassword);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.editConfirmPassword);

        number = (EditText) view.findViewById(R.id.editTextNumber);
        city = (EditText) view.findViewById(R.id.editTextCity);

        name.setText(Config.customerModel.getStrName());
        number.setText(Config.customerModel.getStrContacts());

        if (Config.customerModel.getStrAddress() != null)
            city.setText(Config.customerModel.getStrAddress());

        txtViewHeader.setText(getActivity().getString(R.string.my_account_edit));
        Button goToDashBoard = (Button) view.findViewById(R.id.buttonSaveSetting);

        libs = new Libs(getActivity());

        //

        threadHandler = new ThreadHandler();
        backgroundThread = new BackgroundThread();
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

                        Config.customerModel.setStrAddress(strAddress);
                        Config.customerModel.setStrContacts(strContactNo);
                        Config.customerModel.setStrName(strName);

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
                        storageService.updateDocs(jsonToUpdate, Config.jsonDocId, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                if (strPass != null && !strPass.equalsIgnoreCase("")) {

                                    if (libs.isConnectingToInternet()) {

                                        progressDialog.setMessage(getActivity().getString(R.string.process_login));

                                        try {
                                            strOldPass = AESCrypt.encrypt(Config.string, strOldPass);
                                            strPass = AESCrypt.encrypt(Config.string, strPass);
                                        } catch (GeneralSecurityException e) {
                                            e.printStackTrace();
                                        }

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

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                File f = libs.getInternalFileImages(Config.customerModel.getStrName());

                bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);

            //loadingPanel.setVisibility(View.GONE);
        }
    }
}
