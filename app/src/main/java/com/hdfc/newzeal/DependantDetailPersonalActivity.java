package com.hdfc.newzeal;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.hdfc.views.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DependantDetailPersonalActivity extends AppCompatActivity {

    public static RoundedImageView imgButtonCamera;
    public static String dependantImgName = "";
    public static String strImageName = "", strImagePathToServer = "";
    public static String strDependantName = "";
    public static long longDependantId = 0;
    public static Bitmap bitmap = null;
    public static Uri uri;
    private static Thread backgroundThread, backgroundThreadCamera;
    private static Handler backgroundThreadHandler;
    private static boolean isCamera = false;
    private static SearchView searchView;
    private Libs libs;
    private EditText editName, editContactNo, editAddress, editRelation, editDependantEmail;
    private Button buttonContinue;
    private ProgressDialog mProgress = null;

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

        mProgress = new ProgressDialog(DependantDetailPersonalActivity.this);

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
            }
        });

        String tempDate = String.valueOf(new Date().getDate() + "" + new Date().getTime());

        dependantImgName = tempDate + ".jpeg";

        imgButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libs.selectImage(dependantImgName, null, DependantDetailPersonalActivity.this);
                isCamera = true;
            }
        });

        setupSearchView();

        libs.setStatusBarColor("#cccccc");
    }

    public void backToSelection() {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(DependantDetailPersonalActivity.this);
        alertbox.setTitle(getString(R.string.app_name));
        alertbox.setMessage(getString(R.string.delete_info));
        alertbox.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertbox.show();
    }

    private void validateDependant() {
        editName.setError(null);
        editContactNo.setError(null);
        editAddress.setError(null);
        editRelation.setError(null);
        //editDependantEmail.setError(null);

        strDependantName = editName.getText().toString();
        String strContactNo = editContactNo.getText().toString();
        String strAddress = editAddress.getText().toString();
        String strRelation = editRelation.getText().toString();
        String strEmail = editDependantEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(strImageName) && longDependantId <= 0) {
            libs.toast(2, 2, getString(R.string.warning_profile_pic));
            focusView = imgButtonCamera;
            cancel = true;
        } else {

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

        /*if (TextUtils.isEmpty(strEmail)) {
            editDependantEmail.setError(getString(R.string.error_field_required));
            focusView = editDependantEmail;
            cancel = true;
        } else if (!libs.isEmailValid(strEmail)) {
            editDependantEmail.setError(getString(R.string.error_invalid_email));
            focusView = editDependantEmail;
            cancel = true;
        }*/

            if (TextUtils.isEmpty(strDependantName)) {
                editName.setError(getString(R.string.error_field_required));
                focusView = editName;
                cancel = true;
            }
        }



        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                longDependantId = NewZeal.dbCon.insertDependant(strDependantName, strContactNo, strAddress, strRelation, SignupActivity.longUserId, strImageName, strEmail);
                if (longDependantId > 0) {
                    libs.toast(1, 1, getString(R.string.dpndnt_details_saved));
                    strImagePathToServer = strImageName;
                    strImageName = "";
                    Intent selection = new Intent(DependantDetailPersonalActivity.this, DependantDetailsMedicalActivity.class);
                    startActivity(selection);
                    finish();
                } else {
                    libs.toast(1, 1, getString(R.string.dpndnt_details_not_saved));
                }

            } catch (Exception e) {

            }
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView);

        ComponentName cn = new ComponentName(this, DependantDetailPersonalActivity.class);

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(cn);
        searchView.setSearchableInfo(searchableInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        handleIntent(intent);
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    protected void handleIntent(Intent intent) {

        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            //handles suggestion clicked query
            readContacts(intent);

        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            editName.setText("should search for query: '" + query + "'...");
        }
    }

    @OnShowRationale({Manifest.permission.READ_CONTACTS})
    void showRationaleForContact(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_contact_rationale, request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        DependantDetailPersonalActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    public void readContacts(Intent intent) {

        try {

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(intent.getData(), new String[]{ContactsContract.Contacts._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI, ContactsContract.Contacts.HAS_PHONE_NUMBER}, null, null, null);

            String phone = "";
            String emailContact = null;
            String image_uri = "";
            String id = "";
            String name = "";

            editName.setText("");
            editContactNo.setText("");
            editDependantEmail.setText("");

            imgButtonCamera.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.camera_icon));

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        pCur.close();
                    }
                }

                Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);

                if (emailCur.getCount() > 0) {
                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emailCur.close();
                }

                if (image_uri != null) {
                    try {
                        mProgress.setMessage(getString(R.string.loading));
                        mProgress.show();
                        uri = Uri.parse(image_uri);
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        backgroundThread = new BackgroundThread();
                        backgroundThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                editName.setText(name);
                editContactNo.setText(phone);
                editDependantEmail.setText(emailContact);

                searchView.setFocusable(false);
                searchView.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SignupActivity.longUserId > 0 && !strDependantName.equalsIgnoreCase("")) {
            String strImgPath = NewZeal.dbCon.retrieveDependantPersonal(SignupActivity.longUserId, editName, editContactNo, editAddress, editRelation, strDependantName, editDependantEmail);

            if (!strDependantName.equalsIgnoreCase("") && isCamera == false) {
                strImageName = strImgPath;
                backgroundThreadHandler = new BackgroundThreadHandler();
                backgroundThreadCamera = new BackgroundThreadCamera();
                backgroundThreadCamera.start();
            } else isCamera = false;
        } else isCamera = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strImageName = Libs.customerImageUri.getPath().toString();
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            backgroundThreadHandler = new BackgroundThreadHandler();
                            backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //
    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = libs.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    InputStream is = getContentResolver().openInputStream(uri);
                    libs.copyInputStreamToFile(is, galleryFile);
                    bitmap = libs.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {

            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                    bitmap = libs.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            if (imgButtonCamera != null && bitmap != null)
                imgButtonCamera.setImageBitmap(bitmap);
        }
    }
}
