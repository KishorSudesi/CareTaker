package com.hdfc.caretaker.fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarlaCompletedActivityFragment extends Fragment {

    private static Bitmap bitmap;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static ImageView imageViewCarla;
    private static String strCarlaImageUrl;
    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;
    private String strCarlaImageName;
    private Libs libs;

    public static CarlaCompletedActivityFragment newInstance(ActivityListModel _activityListModel) {

        CarlaCompletedActivityFragment fragment = new CarlaCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", _activityListModel);
        fragment.setArguments(args);
        return fragment;
    }

    /*public CarlaCompletedActivityFragment() {
        // Required empty public constructor
    }*/

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

        libs = new Libs(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        //
        ActivityListModel activityListModel = (ActivityListModel) this.getArguments().getSerializable("ACTIVITY");

        if (activityListModel != null) {

            txtViewHead2.setText(activityListModel.getStrMessage());
            String strHead = activityListModel.getStrPerson() + getActivity().getResources().getString(R.string.assisted_in);
            txtViewHead1.setText(strHead);
            String strDate = getActivity().getResources().getString(R.string.at) + activityListModel.getStrDateTime();
            txtViewDate.setText(strDate);
            txtViewMSG.setText(activityListModel.getStrDesc());

            strCarlaImageName = libs.replaceSpace(activityListModel.getStrPerson());

            strCarlaImageUrl = libs.replaceSpace(activityListModel.getStrImageUrl());

            Libs.log(strCarlaImageUrl + " 1 ", " 0 ");
        }
        //
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Libs.log(" On Resumse ", " 0 ");

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (bitmap != null)
                imageViewCarla.setImageBitmap(bitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                if (strCarlaImageName != null && !strCarlaImageName.equalsIgnoreCase("")) {

                    File f = libs.getInternalFileImages(strCarlaImageName);

                    if (!f.exists()) {
                        if (strCarlaImageUrl != null && !strCarlaImageUrl.equalsIgnoreCase(""))
                            libs.loadImageFromWeb(strCarlaImageName, strCarlaImageUrl);
                    }

                    bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intScreenWidth, Config.intHeight);
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

}
