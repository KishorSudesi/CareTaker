package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    private ImageButton buttonNotifications;
    private TextView textViewNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        buttonNotifications = (ImageButton) findViewById(R.id.buttonNotifications);
        textViewNotifications = (TextView) findViewById(R.id.textViewNotifications);

        textViewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotifications();
            }
        });

    }

    public void goToNotifications() {
        Intent selection = new Intent(NotificationsActivity.this, NotificationsActivity.class);
        startActivity(selection);
        finish();
    }
}
