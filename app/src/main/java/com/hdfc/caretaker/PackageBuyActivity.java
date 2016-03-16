package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hdfc.config.Config;

public class PackageBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_buy);

        Button backButton = (Button) findViewById(R.id.buttonBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(PackageBuyActivity.this, DashboardActivity.class);
                Config.intSelectedMenu=Config.intAccountScreen;
                //newIntent.putExtra("WHICH_SCREEN", Config.intAccountScreen);
                startActivity(newIntent);
            }
        });
    }

    public void goToBasePacakage(View v) {
        Intent newIntent = new Intent(PackageBuyActivity.this, BasePackageActivity.class);
        startActivity(newIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
