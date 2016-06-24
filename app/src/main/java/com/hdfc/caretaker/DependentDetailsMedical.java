package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.libs.Utils;

/**
 * Created by Admin on 24-06-2016.
 */
public class DependentDetailsMedical extends AppCompatActivity {

    private Utils utils;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependent_details_medical);

        utils = new Utils(DependentDetailsMedical.this);
        utils.setStatusBarColor("#2196f3");

        progressDialog = new ProgressDialog(DependentDetailsMedical.this);

        editAge = (EditText) findViewById(R.id.editAgedepend);
        editDiseases = (EditText) findViewById(R.id.editDiseasesdepend);
        editNotes = (EditText) findViewById(R.id.editNotesdepend);

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

    }
    public void setButtonText() {

        if (editDiseases.getText().toString().trim().length() <= 0 && editNotes.getText().toString().trim().length() <= 0)
            buttonContinue.setText(getString(R.string.skip));
        else
            buttonContinue.setText(getString(R.string.submit));
    }

}
