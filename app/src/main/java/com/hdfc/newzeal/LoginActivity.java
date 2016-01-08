package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hdfc.config.NewZeal;
import com.hdfc.db.DbCon;
import com.hdfc.libs.Libs;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout relLayout;

    private EditText editEmail, editPassword;

    private RelativeLayout layoutLogin;

    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        libs =new Libs(LoginActivity.this);

        //libs.setupUI();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            imgBg.setImageBitmap(Libs.decodeSampledBitmapFromResource(getResources(), R.drawable.bg_blue, screenWidth, screenHeight));

            NewZeal.dbCon = DbCon.getInstance(LoginActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(relLayout.getVisibility()==View.GONE) {

                    relLayout.setVisibility(View.VISIBLE);
                    try {

                        libs.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text), getResources().getDrawable(R.drawable.edit_text_focus), editEmail);

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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                libs.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text), getResources().getDrawable(R.drawable.edit_text_focus),editPassword);
            }
        });

        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                libs.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text), getResources().getDrawable(R.drawable.edit_text_focus),editPassword);
            }
        });*/

        editEmail.setFocusable(false);
    }

    public void goToWho(View v){

        Intent selection = new Intent(LoginActivity.this, CareSelectionActivity.class);
        startActivity(selection);
    }

    public void validateLogin(View v){

        if(relLayout.getVisibility()==View.VISIBLE) {

            editEmail.setError(null);
            editPassword.setError(null);

            String userName = editEmail.getText().toString();
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
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                Libs.toast(1, 1, getString(R.string.coming_soon));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}
