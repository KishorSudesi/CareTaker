package com.hdfc.newzeal;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.hdfc.views.RoundedImageView;

import java.io.File;
import java.util.Date;

public class DependantDetailPersonalActivity extends AppCompatActivity {

    public static Uri fileUri;
    public static RoundedImageView imgButtonCamera;
    public static String dependantImgName = "";
    public static String strImageName = "";
    public static String strDependantName = "";
    public static long longDependantId = 0;
    private Libs libs;
    private EditText editName, editContactNo, editAddress, editRelation, editDependantEmail;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependant_detail_personal);

        libs = new Libs(DependantDetailPersonalActivity.this);

        editName = (EditText) findViewById(R.id.editDependantName);
        editContactNo = (EditText) findViewById(R.id.editContactNo);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editRelation = (EditText) findViewById(R.id.editRelation);
        editDependantEmail = (EditText) findViewById(R.id.editDependantEmail);

        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        imgButtonCamera = (RoundedImageView) findViewById(R.id.imageButtonCamera);

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
                validateDependant();

                /*Intent selection = new Intent(DependantDetailPersonalActivity.this, DependantDetailsMedicalActivity.class);
                startActivity(selection);
                finish();*/
            }
        });

        String tempDate = String.valueOf(new Date().getDate() + "" + new Date().getTime());

        dependantImgName = tempDate + ".jpeg";

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(dependantImgName);
            }
        });

        setupSearchView();

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#cccccc"));
        }
    }

    public void backToSelection() {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(DependantDetailPersonalActivity.this);
        alertbox.setTitle("NewZeal");
        alertbox.setMessage("All your Information will not be saved, Ok to Proceed?");
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //delete temp dependants
                try {
                    NewZeal.dbCon.deleteTempDependants();
                    Intent selection = new Intent(DependantDetailPersonalActivity.this, SignupActivity.class);
                    selection.putExtra("LIST_DEPENDANT", true);

                    //
                    //
                    /*
                    int intCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

                    if (intCount > 1)
                        AddDependantFragment.buttonContinue.setVisibility(View.VISIBLE);

                    //Resources res = getResources();
                    AddDependantFragment.adapter.notifyDataSetChanged();

                    ///

                    int intCountConfirm = NewZeal.dbCon.retrieveConfirmDependants(SignupActivity.longUserId);

                    if (intCountConfirm > 1)
                        ConfirmFragment.buttonContinue.setVisibility(View.VISIBLE);

                    //Resources res = getResources();
                    ConfirmFragment.adapter.notifyDataSetChanged();*/

                    //
                    //

                    arg0.dismiss();
                    startActivity(selection);
                    finish();
                } catch (Exception e) {
                }
            }
        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertbox.show();
    }

    public void openCamera(String strFileName) {
        //strFileName = strFileName+".jpeg";
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = libs.createFileInternal(strFileName);
        fileUri = Uri.fromFile(file);
        if (file != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) { //&& data != null
            try {
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        bitmap = libs.getBitmap(fileUri, 300, 300);
                        strImageName = fileUri.getPath();
                        Log.e("PATH 1", fileUri.getPath().toString());
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

    private void validateDependant() {
        editName.setError(null);
        editContactNo.setError(null);
        editAddress.setError(null);
        editRelation.setError(null);
        editDependantEmail.setError(null);

        strDependantName = editName.getText().toString();
        String strContactNo = editContactNo.getText().toString();
        String strAddress = editAddress.getText().toString();
        String strRelation = editRelation.getText().toString();
        String strEmail = editDependantEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strRelation)) {
            editRelation.setError(getString(R.string.error_field_required));
            focusView = editRelation;
            cancel = true;
        }

        if (TextUtils.isEmpty(strAddress)) {
            editAddress.setError(getString(R.string.error_field_required));
            focusView = editAddress;
            cancel = true;
        }

        if (TextUtils.isEmpty(strContactNo)) {
            editContactNo.setError(getString(R.string.error_field_required));
            focusView = editContactNo;
            cancel = true;
        } else if (!libs.validCellPhone(strContactNo)) {
            editContactNo.setError(getString(R.string.error_invalid_contact_no));
            focusView = editContactNo;
            cancel = true;
        }

        if (TextUtils.isEmpty(strEmail)) {
            editDependantEmail.setError(getString(R.string.error_field_required));
            focusView = editDependantEmail;
            cancel = true;
        } else if (!libs.isEmailValid(strEmail)) {
            editDependantEmail.setError(getString(R.string.error_invalid_email));
            focusView = editDependantEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(strDependantName)) {
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }

        if (TextUtils.isEmpty(strImageName) && longDependantId <= 0) {
            Libs.toast(2, 2, "Profile picture needed");
            focusView = imgButtonCamera;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                longDependantId = NewZeal.dbCon.insertDependant(strDependantName, strContactNo, strAddress, strRelation, SignupActivity.longUserId, strImageName, strEmail);
                if (longDependantId > 0) {
                    Libs.toast(1, 1, getString(R.string.dpndnt_details_saved));
                    strImageName = "";
                    Intent selection = new Intent(DependantDetailPersonalActivity.this, DependantDetailsMedicalActivity.class);
                    startActivity(selection);
                    finish();
                } else {
                    Libs.toast(1, 1, getString(R.string.dpndnt_details_not_saved));
                }

            } catch (Exception e) {

            }
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            //handles suggestion clicked query
            String displayName = getDisplayNameForContact(intent);
            editName.setText(displayName);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            editName.setText("should search for query: '" + query + "'...");
        }
    }

    private String getDisplayNameForContact(Intent intent) {
        Cursor phoneCursor = getContentResolver().query(intent.getData(), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        phoneCursor.moveToFirst();
        int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String name = phoneCursor.getString(idDisplayName);
        phoneCursor.close();
        return name;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("DependantDetail", String.valueOf(strDependantName));

        if (!strDependantName.equalsIgnoreCase(""))
            NewZeal.dbCon.retrieveDependantPersonal(SignupActivity.longUserId, editName, editContactNo, editAddress, editRelation, strDependantName, imgButtonCamera, editDependantEmail);

    }
}
