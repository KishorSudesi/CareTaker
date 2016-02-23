package com.hdfc.newzeal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.newzeal.fragments.AddRatingCompletedActivityFragment;
import com.hdfc.newzeal.fragments.CarlaCompletedActivityFragment;
import com.hdfc.newzeal.fragments.ImageCompletedActivityFragment;
import com.hdfc.newzeal.fragments.VideoCompletedActivityFragment;
import com.hdfc.newzeal.fragments.ViewRatingCompletedActivityFragment;

public class ActivityCompletedActivity extends AppCompatActivity {

    private static ImageButton imageButtonDesc, imageButtonVideo, imageButtonImage, imageButtonRating, imageButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_completed);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        TextView txtViewHeader = (TextView) findViewById(R.id.header);

        txtViewHeader.setText("Activity Completed");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dashboardIntent = new Intent(ActivityCompletedActivity.this, ActivityListActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        });

        imageButtonDesc = (ImageButton) findViewById(R.id.imageButtonDesc);
        imageButtonVideo = (ImageButton) findViewById(R.id.imageButtonVideo);
        imageButtonImage = (ImageButton) findViewById(R.id.imageButtonImage);
        imageButtonRating = (ImageButton) findViewById(R.id.imageButtonRating);
        imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);

        setMenuInitView();

        imageButtonDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCarlaDescription();
            }
        });

        imageButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVideo();
            }
        });

        imageButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToImage();
            }
        });

        imageButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewRating();
            }
        });

        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddRating();
            }
        });

        goToCarlaDescription();
    }

    public void setMenuInitView() {

        imageButtonDesc.setBackgroundColor(Color.TRANSPARENT);
        imageButtonVideo.setBackgroundColor(Color.TRANSPARENT);
        imageButtonImage.setBackgroundColor(Color.TRANSPARENT);
        imageButtonRating.setBackgroundColor(Color.TRANSPARENT);
        imageButtonAdd.setBackgroundColor(Color.TRANSPARENT);
    }

    public void goToCarlaDescription() {
        setMenuInitView();
        imageButtonDesc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CarlaCompletedActivityFragment newFragment = new CarlaCompletedActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToVideo() {
        setMenuInitView();
        imageButtonVideo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        VideoCompletedActivityFragment newFragment = new VideoCompletedActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToImage() {
        setMenuInitView();
        imageButtonImage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ImageCompletedActivityFragment newFragment = new ImageCompletedActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToAddRating() {
        setMenuInitView();
        imageButtonAdd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        AddRatingCompletedActivityFragment newFragment = new AddRatingCompletedActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToViewRating() {
        setMenuInitView();
        imageButtonRating.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ViewRatingCompletedActivityFragment newFragment = new ViewRatingCompletedActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
