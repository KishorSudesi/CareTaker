package com.hdfc.newzeal.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.views.CustomViewPager;
import com.hdfc.views.RoundedImageView;

import java.io.File;
import java.util.Date;

public class GuruDetailsFragment extends Fragment {

    public static RoundedImageView imgButtonCamera;

    public static Uri fileUri;
    public static String strCustomerImgName;
    public static String strCustomerImgNameCamera;
    private EditText editName, editEmail, editPass, editConfirmPass, editContactNo, editAddress;
    private Libs libs;
    private Button buttonContinue;

    //public static String strDataName, strDataEmail, strDataContactNo, strDataImgName;

    public GuruDetailsFragment() {
        // Required empty public constructor
        Log.e("GuruDetailsFragment", "GuruDetailsFragment");

    }

    public static GuruDetailsFragment newInstance() {
        GuruDetailsFragment fragment = new GuruDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.e("GuruDetailsFragment", "newInstance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libs = new Libs(getActivity());
        Log.e("GuruDetailsFragment", "onCreate");
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
        buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);
        editAddress = (EditText) rootView.findViewById(R.id.editAddress);

        String tempDate = String.valueOf(new Date().getDate() + "" + new Date().getTime()) + ".jpeg";

        strCustomerImgNameCamera = tempDate;

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(strCustomerImgNameCamera);
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
                //SignupActivity._mViewPager.setCurrentItem(1);
            }
        });

        Log.e("GuruDetailsFragment", "onCreateView");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //strCustomerImgName="";
        Log.e("GuruDetailsFragment", "onResume");

        //Log.e("GuruDetailsFragment", String.valueOf(SignupActivity.longUserId));

        if (SignupActivity.longUserId > 0)
            NewZeal.dbCon.retrieveUser(SignupActivity.longUserId, editName, editEmail, editContactNo, imgButtonCamera, editAddress);
    }

    private void validateUser() {

        editName.setError(null);
        editEmail.setError(null);
        editPass.setError(null);
        editConfirmPass.setError(null);
        editContactNo.setError(null);
        editAddress.setError(null);

        String strName = editName.getText().toString();
        String strEmail = editEmail.getText().toString();
        String strPass = editPass.getText().toString();
        String strConfirmPass = editConfirmPass.getText().toString();
        String strContactNo = editContactNo.getText().toString();
        String strAddress = editAddress.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strContactNo)) {
            editContactNo.setError(getString(R.string.error_field_required));
            focusView = editContactNo;
            cancel = true;
        } else if (!libs.validCellPhone(strContactNo)) {
            editContactNo.setError(getString(R.string.error_invalid_contact_no));
            focusView = editContactNo;
            cancel = true;
        }

        if (TextUtils.isEmpty(strAddress)) {
            editAddress.setError(getString(R.string.error_field_required));
            focusView = editAddress;
            cancel = true;
        }

        if (!TextUtils.isEmpty(strPass) && libs.isPasswordValid(strPass) && !TextUtils.isEmpty(strConfirmPass) && libs.isPasswordValid(strConfirmPass)) {

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

        if (TextUtils.isEmpty(strEmail)) {
            editEmail.setError(getString(R.string.error_field_required));
            focusView = editEmail;
            cancel = true;
        } else if (!libs.isEmailValid(strEmail)) {
            editEmail.setError(getString(R.string.error_invalid_email));
            focusView = editEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(strName)) {
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }

        if (TextUtils.isEmpty(strCustomerImgName) & SignupActivity.longUserId <= 0) {
            Libs.toast(1, 1, "Profile picture needed");
            focusView = imgButtonCamera;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                long lngUserId = NewZeal.dbCon.insertUser(strName, strEmail, strConfirmPass, strContactNo, SignupActivity.longUserId, strCustomerImgName, strAddress);
                if (lngUserId > 0) {

                    SignupActivity.longUserId = lngUserId;
                    CustomViewPager.setPagingEnabled(true);
                    Libs.toast(1, 1, getString(R.string.your_details_saved));

                    //

                   /* int intCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

                    if (intCount > 1)
                        AddDependantFragment.buttonContinue.setVisibility(View.VISIBLE);

                    //Resources res = getResources();
                    AddDependantFragment.adapter.notifyDataSetChanged();*/

                    strCustomerImgName = "";
                    //

                    SignupActivity._mViewPager.setCurrentItem(1);
                } else {
                    Libs.toast(1, 1, getString(R.string.email_exists));
                }

            } catch (Exception e) {

            }
        }
    }

    public void openCamera(String strFileName) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = libs.createFileInternal(strFileName);
        fileUri = Uri.fromFile(file);
        if (file != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            GuruDetailsFragment.this.startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        bitmap = libs.getBitmap(fileUri, 300, 300);
                        strCustomerImgName = fileUri.getPath().toString();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            libs.postImagePick(bitmap, imgButtonCamera);
        }
    }

}
