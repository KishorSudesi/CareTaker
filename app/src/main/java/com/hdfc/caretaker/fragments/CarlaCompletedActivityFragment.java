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
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

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
    private Utils utils;

    public static CarlaCompletedActivityFragment newInstance(ActivityModel _activityModel) {

        CarlaCompletedActivityFragment fragment = new CarlaCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", _activityModel);
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

        utils = new Utils(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        //
        ActivityModel activityModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY");

        if (activityModel != null) {

            txtViewHead2.setText(activityModel.getStrActivityDesc());
            String strHead = activityModel.getStrProviderID() + getActivity().getResources().getString(R.string.assisted_in);
            txtViewHead1.setText(strHead);
            String strDate = getActivity().getResources().getString(R.string.at) + activityModel.getStrActivityDate();
            txtViewDate.setText(strDate);
            txtViewMSG.setText(activityModel.getStrActivityDesc());

            strCarlaImageName = utils.replaceSpace(activityModel.getStrProviderID());

            strCarlaImageUrl = utils.replaceSpace(activityModel.getStrProviderID());

            Utils.log(strCarlaImageUrl + " 1 ", " 0 ");
        }
        //
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

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

                    File f = utils.getInternalFileImages(strCarlaImageName);

                    if (!f.exists()) {
                        if (strCarlaImageUrl != null && !strCarlaImageUrl.equalsIgnoreCase(""))
                            utils.loadImageFromWeb(strCarlaImageName, strCarlaImageUrl);
                    }

                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intScreenWidth,
                            Config.intHeight);
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

}
