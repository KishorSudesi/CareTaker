package com.hdfc.newzeal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;

public class DependantDetailsMedicalActivity extends AppCompatActivity {

    private static SharedPreferences sPre;
    private static long longUserId;
    private static String strDependantName;
    private Libs libs;
    private EditText editAge, editDiseases, editNotes;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_details_medical);

        libs = new Libs(DependantDetailsMedicalActivity.this);

        editAge = (EditText) findViewById(R.id.editAge);
        editDiseases = (EditText) findViewById(R.id.editDiseases);
        editNotes = (EditText) findViewById(R.id.editNotes);

        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        sPre = getSharedPreferences("NEWZEAL", MODE_PRIVATE);

        longUserId = SignupActivity.longUserId;
        strDependantName = DependantDetailPersonalActivity.strDependantName;

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDependantMedicalData();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //do nothing
    }

    public void gotoSignupListDependants(){

    }

    private void validateDependantMedicalData() {
        editAge.setError(null);
        editDiseases.setError(null);
        editNotes.setError(null);

        String strAge = editAge.getText().toString();
        String strDiseases = editDiseases.getText().toString();
        String strNotes = editNotes.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strAge)) {
            editAge.setError(getString(R.string.error_field_required));
            focusView = editAge;
            cancel = true;
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
            try {
                boolean  isUpdated = NewZeal.dbCon.updateDependantMedicalDetails(strDependantName, strAge, strDiseases, strNotes, longUserId);
                if(isUpdated) {

                    Libs.toast(1, 1, "Dependant Medical Details Saved");
                    Intent selection = new Intent(DependantDetailsMedicalActivity.this, SignupActivity.class);
                    selection.putExtra("LIST_DEPENDANT", true);
                    startActivity(selection);
                } else Libs.toast(1, 1, "Error. Try Again!!!");

            }catch (Exception e){

            }
        }
    }

}
