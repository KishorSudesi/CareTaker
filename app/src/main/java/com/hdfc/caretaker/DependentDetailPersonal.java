package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.RoundedImageView;

import java.util.Date;

/**
 * Created by Admin on 24-06-2016.
 */

public class DependentDetailPersonal extends AppCompatActivity{
    private static SearchView searchView;
    private static EditText editName, editContactNo, editAddress, editDependantEmail, editTextDate;
    private Spinner spinnerRelation;
    public static RoundedImageView imgButtonCamera;
    Button buttonContinue;
    private Utils utils;
    private String strRelation;
    public static String relation;
    private static ProgressDialog mProgress = null;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            String strDate = Utils.writeFormatActivityYear.format(date);
            //String _strDate = Utils.readFormat.format(date);
            DependentDetailsMedicalActivity.date = date;
            editTextDate.setText(strDate);

            /*iDate = date.getDate();
            iMonth = date.getMonth();
            iYear = date.getYear();*/
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependent_detail_personal);

        utils = new Utils(DependentDetailPersonal.this);
        utils.setStatusBarColor("#2196f3");

        editName = (EditText) findViewById(R.id.editDependName);
        editContactNo = (EditText) findViewById(R.id.editContNo);
        editAddress = (EditText) findViewById(R.id.editAddr);
        editTextDate = (EditText)findViewById(R.id.editDateofbirth);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        spinnerRelation = (Spinner) findViewById(R.id.editRelations);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Config.strRelations);

        if (spinnerRelation != null) {
            spinnerRelation.setAdapter(adapter);

            spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    strRelation = (String) parent.getItemAtPosition(position);
                    relation = spinnerRelation.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        editDependantEmail = (EditText) findViewById(R.id.editDependEmail);
        mProgress = new ProgressDialog(DependentDetailPersonal.this);

        buttonContinue = (Button) findViewById(R.id.btn1continue);

        imgButtonCamera = (RoundedImageView) findViewById(R.id.img1ButtonCamera);

     //   Button buttonBack = (Button) findViewById(R.id.buttonBack);

        if (buttonContinue != null) {
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   validateDependant();
                    Intent selection = new Intent(DependentDetailPersonal.this,
                            DependentDetailsMedical.class);
                    startActivity(selection);
                }
            });
        }


    }
}
