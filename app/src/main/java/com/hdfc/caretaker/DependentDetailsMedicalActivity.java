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

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.fragments.AddDependentFragment;
import com.hdfc.caretaker.fragments.ConfirmFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DependentDetailsMedicalActivity extends AppCompatActivity {

    public static Date date = null;
    public static int uploadSize, uploadingCount = 0;
    public static JSONObject jsonDependant;
    private static int idregisterflag = 0;
    private static int editregisterflag = 0;
    private static String jsonDocId;
    private static int dependentFlag = 0;
    private Utils utils;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;
    private Button buttonContinue;
    private int iDependentCount = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependent_details_medical);

        utils = new Utils(DependentDetailsMedicalActivity.this);
        utils.setStatusBarColor("#2196f3");

        progressDialog = new ProgressDialog(DependentDetailsMedicalActivity.this);

        editAge = (EditText) findViewById(R.id.editAge);
        editDiseases = (EditText) findViewById(R.id.editDiseases);
        editNotes = (EditText) findViewById(R.id.editNotes);

        idregisterflag = 0;
        editregisterflag = 0;

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

            //


                storeData();


        }
    }

    private void createJson(){
        DependentModel dependentModel = DependentDetailPersonalActivity.dependentModel;
        //
        jsonDependant = new JSONObject();
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

    }

    public void skip() {


        if (DependentDetailPersonalActivity.dependentModel != null) {

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            strAge = editAge.getText().toString().trim();

            DependentDetailPersonalActivity.dependentModel.setStrAge(strAge);



            //createJson();

            if(DependentDetailPersonalActivity.editflag) {

                if (DependentDetailPersonalActivity.dependentModel.getStrImagePath() != null &&
                        !DependentDetailPersonalActivity.dependentModel.getStrImagePath().equalsIgnoreCase("")
                        &&!SignupActivity.dependentModels.get(DependentDetailPersonalActivity.mPosition+1).getStrImagePath().equalsIgnoreCase(DependentDetailPersonalActivity.dependentModel.getStrImagePath())) {

                    if(editregisterflag==0)
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

            }else {
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

        //CustomViewPager.setPagingEnabled(true);

              /*  utils.retrieveConfirmDependants();

                if (ConfirmFragment.adapter != null)
                    ConfirmFragment.adapter.notifyDataSetChanged();*/

        if (ConfirmFragment.adapter != null) {
            ConfirmFragment.prepareListData();
                    ConfirmFragment.adapter.notifyDataSetChanged();
        }
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

            DependentDetailPersonalActivity.dependentModel.setStrAge(strAge);
            DependentDetailPersonalActivity.dependentModel.setStrIllness(strDiseases);
            DependentDetailPersonalActivity.dependentModel.setStrNotes(strNotes);

          /*  if (Config.dependentModel != null) {
                SignupActivity.dependentModels.remove(DependentDetailPersonalActivity.dependentModel);
                Config.dependentModel = null;
            }


            //

            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);*/

            if(DependentDetailPersonalActivity.editflag) {

                if (DependentDetailPersonalActivity.dependentModel.getStrImagePath() != null &&
                        !DependentDetailPersonalActivity.dependentModel.getStrImagePath().equalsIgnoreCase("")
                        &&!SignupActivity.dependentModels.get(0).getStrImagePath().equalsIgnoreCase(DependentDetailPersonalActivity.dependentModel.getStrImagePath())) {

                    if(editregisterflag==0)
                        deleteImage();

                    if (editregisterflag == 1)
                        edituploadDependentImages();

                    if (editregisterflag == 2)
                        updateDependentData();

                } else {
                    if (editregisterflag == 0)
                        updateDependentData();
                }


            }else {

               /* if (idregisterflag == 0)
                    uploadDependentImages();

                if (idregisterflag == 1)
                    insertDependent();

                if (idregisterflag == 2)
                    createDependentUser();
*/
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

    public void callSuccess() {

        //remove "Add Dependent"

        for (int i = 0; i < SignupActivity.dependentModels.size(); i++) {
            if (SignupActivity.dependentModels.get(i).getStrName().
                    equalsIgnoreCase(DependentDetailsMedicalActivity.this.
                            getResources().
                            getString(R.string.add_dependent))) {
                SignupActivity.dependentModels.remove(i);
                break;
            }
        }

        if (progressDialog.isShowing())
            progressDialog.dismiss();
        utils.toast(2, 2, getString(R.string.register_success));

        Intent intent = new Intent(DependentDetailsMedicalActivity.this, AccountSuccessActivity.class);
        startActivity(intent);
        DependentDetailsMedicalActivity.this.finish();
    }


    public void edituploadDependentImages(){


            try {


                if (utils.isConnectingToInternet()) {

                    final DependentModel dependentModel =  DependentDetailPersonalActivity.dependentModel;

                    final int progress = uploadingCount;

                    if (progressDialog.isShowing())
                        progressDialog.setProgress(uploadingCount);

                    UploadService uploadService = new UploadService(DependentDetailsMedicalActivity.this);

                    if (!dependentModel.getStrName().equalsIgnoreCase(
                            DependentDetailsMedicalActivity.this.getResources().getString(R.string.add_dependent))) {

                        if (dependentModel.getStrImagePath() != null &&
                                !dependentModel.getStrImagePath().equalsIgnoreCase("")) {

                            uploadService.uploadImageCommon(dependentModel.getStrImagePath(),
                                    utils.replaceSpace(dependentModel.getStrContacts()), "Profile Picture",
                                    dependentModel.getStrContacts(),
                                    UploadFileType.IMAGE, new App42CallBack() {

                                        public void onSuccess(Object response) {
                                            Utils.log(response.toString()," Error ");

                                            if (response != null) {

                                                Upload upload = (Upload) response;
                                                ArrayList<Upload.File> fileList = upload.getFileList();

                                                if (fileList.size() > 0) {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    String strImagePath = fileList.get(0).getUrl();
                                                   /* SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);*/

                                                    DependentDetailPersonalActivity.dependentModel.setStrImageUrl(strImagePath);
                                                    //uploadingCount++;

                                                    editregisterflag=2;
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
                                                Utils.log(ex.toString()," TAG ");
                                                App42Exception exception = (App42Exception) ex;
                                                int appErrorCode = exception.getAppErrorCode();

                                                if (appErrorCode == 2100) {
                                                   /* if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                                    //uploadingCount++;
                                                    editregisterflag=2;
                                                    updateDependentData();

                                                }else {

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
                    uploadingCount=0;
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
    public void uploadDependentImages() {

        try {


                if (utils.isConnectingToInternet()) {

                    final DependentModel dependentModel =  DependentDetailPersonalActivity.dependentModel;

                    //final int progress = uploadingCount;

                   /* if (progressDialog.isShowing())
                        progressDialog.setProgress(uploadingCount);*/

                    UploadService uploadService = new UploadService(DependentDetailsMedicalActivity.this);

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
                                            Utils.log(response.toString()," TAG ");

                                            if (response != null) {

                                                Upload upload = (Upload) response;
                                                ArrayList<Upload.File> fileList = upload.getFileList();

                                                if (fileList.size() > 0) {
                                                   /* if (progressDialog.isShowing())
                                                        progressDialog.dismiss();*/
                                                    String strImagePath = fileList.get(0).getUrl();
                                                   /* SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);*/

                                                    DependentDetailPersonalActivity.dependentModel.setStrImageUrl(strImagePath);
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
                                                }else {

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
                    uploadingCount=0;
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


            if (DependentDetailPersonalActivity.dependentModel != null) {
                createJson();
            }

          /*  SignupActivity.dependentModels.get(iDependentCount).setIntHealthBp(70 +
                    iDependentCount);
            SignupActivity.dependentModels.get(iDependentCount).
                    setIntHealthHeartRate(80 + iDependentCount);
            SignupActivity.dependentModels.get(iDependentCount).setStrCustomerID(jsonDocId);*/

            //

            if (utils.isConnectingToInternet()) {

                StorageService storageService = new StorageService(DependentDetailsMedicalActivity.this);

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

                                        DependentDetailPersonalActivity.dependentModel.setStrDependentID(strDependentDocId);

                                      /*  SignupActivity.dependentModels.
                                                get(iSelectedDependent).
                                                setStrDependentID(strDependentDocId);*/

                                        if (!Config.strDependentIds.contains(strDependentDocId))
                                            Config.strDependentIds.add(strDependentDocId);
                                        //

                                        //iDependentCount++;

                                        /*if (SignupActivity.dependentModels.size() == iDependentCount)
                                            gotoDependnetList();
                                        else*/
                                        idregisterflag = 3;

                                        /*if (Config.dependentModel != null) {
                                            SignupActivity.dependentModels.remove(DependentDetailPersonalActivity.dependentModel);
                                            Config.dependentModel = null;
                                        }*/
                                        SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);

                                        if (SignupActivity.dependentModels.size() == 2) {
                                            confirmRegister();
                                        } else {
                                            //createDependentUser();


                                            DependentDetailPersonalActivity.dependentModel = null;
                                            Intent next = new Intent(DependentDetailsMedicalActivity.this, SignupActivity.class);

                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();

                                            utils.toast(1, 1, getString(R.string.dpndnt_details_saved));

                                            startActivity(next);
                                            finish();

                                        }

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

/*                                    iDependentCount++;

                                    if (SignupActivity.dependentModels.size() == iDependentCount)
                                        gotoDependnetList();
                                    else*/
                                    //createDependentUser();

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

    private void confirmRegister() {

        try {


            if (utils.isConnectingToInternet()) {

                StorageService storageService = new StorageService(DependentDetailsMedicalActivity.this);

                JSONObject jsonToUpdate = new JSONObject();

                jsonToUpdate.put("customer_register", true);

                storageService.updateDocs(jsonToUpdate,
                        Config.customerModel.getStrCustomerID(),
                        Config.collectionCustomer, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {
                                try {
                                    if (o != null) {
                                        Utils.log(o.toString(), "LOG");
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();


                                        DependentDetailPersonalActivity.dependentModel = null;
                                        Intent next = new Intent(DependentDetailsMedicalActivity.this, SignupActivity.class);

                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();

                                        utils.toast(1, 1, getString(R.string.dpndnt_details_saved));

                                        startActivity(next);
                                        finish();
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    utils.toast(2, 2, getString(R.string.error));
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    e1.printStackTrace();
                                }

                            }

                            @Override
                            public void onException(Exception e) {

                                try {

                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    if (e != null) {
                                        Utils.log(e.toString(),"TAG");
                                        utils.toast(2, 2, getString(R.string.error));
                                    }else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }


                            }
                        });

            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void createDependentUser() {

        try {

            if (utils.isConnectingToInternet()) {

                UserService userService = new UserService(DependentDetailsMedicalActivity.this);

                ArrayList<String> roleList = new ArrayList<>();
                roleList.add("dependent");

                //Utils.log(" 2 ", " IN 0");

                userService.onCreateUser(DependentDetailPersonalActivity.dependentModel.getStrContacts(),
                        ActivityGuruPersonalInfo.strPass, DependentDetailPersonalActivity.dependentModel.getStrEmail(),
                        new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {

                                 /*   iDependentCount++;

                                    if (SignupActivity.dependentModels.size() == iDependentCount)
                                        callSuccess();
                                    else
                                        createDependent();
                                } else {*/



                                    //Config.clientModels.setCustomerModel(Config.customerModel);

//                                        SignupActivity._mViewPager.setCurrentItem(1);
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

                                        Intent previos = new Intent(DependentDetailsMedicalActivity.this,DependentDetailPersonalActivity.class);
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

    public void deleteImage() {

        try {

            if (utils.isConnectingToInternet()) {
/*
                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();*/

                UploadService uploadService = new UploadService(DependentDetailsMedicalActivity.this);

               /* if (progressDialog.isShowing())
                    progressDialog.setProgress(1);
*/
                uploadService.removeImage(DependentDetailPersonalActivity.dependentModel.getStrContacts(),
                        DependentDetailPersonalActivity.dependentModel.getStrContacts(),
                        new App42CallBack() {
                            public void onSuccess(Object response) {

                                if(response!=null){

                                    editregisterflag=1;
                                    edituploadDependentImages();
                                }else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                            @Override
                            public void onException(Exception e) {

                                if(e!=null) {
                                    Utils.log(e.toString(), "Message");

                                    App42Exception exception = (App42Exception) e;
                                    int appErrorCode = exception.getAppErrorCode();
                                    //1401
                                    if (appErrorCode == 2103 || appErrorCode == 2102) {
                                        editregisterflag=1;
                                        edituploadDependentImages();
                                    } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }

                                }else{
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }
    public void updateDependentData(){

        if (DependentDetailPersonalActivity.dependentModel != null) {
            createJson();
        }
        if (utils.isConnectingToInternet()) {

            StorageService storageService = new StorageService(DependentDetailsMedicalActivity.this);

            //JSONObject jsonToUpdate = new JSONObject();

            storageService.updateDocs(jsonDependant,
                    DependentDetailPersonalActivity.dependentModel.getStrDependentID(),
                    Config.collectionDependent, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();

                                if (o != null) {
                                    Utils.log(o.toString(), "LOG");

                                    utils.toast(1, 1, getString(R.string.your_details_saved));

                                    //Config.clientModels.setCustomerModel(Config.customerModel);

//                                        SignupActivity._mViewPager.setCurrentItem(1);
                                    editregisterflag=3;
                                    Intent next = new Intent(DependentDetailsMedicalActivity.this,SignupActivity.class);
                                    startActivity(next);
                                    finish();

                                } else {

                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                utils.toast(2, 2, getString(R.string.error));
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                e1.printStackTrace();
                            }

                        }

                        @Override
                        public void onException(Exception e) {
                            Utils.log(e.getMessage(), "EE");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            if (e != null) {
                                utils.toast(2, 2, getString(R.string.error));
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
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

        if (DependentDetailPersonalActivity.editflag && DependentDetailPersonalActivity.mPosition > -1) {

            if (DependentDetailPersonalActivity.dependentModel != null) {
                editDiseases.setText(DependentDetailPersonalActivity.dependentModel.getStrIllness());
                editNotes.setText(DependentDetailPersonalActivity.dependentModel.getStrNotes());
            }

        }

        //DependentDetailPersonalActivity.dependentModel.setStrAge(strAge);
    }
}
