package com.hdfc.newzeal;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;

public class DependantDetailsMedicalActivity extends AppCompatActivity {

    private static long longUserId;
    private static String strDependantName;
    private Libs libs;
    private EditText editAge, editDiseases, editNotes;
    private Button buttonContinue, buttonBack;

    private String strAge, strDiseases, strNotes;
    //private static Thread backgroundThread;
    //private static Handler myHandler;

    //private ProgressDialog mProgress=null;

    private boolean isUpdated;

    private int intDpndtCount = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_details_medical);

        libs = new Libs(DependantDetailsMedicalActivity.this);

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

       /* mProgress = new ProgressDialog(this);
        myHandler = new MyHandler();*/

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDependantMedicalData();

               /* Intent selection = new Intent(DependantDetailsMedicalActivity.this, SignupActivity.class);
                selection.putExtra("LIST_DEPENDANT", true);
                startActivity(selection);
                finish();*/
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#cccccc"));
        }

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
            try {

                /*backgroundThread = new BackgroundThread();
                backgroundThread.start();
                mProgress.setMessage("Creating...");
                mProgress.show();*/

                isUpdated = NewZeal.dbCon.updateDependantMedicalDetails(strDependantName, strAge, strDiseases, strNotes, longUserId);

                //Log.e("ERROR", String.valueOf(isUpdated));
                if (isUpdated) {

                    // intDpndtCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

                    Intent selection = new Intent(DependantDetailsMedicalActivity.this, SignupActivity.class);
                    selection.putExtra("LIST_DEPENDANT", true);
                    DependantDetailPersonalActivity.strDependantName = "";
                    DependantDetailPersonalActivity.longDependantId = 0;

                    startActivity(selection);
                    finish();

                   /* AddDependantFragment.adapter.notifyDataSetChanged();
                    if (intDpndtCount > 1)
                        AddDependantFragment.buttonContinue.setVisibility(View.VISIBLE);*/

                    /*int intCountConfirm = NewZeal.dbCon.retrieveConfirmDependants(SignupActivity.longUserId);

                    if (intCountConfirm > 1)
                        ConfirmFragment.buttonContinue.setVisibility(View.VISIBLE);

                    //Resources res = getResources();
                    ConfirmFragment.adapter.notifyDataSetChanged();*/
                } else Libs.toast(1, 1, getString(R.string.error));

            } catch (Exception e) {

            }
        }
    }

    public void backToSelection() {
        Intent selection = new Intent(DependantDetailsMedicalActivity.this, DependantDetailPersonalActivity.class);
        //selection.putExtra("LIST_DEPENDANT", true);
        startActivity(selection);
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (!strDependantName.equalsIgnoreCase(""))
            NewZeal.dbCon.retrieveDependantMedical(SignupActivity.longUserId, editAge, editDiseases, editNotes, strDependantName);*/

    }

    /*public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                myHandler.sendEmptyMessage(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/
    //

   /* public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            sendResult();
        }
    }

    public void sendResult() {
        mProgress.dismiss();
    }*/

}
