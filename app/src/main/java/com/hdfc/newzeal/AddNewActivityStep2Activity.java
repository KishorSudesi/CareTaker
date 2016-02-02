package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewActivityStep2Activity extends AppCompatActivity {

    private EditText editTextDate;

    private String selectedDateTime = "";
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.


            SimpleDateFormat fmt = new SimpleDateFormat("DD-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            String strDate = fmt.format(date);
            editTextDate.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_activity_step2);

        Button cancelButton = (Button) findViewById(R.id.buttonBack);
        editTextDate = (EditText) findViewById(R.id.editTextDate);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                //
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AddNewActivityStep2Activity.this, AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void goToActivityList(View v) {
        Intent newIntent = new Intent(AddNewActivityStep2Activity.this, ActivityListActivity.class);
        startActivity(newIntent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
