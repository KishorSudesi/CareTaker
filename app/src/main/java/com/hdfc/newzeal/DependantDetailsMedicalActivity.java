package com.hdfc.newzeal;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.app42service.UploadService;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import java.util.ArrayList;

public class DependantDetailsMedicalActivity extends AppCompatActivity {

    private static long longUserId;
    private static String strDependantName;
    private Libs libs;
    private EditText editAge, editDiseases, editNotes;
    private Button buttonContinue, buttonBack;

    private String strAge, strDiseases, strNotes;
    private boolean isUpdated;

    private ProgressDialog progressDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_details_medical);

        libs = new Libs(DependantDetailsMedicalActivity.this);
        progressDialog = new ProgressDialog(DependantDetailsMedicalActivity.this);

        editAge = (EditText) findViewById(R.id.editAge);
        editDiseases = (EditText) findViewById(R.id.editDiseases);
        editNotes = (EditText) findViewById(R.id.editNotes);

        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSelection();
            }
        });

        longUserId = SignupActivity.longUserId;
        strDependantName = DependantDetailPersonalActivity.strDependantName;

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDependantMedicalData();
            }
        });

        libs.setStatusBarColor("#cccccc");

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
    }

    private void validateDependantMedicalData() {
        editAge.setError(null);
        editDiseases.setError(null);
        editNotes.setError(null);

        strAge = editAge.getText().toString();
        strDiseases = editDiseases.getText().toString();
        strNotes = editNotes.getText().toString();

        int tempIntAge = 0;

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strAge)) {
            editAge.setError(getString(R.string.error_field_required));
            focusView = editAge;
            cancel = true;
        } else {
            tempIntAge = Integer.parseInt(strAge);

            if (tempIntAge < 0 || tempIntAge > 150) {
                editAge.setError(getString(R.string.error_invalid_age));
                focusView = editAge;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(strDiseases)) {
            editDiseases.setError(getString(R.string.error_field_required));
            focusView = editDiseases;
            cancel = true;
        }

        if (TextUtils.isEmpty(strNotes)) {
            editNotes.setError(getString(R.string.error_field_required));
            focusView = editNotes;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            uploadDependantProfile();
        }
    }

    public void uploadDependantProfile() {

        if (libs.isConnectingToInternet()) {

            progressDialog.setMessage("Uplaoding Profile Picture...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            UploadService uploadService = new UploadService(DependantDetailsMedicalActivity.this);

            uploadService.uploadImageCommon(DependantDetailPersonalActivity.strImagePathToServer, libs.replaceSpace(DependantDetailPersonalActivity.strDependantName), "Profile Picture", SignupActivity.strCustomerEmail, UploadFileType.IMAGE, new App42CallBack() {
                public void onSuccess(Object response) {
                    Upload upload = (Upload) response;

                    Libs.log(response.toString(), "");
                    ArrayList<Upload.File> fileList = upload.getFileList();

                    if (fileList.size() > 0) {
                        String strDependantImageUrl = fileList.get(0).getUrl();

                        isUpdated = NewZeal.dbCon.updateDependantMedicalDetails(strDependantName, strAge, strDiseases, strNotes, longUserId, strDependantImageUrl);

                        if (isUpdated) {

                            Intent selection = new Intent(DependantDetailsMedicalActivity.this, SignupActivity.class);
                            selection.putExtra("LIST_DEPENDANT", true);
                            DependantDetailPersonalActivity.strDependantName = "";
                            DependantDetailPersonalActivity.strImageName = "";
                            DependantDetailPersonalActivity.longDependantId = 0;
                            DependantDetailPersonalActivity.strImagePathToServer = "";
                            progressDialog.dismiss();
                            libs.toast(1, 1, getString(R.string.dpndnt_medical_info_saved));

                            startActivity(selection);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.error));
                        }
                    } else libs.toast(2, 2, getString(R.string.error));

                }

                public void onException(Exception ex) {
                    progressDialog.dismiss();
                    libs.toast(2, 2, getString(R.string.error) + ex.getMessage());
                    //ex.printStackTrace();
                }
            });

        } else libs.toast(2, 2, getString(R.string.warning_internet));
    }

    public void backToSelection() {
        Intent selection = new Intent(DependantDetailsMedicalActivity.this, DependantDetailPersonalActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
