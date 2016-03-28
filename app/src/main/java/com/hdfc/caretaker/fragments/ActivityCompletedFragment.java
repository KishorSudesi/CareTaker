package com.hdfc.caretaker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.ActivityModel;

public class ActivityCompletedFragment extends Fragment {

    public static ImageButton imageButtonDesc, imageButtonVideo, imageButtonImage, imageButtonRating, imageButtonAdd;
    private static ActivityListModel _activityListModel;
    private static ActivityModel _activityModel;

    public static ActivityCompletedFragment newInstance(ActivityListModel activityListModel, ActivityModel activityModels) {
        ActivityCompletedFragment fragment = new ActivityCompletedFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", activityListModel);
        args.putSerializable("ACTIVITY_COMPLETE", activityModels);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setMenuInitView() {

        imageButtonDesc.setBackgroundColor(Color.TRANSPARENT);
        imageButtonVideo.setBackgroundColor(Color.TRANSPARENT);
        imageButtonImage.setBackgroundColor(Color.TRANSPARENT);
        imageButtonRating.setBackgroundColor(Color.TRANSPARENT);
        imageButtonAdd.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_completed, container, false);
        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
        TextView txtViewHeader = (TextView) view.findViewById(R.id.header);

        _activityListModel = (ActivityListModel) this.getArguments().getSerializable("ACTIVITY");
        _activityModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY_COMPLETE");

        txtViewHeader.setText(getActivity().getResources().getString(R.string.completed_activity));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityFragment fragment = ActivityFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_dashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        imageButtonDesc = (ImageButton) view.findViewById(R.id.imageButtonDesc);
        imageButtonVideo = (ImageButton) view.findViewById(R.id.imageButtonVideo);
        imageButtonImage = (ImageButton) view.findViewById(R.id.imageButtonImage);
        imageButtonRating = (ImageButton) view.findViewById(R.id.imageButtonRating);
        imageButtonAdd = (ImageButton) view.findViewById(R.id.imageButtonAdd);

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

        return view;
    }

    public void goToCarlaDescription() {
        setMenuInitView();
        imageButtonDesc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        CarlaCompletedActivityFragment newFragment = CarlaCompletedActivityFragment.newInstance(_activityListModel);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToVideo() {

        // if(_activityModel!=null) {
        setMenuInitView();
        imageButtonVideo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        VideoCompletedActivityFragment newFragment = VideoCompletedActivityFragment.newInstance(_activityModel);
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
        //}
    }

    public void goToImage() {
        setMenuInitView();
        imageButtonImage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        GalleryFragment newFragment = GalleryFragment.newInstance(_activityModel.getImageModels());
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToAddRating() {
        setMenuInitView();
        imageButtonAdd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        AddRatingCompletedActivityFragment newFragment = AddRatingCompletedActivityFragment.newInstance(getActivity(), _activityListModel, _activityModel);
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToViewRating() {
        // if(_activityModel!=null) {
        setMenuInitView();
        imageButtonRating.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ViewRatingCompletedActivityFragment newFragment = ViewRatingCompletedActivityFragment.newInstance(_activityModel.getFeedBackModels());
        //Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        // newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_completed_activity, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
        // }
    }
}
