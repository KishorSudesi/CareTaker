package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
                startActivity(newIntent);
            }
        });
    }

    public void goToBasePacakage(View v) {
        Intent newIntent = new Intent(PackageBuyActivity.this, BasePackageActivity.class);
        startActivity(newIntent);
    }
}
