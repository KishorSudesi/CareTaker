package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 24-06-2016.
 */
public class DependentDetailsMedical extends AppCompatActivity {

    private Utils utils;
    public static Date date;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;
    private Button buttonContinue;
    public static JSONObject jsonDependant;
    private int iDependentCount = 0;
    private static int idregisterflag = 0;
    private static int editregisterflag = 0;
    public static int uploadSize, uploadingCount = 0;
    private SessionManager sessionManager;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependent_details_medical);
        jsonDependant=new JSONObject();
        utils = new Utils(DependentDetailsMedical.this);
        utils.setStatusBarColor("#2196f3");
        editregisterflag = 0;
        sessionManager = new SessionManager(DependentDetailsMedical.this);

        progressDialog = new ProgressDialog(DependentDetailsMedical.this);

        editAge = (EditText) findViewById(R.id.editAgedepend);
        editDiseases = (EditText) findViewById(R.id.editDiseasesdepend);
        editNotes = (EditText) findViewById(R.id.editNotesdepend);
        buttonContinue = (Button) findViewById(R.id.btnContinuedepend);
        Bundle getBundle = getIntent().getExtras();
        if (getBundle != null) {
            mPosition = getBundle.getInt("childposition");
        } else {
        }

        editDiseases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setButtonText();
            }
        });

        editNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setButtonText();
            }
        });

        buttonContinue = (Button) findViewById(R.id.btnContinuedepend);
        Button buttonBack = (Button) findViewById(R.id.buttonBack);

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


                    String buttonText = buttonContinue.getText().toString().trim();

                    if (buttonText.equalsIgnoreCase(getString(R.string.submit))) {
                        validateDependantMedicalData();
                    } else {
                        skip();
                    }

//                    if (buttonContinue.getText().toString().trim().equalsIgnoreCase(getString(R.string.submit)))
//                        validateDependantMedicalData();
//
//                    if (buttonContinue.getText().toString().trim().equalsIgnoreCase(getString(R.string.skip)))
//                        skip();
                }
            });
        }

        utils.setStatusBarColor("#cccccc");


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
        backToSelection();
    }

    public void backToSelection() {
        Intent selection = new Intent(DependentDetailsMedical.this,
                DependentDetailPersonal.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("editflag",true);
        bundle.putInt("childposition",mPosition);
        selection.putExtras(bundle);
        startActivity(selection);
        finish();
    }

    public void setButtonText() {
        String dieasesText = editDiseases.getText().toString().trim();
        String notestText = editNotes.getText().toString().trim();


        if (dieasesText != null && dieasesText.length() > 0 && notestText != null && notestText.length() > 0)
            buttonContinue.setText(getString(R.string.submit));
        else
            buttonContinue.setText(getString(R.string.skip));

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

            //


            storeData();


        }
    }

    private JSONObject createJson(JSONObject jsonDep) {
        DependentModel dependentModel = DependentDetailPersonal.dependentModel;
        //
        JSONObject jsonDependant = jsonDep;
        try {

            jsonDependant.put("dependent_name", dependentModel.getStrName());

            if (dependentModel.getStrIllness() == null || dependentModel.getStrIllness().equalsIgnoreCase(""))
                dependentModel.setStrIllness("NA");

            if (dependentModel.getStrNotes() == null || dependentModel.getStrNotes().equalsIgnoreCase(""))
                dependentModel.setStrNotes("NA");

            jsonDependant.put("dependent_illness", dependentModel.getStrIllness());

            jsonDependant.put("dependent_address", dependentModel.getStrAddress());
            jsonDependant.put("dependent_email", dependentModel.getStrEmail());

            jsonDependant.put("dependent_notes", dependentModel.getStrNotes());
            jsonDependant.put("dependent_age", dependentModel.getStrAge());
            jsonDependant.put("dependent_dob", dependentModel.getStrDob());
            jsonDependant.put("dependent_contact_no", dependentModel.getStrContacts());

            jsonDependant.put("dependent_profile_url", dependentModel.getStrImageUrl());
            jsonDependant.put("dependent_relation", dependentModel.getStrRelation());
            jsonDependant.put("customer_id", Config.customerModel.getStrCustomerID());

            jsonDependant.put("health_bp", 70 + iDependentCount);

            Config.dependentNames.add(dependentModel.getStrName());

            jsonDependant.put("health_heart_rate", 80 + iDependentCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDependant;
    }

    public void updateDependentDataInDb()

    {
        try {
            String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ? AND " + DbHelper.COLUMN_OBJECT_ID + " = ?";

            // WHERE clause arguments
            String[] selectionArgs = {Config.collectionDependent, DependentDetailPersonal.dependentModel.getStrDependentID()};
            Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {

                    JSONObject jsonReceived = new JSONObject(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                    jsonReceived = createJson(jsonReceived);

                    String strDocument = jsonReceived.toString();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());


                    String updatedAt = Utils.readFormat.format(c.getTime());

                    String values[] = {cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), updatedAt, strDocument, Config.collectionDependent, "", "1", "",""};
                    try {
                        //Config.jsonCustomer = new JSONObject(strDocument);

                        selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                        // WHERE clause arguments
                        String[] selectionArgsUpdate = {cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID))};
                        boolean isUpdatedDB = CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgsUpdate);
                        if (isUpdatedDB) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // createCustomerModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                } while (cursor.moveToNext());
                cursor.close();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                backToDashBoard();

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                backToDashBoard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void skip() {


        if (DependentDetailPersonal.dependentModel != null) {

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            strAge = editAge.getText().toString().trim();

            DependentDetailPersonal.dependentModel.setStrAge(strAge);

            if (DependentDetailPersonal.editflag) {

                if (DependentDetailPersonal.dependentModel.getStrImagePath() != null &&
                        !DependentDetailPersonal.dependentModel.getStrImagePath().equalsIgnoreCase("")
                        && !SignupActivity.dependentModels.get(DependentDetailPersonal.mPosition + 1).getStrImagePath().equalsIgnoreCase(DependentDetailPersonalActivity.dependentModel.getStrImagePath())) {

                    if (editregisterflag == 0)
                        deleteImage();

                    if (editregisterflag == 1)
                        edituploadDependentImages();

                    if (editregisterflag == 2)
                        updateDependentData();

                    Utils.log("w ", " 1 ");
                } else {
                    if (editregisterflag == 0)
                        updateDependentData();
                }


                //


                //}

            } else {
                if (idregisterflag == 0)
                    createDependentUser();

                //uploadDependentImages();

                if (idregisterflag == 1)
                    uploadDependentImages();


                if (idregisterflag == 2)
                    insertDependent();
            }

            // DependentDetailPersonalActivity.dependentModel.setStrImageUrl("");
           /* */


            //gotoDependnetList();

        } else {
            progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.dependent_data_lost));
        }

    }

    public void storeData() {

        if (DependentDetailPersonal.dependentModel != null) {

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            DependentDetailPersonal.dependentModel.setStrAge(strAge);
            DependentDetailPersonal.dependentModel.setStrIllness(strDiseases);
            DependentDetailPersonal.dependentModel.setStrNotes(strNotes);

            if (DependentDetailPersonal.editflag) {

                if (DependentDetailPersonal.dependentModel.getStrImagePath() != null &&
                        !DependentDetailPersonal.dependentModel.getStrImagePath().equalsIgnoreCase("")
                        && !SignupActivity.dependentModels.get(0).getStrImagePath().equalsIgnoreCase(DependentDetailPersonal.dependentModel.getStrImagePath())) {

                    if (editregisterflag == 0)
                        deleteImage();

                    if (editregisterflag == 1)
                        edituploadDependentImages();

                    if (editregisterflag == 2)
                        updateDependentData();

                } else {
                    if (editregisterflag == 0)
                        updateDependentData();
                }


            } else {


                if (idregisterflag == 0)
                    createDependentUser();

                //uploadDependentImages();

                if (idregisterflag == 1)
                    uploadDependentImages();


                if (idregisterflag == 2)
                    insertDependent();
            }

        } else {
            progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.dependent_data_lost));
        }
    }

    public void createDependentUser() {

        try {

            if (utils.isConnectingToInternet()) {

                UserService userService = new UserService(DependentDetailsMedical.this);

                ArrayList<String> roleList = new ArrayList<>();
                roleList.add("dependent");

                //Utils.log(" 2 ", " IN 0");

                userService.onCreateUser(DependentDetailPersonal.dependentModel.getStrContacts(),
                        "123", DependentDetailPersonal.dependentModel.getStrEmail(),
                        new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {


                                    idregisterflag = 1;

                                    uploadDependentImages();


                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (e != null) {
                                    Utils.log(e.getMessage(), "");

                                    int appErrorCode = ((App42Exception) e).getAppErrorCode();

                                    if (appErrorCode == 2005 || appErrorCode == 2001) {

                                        utils.toast(2, 2, getString(R.string.mobile_exists));

                                        Intent previos = new Intent(DependentDetailsMedical.this, DependentDetailPersonal.class);
                                        startActivity(previos);

                                       /* iDependentCount++;

                                        if (SignupActivity.dependentModels.size() == iDependentCount)
                                            callSuccess();
                                        else
                                            createDependentUser(strDependentEmail);*/
                                    } else {

                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {

                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        }, roleList);

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
            e.printStackTrace();
        }
    }

    public void uploadDependentImages() {

        try {


            if (utils.isConnectingToInternet()) {

                final DependentModel dependentModel = DependentDetailPersonal.dependentModel;

                //final int progress = uploadingCount;

                   /* if (progressDialog.isShowing())
                        progressDialog.setProgress(uploadingCount);*/

                UploadService uploadService = new UploadService(DependentDetailsMedical.this);

                   /* if (!dependentModel.getStrName().equalsIgnoreCase(
                            DependentDetailsMedicalActivity.this.getResources().getString(R.string.add_dependent))) {
*/
                if (dependentModel.getStrImagePath() != null &&
                        !dependentModel.getStrImagePath().equalsIgnoreCase("")) {

                    uploadService.uploadImageCommon(dependentModel.getStrImagePath(),
                            utils.replaceSpace(dependentModel.getStrContacts()), "Profile Picture",
                            dependentModel.getStrContacts(),
                            UploadFileType.IMAGE, new App42CallBack() {

                                public void onSuccess(Object response) {
                                    Utils.log(response.toString(), " TAG ");

                                    if (response != null) {


                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {
                                                   /* if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                            String strImagePath = fileList.get(0).getUrl();
                                                   /* SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);*/

                                            DependentDetailPersonal.dependentModel.setStrImageUrl(strImagePath);
                                            //uploadingCount++;
                                            idregisterflag = 2;
                                            insertDependent();
                                              /*  if (uploadingCount == uploadSize) {
                                                    uploadImage();*/
                                        }/* else {
                                                uploadDependentImages();
                                            }*/
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                public void onException(Exception ex) {

                                    if (ex != null) {

                                        App42Exception exception = (App42Exception) ex;
                                        int appErrorCode = exception.getAppErrorCode();

                                        if (appErrorCode == 2100) {
                                                   /* if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                            //uploadingCount++;
                                            idregisterflag = 2;
                                            insertDependent();
                                        } else {

                                               /* if (uploadingCount == uploadSize) {
                                                    uploadImage();
                                                } else*/
//                                                    uploadDependentImages();
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            utils.toast(2, 2, getString(R.string.error));
                                        }

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });

                } else {
                    idregisterflag = 2;
                    insertDependent();
                }
                    /*} else {
                        uploadingCount++;
                        uploadDependentImages();
                    }*/

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                uploadSize = uploadingCount;
                uploadingCount = 0;
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
           /* } else {
               *//* if (uploadingCount == uploadSize)
                    uploadImage();*//*
            }*/

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
            e.printStackTrace();
        }
    }

    public void insertDependent() {
        try {


            if (DependentDetailPersonal.dependentModel != null) {
                jsonDependant = createJson(new JSONObject());
            }


            if (utils.isConnectingToInternet()) {

                StorageService storageService = new StorageService(DependentDetailsMedical.this);

                storageService.insertDocs(jsonDependant,
                        new AsyncApp42ServiceApi.App42StorageServiceListener() {

                            @Override
                            public void onDocumentInserted(Storage response) {

                                if (response != null) {
                                    Utils.log(response.toString(), "message");

                                    if (response.isResponseSuccess()) {
                                        //
                                        Storage.JSONDocument jsonDocument = response.
                                                getJsonDocList().
                                                get(0);

                                        String strDependentDocId = jsonDocument.getDocId();

                                        DependentDetailPersonal.dependentModel.setStrDependentID(strDependentDocId);

                                        try {

                                            File newFile = new File(DependentDetailPersonal.dependentModel.getStrImagePath());
                                            File renameFile = utils.getInternalFileImages(
                                                    DependentDetailPersonal.dependentModel.getStrDependentID());

                                            utils.moveFile(newFile, renameFile);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        if (!Config.strDependentIds.contains(strDependentDocId)) {
                                            Config.strDependentIds.add(strDependentDocId);
                                            sessionManager.saveDependentsIds(Config.strDependentIds);
                                        }

                                        idregisterflag = 3;

                                        Config.dependentModels.add(DependentDetailPersonal.dependentModel);

                                        DependentDetailPersonal.dependentModel = null;
                                        Intent next = new Intent(DependentDetailsMedical.this, DashboardActivity.class);
                                        Config.intSelectedMenu = Config.intRecipientScreen;
                                        startActivity(next);
                                        finish();

                                      /*  if (SignupActivity.dependentModels.size() == 2) {
                                            confirmRegister();
                                        } else {
                                            //createDependentUser();


                                            DependentDetailPersonal.dependentModel = null;
                                            Intent next = new Intent(DependentDetailsMedical.this, DashboardActivity.class);
                                            Config.intSelectedMenu=Config.intRecipientScreen;*/

                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();

                                        utils.toast(1, 1, getString(R.string.dpndnt_details_saved));


                                        //  }

                                        //createDependentUser(strDependentEmail);
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onUpdateDocSuccess(Storage response) {

                            }

                            @Override
                            public void onFindDocSuccess(Storage response) {

                            }

                            @Override
                            public void onInsertionFailed(App42Exception ex) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();

                                if (ex != null) {
                                    Utils.log(ex.getMessage(), "");
                                    utils.toast(2, 2, getString(R.string.error_register));

                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onFindDocFailed(App42Exception ex) {
                            }

                            @Override
                            public void onUpdateDocFailed(App42Exception ex) {
                            }
                        }, Config.collectionDependent);
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }


    public void deleteImage() {

        try {

            if (utils.isConnectingToInternet()) {
/*
                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();*/

                UploadService uploadService = new UploadService(DependentDetailsMedical.this);

               /* if (progressDialog.isShowing())
                    progressDialog.setProgress(1);
*/
                uploadService.removeImage(DependentDetailPersonal.dependentModel.getStrContacts(),
                        DependentDetailPersonal.dependentModel.getStrContacts(),
                        new App42CallBack() {
                            public void onSuccess(Object response) {

                                if (response != null) {

                                    editregisterflag = 1;
                                    edituploadDependentImages();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                if (e != null) {
                                    Utils.log(e.toString(), "Message");

                                    App42Exception exception = (App42Exception) e;
                                    int appErrorCode = exception.getAppErrorCode();
                                    //1401
                                    if (appErrorCode == 2103 || appErrorCode == 2102) {
                                        editregisterflag = 1;
                                        edituploadDependentImages();
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void edituploadDependentImages() {


        try {


            if (utils.isConnectingToInternet()) {

                final DependentModel dependentModel = DependentDetailPersonal.dependentModel;

                final int progress = uploadingCount;

                if (progressDialog.isShowing())
                    progressDialog.setProgress(uploadingCount);

                UploadService uploadService = new UploadService(DependentDetailsMedical.this);

                if (!dependentModel.getStrName().equalsIgnoreCase(
                        DependentDetailsMedical.this.getResources().getString(R.string.add_dependent))) {

                    if (dependentModel.getStrImagePath() != null &&
                            !dependentModel.getStrImagePath().equalsIgnoreCase("")) {

                        uploadService.uploadImageCommon(dependentModel.getStrImagePath(),
                                utils.replaceSpace(dependentModel.getStrContacts()), "Profile Picture",
                                dependentModel.getStrContacts(),
                                UploadFileType.IMAGE, new App42CallBack() {

                                    public void onSuccess(Object response) {
                                        Utils.log(response.toString(), " Error ");

                                        if (response != null) {

                                            try {

                                                File newFile = new File(DependentDetailPersonal.dependentModel.getStrImagePath());
                                                File renameFile = utils.getInternalFileImages(
                                                        DependentDetailPersonal.dependentModel.getStrDependentID());

                                                utils.moveFile(newFile, renameFile);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            Upload upload = (Upload) response;
                                            ArrayList<Upload.File> fileList = upload.getFileList();

                                            if (fileList.size() > 0) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                String strImagePath = fileList.get(0).getUrl();
                                                   /* SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);*/

                                                DependentDetailPersonal.dependentModel.setStrImageUrl(strImagePath);
                                                //uploadingCount++;

                                                editregisterflag = 2;
                                                updateDependentData();
                                                    /*  if (uploadingCount == uploadSize) {
                                                    uploadImage();*/
                                            }/* else {
                                                uploadDependentImages();
                                            }*/
                                        } else {
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                        }
                                    }

                                    public void onException(Exception ex) {

                                        if (ex != null) {
                                            Utils.log(ex.toString(), " TAG ");
                                            App42Exception exception = (App42Exception) ex;
                                            int appErrorCode = exception.getAppErrorCode();

                                            if (appErrorCode == 2100) {
                                                   /* if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                                //uploadingCount++;
                                                editregisterflag = 2;
                                                updateDependentData();

                                            } else {

                                               /* if (uploadingCount == uploadSize) {
                                                    uploadImage();
                                                } else*/
//                                                    uploadDependentImages();
                                            }

                                        } else {
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                        }
                                    }
                                });

                    } else {
                        uploadingCount++;
                        edituploadDependentImages();
                    }
                } else {
                    uploadingCount++;
                    edituploadDependentImages();
                }

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                uploadSize = uploadingCount;
                uploadingCount = 0;
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
           /* } else {
               *//* if (uploadingCount == uploadSize)
                    uploadImage();*//*
            }*/

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
            e.printStackTrace();
        }

    }

    private void backToDashBoard() {
        try {
            utils.toast(1, 1, getString(R.string.your_details_saved));

            //Config.clientModels.setCustomerModel(Config.customerModel);

//                                        SignupActivity._mViewPager.setCurrentItem(1);
            editregisterflag = 3;
            Intent next = new Intent(DependentDetailsMedical.this, DashboardActivity.class);
            Config.intSelectedMenu = Config.intRecipientScreen;
            startActivity(next);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDependentData() {
        final List<String> dependentIdsList = new ArrayList<>();
        dependentIdsList.clear();
        if (DependentDetailPersonal.dependentModel != null) {

            jsonDependant = createJson(new JSONObject());
            dependentIdsList.addAll(sessionManager.getUpdateDependent());
            dependentIdsList.add(DependentDetailPersonal.dependentModel.getStrDependentID());
        }
        if (utils.isConnectingToInternet()) {


            StorageService storageService = new StorageService(DependentDetailsMedical.this);

            //JSONObject jsonToUpdate = new JSONObject();

            storageService.updateDocs(jsonDependant,
                    DependentDetailPersonal.dependentModel.getStrDependentID(),
                    Config.collectionDependent, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    Utils.log(o.toString(), "LOG");

                                    updateDependentDataInDb();
                                } else {

                                }
                            } catch (Exception e1) {

                                e1.printStackTrace();
                            }

                        }

                        @Override
                        public void onException(Exception e) {

                            sessionManager.saveUpdateDependent(dependentIdsList);
                            updateDependentDataInDb();

                        }
                    });

        } else {
            sessionManager.saveUpdateDependent(dependentIdsList);
            updateDependentDataInDb();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //strAge = utils.getAge(DependentDetailPersonal.iDate,DependentDetailPersonal.iMonth,DependentDetailPersonal.iYear);
        String strage = DependentDetailPersonal.dependentModel.getStrDob();
        try {
            date = Utils.writeFormatActivityYear.parse(strage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        strAge = utils.getAge(date);
        editAge.setText(strAge);

        if (DependentDetailPersonal.editflag && DependentDetailPersonal.mPosition > -1) {

            if (DependentDetailPersonal.dependentModel != null) {
                editDiseases.setText(DependentDetailPersonal.dependentModel.getStrIllness());
                editNotes.setText(DependentDetailPersonal.dependentModel.getStrNotes());
            }

        }

        //DependentDetailPersonalActivity.dependentModel.setStrAge(strAge);
    }

}
