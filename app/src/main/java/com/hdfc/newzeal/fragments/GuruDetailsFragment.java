package com.hdfc.newzeal.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.views.CustomViewPager;

import java.io.File;
import java.io.IOException;

public class GuruDetailsFragment extends Fragment {

    public static ImageButton imgButtonCamera;

    public static Uri fileUri;

    private EditText editName, editEmail, editPass, editConfirmPass, editContactNo;

    private Libs libs;

    private Button buttonContinue;

    public GuruDetailsFragment() {
        // Required empty public constructor
    }

    public static GuruDetailsFragment newInstance() {
        GuruDetailsFragment fragment = new GuruDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libs = new Libs(getActivity());

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_guru_details, container, false);
        imgButtonCamera = (ImageButton) rootView.findViewById(R.id.imageButtonCamera);

        editName = (EditText) rootView.findViewById(R.id.editName);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        editPass = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPass = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editContactNo = (EditText) rootView.findViewById(R.id.editContactNo);
        buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);

        CustomViewPager.setPagingEnabled(true);

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(Libs.sha512(SignupActivity.longUserId + ""));
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validateUser();
                SignupActivity._mViewPager.setCurrentItem(1);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SignupActivity.longUserId > 0)
            NewZeal.dbCon.retrieveUser(SignupActivity.longUserId, editName, editEmail, editContactNo);
    }

    private void validateUser() {

        editName.setError(null);
        editEmail.setError(null);
        editPass.setError(null);
        editConfirmPass.setError(null);
        editContactNo.setError(null);

        String strName = editName.getText().toString();
        String strEmail = editEmail.getText().toString();
        String strPass = editPass.getText().toString();
        String strConfirmPass = editConfirmPass.getText().toString();
        String strContactNo = editContactNo.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strContactNo)) {
            editContactNo.setError(getString(R.string.error_field_required));
            focusView = editContactNo;
            cancel = true;
        } else if (strContactNo.length() != 10) {
            editContactNo.setError(getString(R.string.error_invalid_contact_no));
            focusView = editContactNo;
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

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                long lngUserId = NewZeal.dbCon.insertUser(strName, strEmail, strConfirmPass, strContactNo, SignupActivity.longUserId);
                if (lngUserId > 0) {
                    SignupActivity.longUserId = lngUserId;
                    CustomViewPager.setPagingEnabled(true);
                    Libs.toast(1, 1, getString(R.string.your_details_saved));
                    SignupActivity._mViewPager.setCurrentItem(1);
                } else {
                    Libs.toast(1, 1, getString(R.string.email_exists) + String.valueOf(lngUserId));
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
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        bitmap = getBitmap(fileUri);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            postImagePick(bitmap);
        }
    }

    protected void postImagePick(Bitmap bitmap) {
        imgButtonCamera.setImageBitmap(bitmap);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap original = null;
        try {
            Log.d(" 2 ", "getBitmap" + selectedimg.getPath());
            original = BitmapFactory.decodeFile(selectedimg.getPath(), options);
        } catch (OutOfMemoryError oOm) {
        } catch (Exception e) {
        }
        return original;
    }
}
