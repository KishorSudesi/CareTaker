package com.hdfc.caretaker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    private Utils utils;
    private EditText editAge, editDiseases, editNotes;
    private String strAge, strDiseases, strNotes;
    private ProgressDialog progressDialog;
    private Button buttonContinue;
    public static int uploadSize, uploadingCount=0;
    private int iDependentCount = 0;
    private static int idregisterflag = 0;
    private static String jsonDocId;
    public static JSONObject jsonDependant;




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
            createJson();

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
            jsonDependant.put("customer_id", jsonDocId);

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

            if (Config.dependentModel != null) {
                SignupActivity.dependentModels.remove(DependentDetailPersonalActivity.dependentModel);
                Config.dependentModel = null;
            }
            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);


            createJson();

            if(idregisterflag==0)
                uploadDependentImages();

            if(idregisterflag==1)
                insertDependent();

            if(idregisterflag==2)
                createDependentUser();


            // DependentDetailPersonalActivity.dependentModel.setStrImageUrl("");



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

            if (Config.dependentModel != null) {
                SignupActivity.dependentModels.remove(DependentDetailPersonalActivity.dependentModel);
                Config.dependentModel = null;
            }

            SignupActivity.dependentModels.add(DependentDetailPersonalActivity.dependentModel);

            if(idregisterflag==0)
                uploadDependentImages();

            if(idregisterflag==1)
                insertDependent();

            if(idregisterflag==2)
                createDependentUser();

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


    public void uploadDependentImages() {

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
                                    utils.replaceSpace(dependentModel.getStrName()), "Profile Picture",
                                    dependentModel.getStrEmail(),
                                    UploadFileType.IMAGE, new App42CallBack() {

                                        public void onSuccess(Object response) {
                                            Utils.log(response.toString()," TAG ");

                                            if (response != null) {

                                                Upload upload = (Upload) response;
                                                ArrayList<Upload.File> fileList = upload.getFileList();

                                                if (fileList.size() > 0) {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    String strImagePath = fileList.get(0).getUrl();
                                                    SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);

                                                    DependentDetailPersonalActivity.dependentModel.setStrImageUrl(strImagePath);
                                                    //uploadingCount++;
                                                    idregisterflag=1;
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
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    //uploadingCount++;
                                                    insertDependent();
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
                            uploadDependentImages();
                        }
                    } else {
                        uploadingCount++;
                        uploadDependentImages();
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

    public void insertDependent() {
        try {



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

                                      /*  SignupActivity.dependentModels.
                                                get(iSelectedDependent).
                                                setStrDependentID(strDependentDocId);*/

                                        if (!Config.strDependentIds.contains(strDependentDocId))
                                            Config.strDependentIds.add(strDependentDocId);
                                        //

                                        iDependentCount++;

                                        /*if (SignupActivity.dependentModels.size() == iDependentCount)
                                            gotoDependnetList();
                                        else*/
                                            idregisterflag=2;
                                            createDependentUser();

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
//                                    utils.toast(2, 2, getString(R.string.error_register));

                                    iDependentCount++;

                                    if (SignupActivity.dependentModels.size() == iDependentCount)
                                        gotoDependnetList();
                                    else
                                        createDependentUser();

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

    public void createDependentUser() {

        try {

            if (utils.isConnectingToInternet()) {

                UserService userService = new UserService(DependentDetailsMedicalActivity.this);

                ArrayList<String> roleList = new ArrayList<>();
                roleList.add("dependent");

                //Utils.log(" 2 ", " IN 0");

                userService.onCreateUser( DependentDetailPersonalActivity.dependentModel.getStrEmail(),
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
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    utils.toast(1, 1, getString(R.string.your_details_saved));

                                    //Config.clientModels.setCustomerModel(Config.customerModel);

//                                        SignupActivity._mViewPager.setCurrentItem(1);
                                    idregisterflag = 3;
                                    Intent next = new Intent(DependentDetailsMedicalActivity.this,SignupActivity.class);
                                    startActivity(next);

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                if (e != null) {
                                    Utils.log(e.getMessage(), "");

                                    int appErrorCode = ((App42Exception) e).getAppErrorCode();

                                    if (appErrorCode == 2005 || appErrorCode == 2001) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(DependentDetailsMedicalActivity.this);
                                        builder.setTitle("Care Taker");
                                        builder.setMessage(getString(R.string.email__already_exists))
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent i = new Intent(DependentDetailsMedicalActivity.this,LoginActivity.class);
                                                        startActivity(i);                                                            }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();

                                       /* iDependentCount++;

                                        if (SignupActivity.dependentModels.size() == iDependentCount)
                                            callSuccess();
                                        else
                                            createDependentUser(strDependentEmail);*/
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

        //DependentDetailPersonalActivity.dependentModel.setStrAge(strAge);
    }
}
