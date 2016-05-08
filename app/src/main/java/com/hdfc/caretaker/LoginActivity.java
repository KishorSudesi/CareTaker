package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.CheckView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    public static Utils utils;
    private static ProgressDialog progressDialog;
    private static String userName;
    /* private static Thread backgroundThread;
     private static Handler threadHandler;*/
    private RelativeLayout relLayout;
    private EditText editEmail, editPassword;
    private RelativeLayout layoutLogin;
    private TextView txtForgotPassword;

    private CheckView checkView;
    private TextView editTextCaptcha;
    private EditText forgotpasswordUserName;
    private ImageButton reloadCaptcha;
    private char[] res = new char[4];
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);

        utils = new Utils(LoginActivity.this);
        progressDialog = new ProgressDialog(LoginActivity.this);

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.bg_blue, Config.intScreenWidth, Config.intScreenHeight));
            }

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }

        //todo date format
        Utils.log(utils.convertDateToString(new Date()), "DATE");


        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordfield();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showPasswordfield();
                utils.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text),
                        getResources().getDrawable(R.drawable.edit_text_blue), editPassword);
            }
        });

        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                utils.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text),
                        getResources().getDrawable(R.drawable.edit_text_blue), editEmail);
            }
        });


        //According to http://android-developers.blogspot.com.es/2013/08/some-securerandom-thoughts.html

       /* byte[] encrypted_data = myCipherData.getData();
        IvParameterSpec iv = new IvParameterSpec(myCipherData.getIV());
        Utils.log(myCipher.decryptUTF8(encrypted_data, iv), "");*/
    }

    private void showForgotPasswordDialog(){

        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.forgot_password_custom_dialog, null);

        editTextCaptcha = (EditText) promptsView.findViewById(R.id.editTextCaptcha);
        checkView = (CheckView) promptsView.findViewById(R.id.checkview2);
        forgotpasswordUserName = (EditText) promptsView.findViewById(R.id.editTextUserName);
        reloadCaptcha = (ImageButton) promptsView.findViewById(R.id.reloadCaptcha);

        res = checkView.getValidataAndSetImage();

        reloadCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                res = checkView.getValidataAndSetImage();
            }
        });

        // Create the dialog (without showing)
        final AlertDialog d = new AlertDialog.Builder(this).setTitle("Forgot Password?")
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null).setView(promptsView).create();

        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user input and set it to result

                String scheck = new String(res);
                String string = editTextCaptcha.getText().toString();
                boolean b = string.equals(scheck);

                email = forgotpasswordUserName.getText().toString();

                if (TextUtils.isEmpty(email)) {

                    utils.toast(2, 2, getString(R.string.error_invalid_email));

                } else if (!utils.isEmailValid(email)) {

                    utils.toast(2, 2, getString(R.string.error_invalid_email));

                } else if (!b) {

                    utils.toast(2, 2, getString(R.string.enter_captcha));

                } else {

                    resetPassword(email);
                    d.dismiss();
                }
            }
        });
    }


    private void resetPassword(String userEmail){

        if (utils.isConnectingToInternet()) {

            progressDialog.setMessage(getString(R.string.verify_identity_password));
            progressDialog.setCancelable(false);
            progressDialog.show();

            UserService userService = new UserService(LoginActivity.this);


            userService.resetUserPassword(userEmail, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    progressDialog.dismiss();
                    utils.toast(1, 1, "New password has been sent to your E-mail id");
                }

                @Override
                public void onException(Exception e) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                        String strMess = jsonObjectError.getString("details");

                        utils.toast(2, 2, strMess);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });

        } else utils.toast(2, 2, getString(R.string.warning_internet));

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

    /*public void goToWho(View v) {
        Intent selection = new Intent(LoginActivity.this, CareSelectionActivity.class);
        startActivity(selection);
    }*/

    public void validateLogin(View v) {

        utils.setEditTextDrawable(editEmail, getResources().getDrawable(R.drawable.edit_text));
        utils.setEditTextDrawable(editPassword, getResources().getDrawable(R.drawable.edit_text));

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
            } else if (!utils.isEmailValid(userName)) {
                editEmail.setError(getString(R.string.error_invalid_email));
                focusView = editEmail;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {

                if (utils.isConnectingToInternet()) {

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


                    String strEncryptedPassword = Utils.bytesToHex(myCipherData.getData());*/

                    /*String strPass = null;
                    try {
                        strPass = AESCrypt.encrypt(Config.string, password);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
*/
                    userService.authenticate(userName, password, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {

                            if (o != null) {
                                Config.strUserName = userName;

                                User user = (User) o;

                                ArrayList<String> roleList = user.getRoleList();

                                //todo check rolelist
                                Utils.log(roleList.get(0), " ROLE ");
                                //roleList.size()>0 && roleList.get(0).equalsIgnoreCase("provider");

                                utils.fetchCustomer(progressDialog, 1);
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
                                try {
                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");

                                    utils.toast(2, 2, strMess);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

                } else utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }
    }


    @Override
    public void onBackPressed() {
        /*super.onBackPressed();
        moveTaskToBack(true);*/
        Intent selection = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(selection);
        finish();
    }


   /* public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                if (!strResponse.equalsIgnoreCase("")) {
                    File fileJson = utils.createFileInternal("storage/local.json");

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
                utils.toast(1, 1, getString(R.string.success_login));
                startActivity(dashboardIntent);
                finish();
            } else {
                utils.toast(2, 2, getString(R.string.error));
            }
        }
    }*/

    /* try {
            Gson gson = new Gson();
            CustomerModel customer1 = gson.fromJson(String.valueOf(Config.jsonObject), new TypeToken<CustomerModel>(){}.getType());

            Utils.log(String.valueOf(customer1.getStrEmail()+" ! "+ customer1.getDependentModels().size()), "");

            Utils.log(String.valueOf(customer1.getDependentModels().get(0).getIntHealthBp()+" @ "+ customer1.getDependentModels().get(0).getHealthModels().size()), "");

            Utils.log(String.valueOf(customer1.getDependentModels().get(0).getDependentNotificationModels().size()+" # "+
            customer1.getDependentModels().get(0).getDependentNotificationModels().get(0).getStrNotificationTime()), "");

            Utils.log(String.valueOf(customer1.getDependentModels().get(0).getActivityModels().size()+" $ "+
            customer1.getDependentModels().get(0).getActivityModels().get(0).getStrActivityDate()), "");

            Utils.log(String.valueOf(customer1.getDependentModels().get(0).getActivityModels().get(0).getFeedBackModels().size()+" % "+
            customer1.getDependentModels().get(0).getActivityModels().get(0).getFeedBackModels().get(0).getIntFeedBackRating()), "");

        } catch (Exception e) {
        e.printStackTrace();
        }*/
}
