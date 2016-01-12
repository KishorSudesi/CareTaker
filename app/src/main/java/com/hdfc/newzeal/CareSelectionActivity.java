package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.libs.Libs;

public class CareSelectionActivity extends AppCompatActivity {

    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_selection);

        libs = new Libs(CareSelectionActivity.this);

        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;

        Log.e("Dimensions", String.valueOf(screenWidth + " X " + screenHeight));

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            imgBg.setImageBitmap(Libs.decodeSampledBitmapFromResource(getResources(), R.drawable.ppl_bg, screenWidth, screenHeight));

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void selectedMyself(View v){
        Libs.toast(1, 1, getString(R.string.coming_soon));
    }

    public void selectedLovedOne(View v){
        Intent selection = new Intent(CareSelectionActivity.this, SignupActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
