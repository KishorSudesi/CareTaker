package com.hdfc.caretaker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.caretaker.fragments.AddDependentFragment;
import com.hdfc.caretaker.fragments.ConfirmFragment;
import com.hdfc.libs.Utils;

import java.util.Date;

public class DependentDetailsMedicalActivity extends AppCompatActivity {

    public static Date date = null;
    private Utils utils;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;
    private Button buttonContinue;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependent_details_medical);

        utils = new Utils(DependentDetailsMedicalActivity.this);
        progressDialog = new ProgressDialog(DependentDetailsMedicalActivity.this);

        editAge = (EditText) findViewById(R.id.editAge);
        editDiseases = (EditText) findViewById(R.id.editDiseases);
        editNotes = (EditText) findViewById(R.id.editNotes);

        editDiseases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        //Button buttonSkip = (Button)findViewById(R.id.buttonSkip);

        if (buttonBack != null) {
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backToSelection();
                }
            });
        }

        if (buttonContinue != null) {
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (buttonContinue.getText().toString().trim().equalsIgnoreCase(getString(R.string.submit)))
                        validateDependantMedicalData();

                    if (buttonContinue.getText().toString().trim().equalsIgnoreCase(getString(R.string.skip)))
                        skip();
                }
            });
        }
        /*if (buttonSkip != null) {
            buttonSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dependentData();
                }
            });
        }*/

        utils.setStatusBarColor("#cccccc");

    }

    public void setButtonText() {

        if (editDiseases.getText().toString().trim().length() <= 0 && editNotes.getText().toString().trim().length() <= 0)
            buttonContinue.setText(getString(R.string.skip));
        else
            buttonContinue.setText(getString(R.string.submit));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
        backToSelection();
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

    public void skip() {

        if (DependentDetailPersonalActivity.dependentModel != null) {

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            strAge = editAge.getText().toString().trim();

            DependentDetailPersonalActivity.dependentModel.setIntAge(Integer.parseInt(strAge));
            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);
            DependentDetailPersonalActivity.dependentModel.setStrImageUrl("");

            gotoDependnetList();

        } else {
            progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.dependent_data_lost));
        }

    }

    public void gotoDependnetList() {
        Intent selection = new Intent(DependentDetailsMedicalActivity.this, SignupActivity.class);
        selection.putExtra("LIST_DEPENDANT", true);
        DependentDetailPersonalActivity.strDependantName = "";
        DependentDetailPersonalActivity.strImageName = "";
        DependentDetailPersonalActivity.dependentModel = null;

        //chk this
        utils.retrieveDependants();

        if (AddDependentFragment.adapter != null)
            AddDependentFragment.adapter.notifyDataSetChanged();

        utils.retrieveConfirmDependants();

        if (ConfirmFragment.adapter != null)
            ConfirmFragment.adapter.notifyDataSetChanged();
        //

        progressDialog.dismiss();
        utils.toast(1, 1, getString(R.string.dpndnt_details_saved));

        startActivity(selection);
        finish();
    }

    public void storeData() {

        if (DependentDetailPersonalActivity.dependentModel != null) {

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            DependentDetailPersonalActivity.dependentModel.setIntAge(Integer.parseInt(strAge));
            DependentDetailPersonalActivity.dependentModel.setStrIllness(strDiseases);
            DependentDetailPersonalActivity.dependentModel.setStrNotes(strNotes);
            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);

            DependentDetailPersonalActivity.dependentModel.setStrImageUrl("");

            gotoDependnetList();
        } else {
            progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.dependent_data_lost));
        }
    }

    public void backToSelection() {
        Intent selection = new Intent(DependentDetailsMedicalActivity.this,
                DependentDetailPersonalActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

       // strAge = utils.getAge(DependentDetailPersonalActivity.iDate, DependentDetailPersonalActivity.iMonth,DependentDetailPersonalActivity.iYear);


        strAge = utils.getAge(date);
        editAge.setText(strAge);

        DependentDetailPersonalActivity.dependentModel.setIntAge(Integer.parseInt(strAge));
    }
}
