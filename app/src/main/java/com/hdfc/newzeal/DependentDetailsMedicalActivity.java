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

import com.hdfc.libs.Libs;
import com.hdfc.newzeal.fragments.AddDependentFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;

public class DependentDetailsMedicalActivity extends AppCompatActivity {

    private Libs libs;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependent_details_medical);

        libs = new Libs(DependentDetailsMedicalActivity.this);
        progressDialog = new ProgressDialog(DependentDetailsMedicalActivity.this);

        editAge = (EditText) findViewById(R.id.editAge);
        editDiseases = (EditText) findViewById(R.id.editDiseases);
        editNotes = (EditText) findViewById(R.id.editNotes);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);
        Button buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSelection();
            }
        });

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

        strAge = editAge.getText().toString().trim();
        strDiseases = editDiseases.getText().toString().trim();
        strNotes = editNotes.getText().toString().trim();

        int tempIntAge;

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
            storeData();
        }
    }

    public void storeData() {

        if (DependentDetailPersonalActivity.dependentModel != null) {

            DependentDetailPersonalActivity.dependentModel.setIntAge(Integer.parseInt(strAge));
            DependentDetailPersonalActivity.dependentModel.setStrIllness(strDiseases);
            DependentDetailPersonalActivity.dependentModel.setStrDesc(strNotes);
            DependentDetailPersonalActivity.dependentModel.setStrImgServer("");

            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);

            Intent selection = new Intent(DependentDetailsMedicalActivity.this, SignupActivity.class);
            selection.putExtra("LIST_DEPENDANT", true);
            DependentDetailPersonalActivity.strDependantName = "";
            DependentDetailPersonalActivity.strImageName = "";
            DependentDetailPersonalActivity.dependentModel = null;

            //chk this
            libs.retrieveDependants();
            AddDependentFragment.adapter.notifyDataSetChanged();

            libs.retrieveConfirmDependants();
            ConfirmFragment.adapter.notifyDataSetChanged();
            //

            progressDialog.dismiss();
            libs.toast(1, 1, getString(R.string.dpndnt_medical_info_saved));

            startActivity(selection);
            finish();

        } else {
            progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.dependent_data_lost));
        }
    }

    public void backToSelection() {
        Intent selection = new Intent(DependentDetailsMedicalActivity.this, DependentDetailPersonalActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
