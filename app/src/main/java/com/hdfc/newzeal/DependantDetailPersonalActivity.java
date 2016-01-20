package com.hdfc.newzeal;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class DependantDetailPersonalActivity extends AppCompatActivity {

    public static Uri fileUri;
    public static ImageButton imgButtonCamera;
    public static String dependantImgName = "";
    public static String strDependantName = "";
    private static long longUserId;
    private Libs libs;
    private EditText editName, editContactNo, editAddress, editRelation;
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

        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        imgButtonCamera = (ImageButton) findViewById(R.id.imageButtonCamera);

        longUserId = SignupActivity.longUserId;

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

        Log.e(" 2 ", tempDate);

        dependantImgName = Libs.sha512(tempDate);

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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //libs.backToDependantList();
    }

    public void openCamera(String strFileName) {
        strFileName = "image.jpeg";
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
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        // if (resultCode == RESULT_OK ) { //&& data != null
        try {
            switch (requestCode) {
                case Config.START_CAMERA_REQUEST_CODE:
                    bitmap = getBitmap(fileUri);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
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

    private void validateDependant() {
        editName.setError(null);
        editContactNo.setError(null);
        editAddress.setError(null);
        editRelation.setError(null);

        strDependantName = editName.getText().toString();
        String strContactNo = editContactNo.getText().toString();
        String strAddress = editAddress.getText().toString();
        String strRelation = editRelation.getText().toString();

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
        } else if (strContactNo.length() != 10) {
            editContactNo.setError(getString(R.string.error_invalid_contact_no));
            focusView = editContactNo;
            cancel = true;
        }

        if (TextUtils.isEmpty(strDependantName)) {
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                long lngDependantId = NewZeal.dbCon.insertDependant(strDependantName, strContactNo, strAddress, strRelation, longUserId);
                if (lngDependantId > 0) {
                    Libs.toast(1, 1, getString(R.string.dpndnt_details_saved));

                    Intent selection = new Intent(DependantDetailPersonalActivity.this, DependantDetailsMedicalActivity.class);
                    startActivity(selection);
                    finish();
                } else {
                    Libs.toast(1, 1, getString(R.string.dpndnt_details_not_saved) + String.valueOf(lngDependantId));
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
        Log.e("wer", intent.getAction().toString());
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
        Log.e(" wer ", intent.getData().toString());

        Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();
        int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String name = phoneCursor.getString(idDisplayName);
        phoneCursor.close();
        return name;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!strDependantName.equalsIgnoreCase(""))
            NewZeal.dbCon.retrieveDependantPersonal(SignupActivity.longUserId, editName, editContactNo, editAddress, editRelation, strDependantName);

    }
}
