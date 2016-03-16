package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BasePackageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_package);

        Button backButton = (Button) findViewById(R.id.buttonBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(BasePackageActivity.this, PackageBuyActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void goToAdditionalPacakage(View V) {
        Intent newIntent = new Intent(BasePackageActivity.this, AdditionalServicesActivity.class);
        startActivity(newIntent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
