package com.hdfc.caretaker.fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarlaCompletedFragment extends Fragment {

    private static Bitmap bitmap;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static ImageView imageViewCarla;
    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;
    private String strCarlaImageName, strCarlaImageUrl;
    private Utils utils;
    private int iPosition;

    public CarlaCompletedFragment() {
        // Required empty public constructor
    }

    public static CarlaCompletedFragment newInstance() {

        return new CarlaCompletedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carla_completed_activity, container, false);
        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
        txtViewHeader = (TextView) view.findViewById(R.id.header);
        txtViewMSG = (TextView) view.findViewById(R.id.textViewMSG);
        txtViewDate = (TextView) view.findViewById(R.id.textViewDate);
        txtViewHead1 = (TextView) view.findViewById(R.id.textViewHead1);
        txtViewHead2 = (TextView) view.findViewById(R.id.textViewHead2);
        imageViewCarla = (ImageView) view.findViewById(R.id.imageViewCarla);

        utils = new Utils(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        //

        if (ActivityCompletedFragment._activityModel != null) {

            iPosition = Config.strProviderIds.indexOf(ActivityCompletedFragment._activityModel.getStrProviderID());

            txtViewHead2.setText(ActivityCompletedFragment._activityModel.getStrActivityName());
            iPosition = Config.strProviderIds.indexOf(ActivityCompletedFragment._activityModel.getStrProviderID());

            txtViewHead2.setText(ActivityCompletedFragment._activityModel.getStrActivityName());
            String strHead = getActivity().getResources().getString(R.string.assisted_by) + Config.providerModels.get(iPosition).getStrName();
            txtViewHead1.setText(strHead);
            String strDate = getActivity().getResources().getString(R.string.at) + utils.formatDate(ActivityCompletedFragment._activityModel.getStrActivityDate());
            txtViewDate.setText(strDate);
            txtViewMSG.setText(ActivityCompletedFragment._activityModel.getStrActivityDesc());

            strCarlaImageName = utils.replaceSpace(ActivityCompletedFragment._activityModel.getStrProviderID());
            strCarlaImageUrl = utils.replaceSpace(Config.providerModels.get(iPosition).getStrImgUrl());

        }
        //
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

//        threadHandler = new ThreadHandler();
//        Thread backgroundThread = new BackgroundThread();
//        backgroundThread.start();
        Glide.with(getActivity())
                .load(strCarlaImageUrl)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewCarla);

        /*progressDialog.setMessage(getResources().getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
    }

//    public static class ThreadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            DashboardActivity.loadingPanel.setVisibility(View.GONE);
//
//            if (bitmap != null)
//                imageViewCarla.setImageBitmap(bitmap);
//        }
//    }
//
//    public class BackgroundThread extends Thread {
//        @Override
//        public void run() {
//            try {
//
//                if (strCarlaImageName != null && !strCarlaImageName.equalsIgnoreCase("")) {
//
//                    File f = utils.getInternalFileImages(strCarlaImageName);
//                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intScreenWidth,
//                            Config.intHeight);
//                }
//                threadHandler.sendEmptyMessage(0);
//            } catch (Exception | OutOfMemoryError e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
