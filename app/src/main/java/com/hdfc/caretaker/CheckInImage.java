package com.hdfc.caretaker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.adapters.CheckInImageAdapter;
import com.hdfc.models.ImageModelCheck;
import com.hdfc.views.TouchImageView;

import java.util.List;

public class CheckInImage extends AppCompatActivity {

    private TextView tvLabel;
    private Context mContext;
    private String name = "";
    private List<ImageModelCheck> imageModelChecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_image);

        mContext = this;
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
        name = data.getString("name");
        tvLabel.setText(name);
        imageModelChecks = (List<ImageModelCheck>) data.getSerializable("Pass");

        CheckInImageAdapter checkInImageAdapter = new CheckInImageAdapter(CheckInImage.this, imageModelChecks);
        if (gridView != null) {
            gridView.setAdapter(checkInImageAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogForLargeImage(position);
            }
        });

    }


    private void showDialogForLargeImage(int position) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.image_dialog_layout);

        TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
        TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
        TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);

        textViewTitle.setText(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textViewTitle.setTextAppearance(android.R.style.TextAppearance_Small);
        } else {
            textViewTitle.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
        }

        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mOriginal.
                dialog.dismiss();
            }
        });

        try {
            Glide.with(mContext)
                    .load(imageModelChecks.get(position).getStrImageUrl())
                    .asBitmap()
                    // .transform(new CropCircleTransformation(mContext))
                    .placeholder(R.drawable.person_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)

                    .into(mOriginal);
        } catch (OutOfMemoryError oOm) {
            oOm.printStackTrace();
        }
        dialog.setCancelable(true);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
        dialog.show();
    }


}
