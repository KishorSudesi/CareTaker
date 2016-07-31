package com.hdfc.caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.adapters.CheckInImageAdapter;
import com.hdfc.models.ImageModelCheck;

import java.util.List;

public class CheckInImage extends AppCompatActivity {

    private TextView tvLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_image);


        ImageButton button = (ImageButton) findViewById(R.id.buttonBackCheck);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GridView gridView = (GridView) findViewById(R.id.gridImage);
        tvLabel = (TextView) findViewById(R.id.textViewLabel);
        Intent intent = getIntent();

        Bundle data = intent.getExtras();

        tvLabel.setText(data.getString("name"));
        List<ImageModelCheck> imageModelChecks = (List<ImageModelCheck>) data.getSerializable("Pass");

        CheckInImageAdapter checkInImageAdapter = new CheckInImageAdapter(CheckInImage.this, imageModelChecks);
        if (gridView != null) {
            gridView.setAdapter(checkInImageAdapter);
        }

    }
}
