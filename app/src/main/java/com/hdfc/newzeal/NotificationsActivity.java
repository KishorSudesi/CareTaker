package com.hdfc.newzeal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hdfc.libs.Libs;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Libs libs = new Libs(NotificationsActivity.this);
        libs.dashboarMenuNavigation();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
