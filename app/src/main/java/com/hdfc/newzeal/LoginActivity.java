package com.hdfc.newzeal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.model.FileModel;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static Libs libs;
    private static ProgressDialog progressDialog;
    private static String userName;
    /* private static Thread backgroundThread;
     private static Handler threadHandler;*/
    private RelativeLayout relLayout;
    private EditText editEmail, editPassword;
    private RelativeLayout layoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        libs = new Libs(LoginActivity.this);
        progressDialog = new ProgressDialog(LoginActivity.this);

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            imgBg.setImageBitmap(Libs.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_blue, Config.intScreenWidth, Config.intScreenHeight));

            //NewZeal.dbCon = DbCon.getInstance(LoginActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordfield();
            }
        });

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showPasswordfield();
                libs.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text), getResources().getDrawable(R.drawable.edit_text_blue), editPassword);
            }
        });

        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                libs.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text), getResources().getDrawable(R.drawable.edit_text_blue), editEmail);
            }
        });


        //According to http://android-developers.blogspot.com.es/2013/08/some-securerandom-thoughts.html

       /* byte[] encrypted_data = myCipherData.getData();
        IvParameterSpec iv = new IvParameterSpec(myCipherData.getIV());
        Libs.log(myCipher.decryptUTF8(encrypted_data, iv), "");*/
    }

    private void showPasswordfield() {
        if (relLayout.getVisibility() == View.GONE) {

            relLayout.setVisibility(View.VISIBLE);
            try {

                TranslateAnimation ta = new TranslateAnimation(0, 0, 15, Animation.RELATIVE_TO_SELF);
                ta.setDuration(1000);
                ta.setFillAfter(true);
                relLayout.startAnimation(ta);

                TranslateAnimation ed = new TranslateAnimation(0, 0, 15, Animation.RELATIVE_TO_SELF);
                ed.setDuration(1000);
                ed.setFillAfter(true);
                editEmail.startAnimation(ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void goToWho(View v) {

        Intent selection = new Intent(LoginActivity.this, CareSelectionActivity.class);
        startActivity(selection);
    }

    public void validateLogin(View v) {

        libs.setEditTextDrawable(editEmail, getResources().getDrawable(R.drawable.edit_text));
        libs.setEditTextDrawable(editPassword, getResources().getDrawable(R.drawable.edit_text));

        if (relLayout.getVisibility() == View.VISIBLE) {

            editEmail.setError(null);
            editPassword.setError(null);

            userName = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(password)) {
                editPassword.setError(getString(R.string.error_field_required));
                focusView = editPassword;
                cancel = true;
            }

            if (TextUtils.isEmpty(userName)) {
                editEmail.setError(getString(R.string.error_field_required));
                focusView = editEmail;
                cancel = true;
            } else if (!libs.isEmailValid(userName)) {
                editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {

                if (libs.isConnectingToInternet()) {

                    progressDialog.setMessage(getString(R.string.process_login));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    UserService userService = new UserService(LoginActivity.this);

                    /*try{
                        PRNGFixes.apply();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }*/

                   /* MyCipher myCipher = new MyCipher(Config.string);
                    MyCipherData myCipherData = myCipher.encryptUTF8(password);

                    String strEncryptedPassword = Libs.bytesToHex(myCipherData.getData());*/

                    String strPass = null;
                    try {
                        strPass = AESCrypt.encrypt(Config.string, password);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }

                    userService.authenticate(userName, strPass, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {

                            StorageService storageService = new StorageService(LoginActivity.this);

                            storageService.findDocsByKeyValue("customer_email", userName, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                @Override
                                public void onDocumentInserted(Storage response) {

                                }

                                @Override
                                public void onUpdateDocSuccess(Storage response) {

                                }

                                @Override
                                public void onFindDocSuccess(Storage response) {

                                    if (response.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                        String strDocument = jsonDocument.getJsonDoc();

                                        Config.jsonDocId = jsonDocument.getDocId();

                                        try {
                                            Config.jsonObject = new JSONObject(strDocument);

                                            Config.strUserName = userName;

                                           /* try {
                                                Gson gson = new Gson();
                                                CustomerModel customer1 = gson.fromJson(String.valueOf(Config.jsonObject), new TypeToken<CustomerModel>(){}.getType());

                                                Libs.log(String.valueOf(customer1.getStrEmail()+" ! "+ customer1.getDependentModels().size()), "");

                                                Libs.log(String.valueOf(customer1.getDependentModels().get(0).getIntHealthBp()+" @ "+ customer1.getDependentModels().get(0).getDependentHealthModels().size()), "");

                                                Libs.log(String.valueOf(customer1.getDependentModels().get(0).getDependentNotificationModels().size()+" # "+
                                                        customer1.getDependentModels().get(0).getDependentNotificationModels().get(0).getStrNotificationTime()), "");

                                                Libs.log(String.valueOf(customer1.getDependentModels().get(0).getActivityModels().size()+" $ "+
                                                        customer1.getDependentModels().get(0).getActivityModels().get(0).getStrActivityDate()), "");

                                                Libs.log(String.valueOf(customer1.getDependentModels().get(0).getActivityModels().get(0).getActivityFeedBackModels().size()+" % "+
                                                        customer1.getDependentModels().get(0).getActivityModels().get(0).getActivityFeedBackModels().get(0).getIntFeedBackRating()), "");

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }*/

                                            //get all Images by user name start

                                            UploadService uploadService = new UploadService(LoginActivity.this);

                                            uploadService.getAllFilesByUser(Config.strUserName, new App42CallBack() {
                                                public void onSuccess(Object response) {

                                                    Upload upload = (Upload) response;
                                                    ArrayList<Upload.File> fileList = upload.getFileList();

                                                    if (fileList.size() > 0) {

                                                        for (int i = 0; i < fileList.size(); i++) {
                                                            Config.fileModels.add(
                                                                    new FileModel(fileList.get(i).getName()
                                                                            , fileList.get(i).getUrl(),
                                                                            fileList.get(i).getType()));

                                                        }

                                                        libs.parseData();

                                                        progressDialog.dismiss();

                                                        libs.toast(1, 1, getString(R.string.success_login));
                                                        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                        dashboardIntent.putExtra("WHICH_SCREEN", Config.intDashboardScreen);
                                                        Config.boolIsLoggedIn = true;
                                                        startActivity(dashboardIntent);
                                                        finish();

                                                    } else {
                                                        progressDialog.dismiss();
                                                        libs.toast(2, 2, getString(R.string.error_load_images));
                                                    }
                                                }

                                                public void onException(Exception ex) {
                                                    progressDialog.dismiss();
                                                    libs.toast(2, 2, getString(R.string.error_load_images));
                                                    Libs.log(ex.getMessage(), " ");
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else libs.toast(2, 2, getString(R.string.error));

                                }

                                @Override
                                public void onInsertionFailed(App42Exception ex) {

                                }

                                @Override
                                public void onFindDocFailed(App42Exception ex) {
                                    progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(ex.getMessage());
                                        JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                        String strMess = jsonObjectError.getString("details");

                                        libs.toast(2, 2, strMess);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                @Override
                                public void onUpdateDocFailed(App42Exception ex) {

                                }
                            });
                        }

                        @Override
                        public void onException(Exception e) {

                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(e.getMessage());
                                JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                String strMess = jsonObjectError.getString("details");

                                libs.toast(2, 2, strMess);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

                } else libs.toast(2, 2, getString(R.string.warning_internet));
            }
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }


   /* public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                if (!strResponse.equalsIgnoreCase("")) {
                    File fileJson = libs.createFileInternal("storage/local.json");

                    FileWriter fos;
                    try {
                        fos = new FileWriter(fileJson.getAbsoluteFile());
                        fos.write(strResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                }

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            progressDialog.dismiss();

            if (isSuccess) {
                Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                libs.toast(1, 1, getString(R.string.success_login));
                startActivity(dashboardIntent);
                finish();
            } else {
                libs.toast(2, 2, getString(R.string.error));
            }
        }
    }*/

}
