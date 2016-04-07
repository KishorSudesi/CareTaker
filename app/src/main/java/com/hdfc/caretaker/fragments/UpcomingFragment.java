package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

import java.io.File;


public class UpcomingFragment extends Fragment {
    private static Bitmap bitmap;

    private static Handler threadHandler;
    private static ImageView imageViewCarla;
    private static ProgressDialog progressDialog;
    private static String strCarlaImageUrl;
    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;
    private String strCarlaImageName;
    private Utils utils;

    public static UpcomingFragment newInstance(ActivityModel activityModel) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", activityModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
        txtViewHeader = (TextView) view.findViewById(R.id.header);
        txtViewMSG = (TextView) view.findViewById(R.id.textViewMSG);
        txtViewDate = (TextView) view.findViewById(R.id.textViewDate);
        txtViewHead1 = (TextView) view.findViewById(R.id.textViewHead1);
        txtViewHead2 = (TextView) view.findViewById(R.id.textViewHead2);
        imageViewCarla = (ImageView) view.findViewById(R.id.imageViewCarla);
        ImageButton msg = (ImageButton) view.findViewById(R.id.buttonMsg);
        ImageButton call = (ImageButton) view.findViewById(R.id.buttonCallUpcoming);

        utils = new Utils(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        final ActivityModel activityModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY");

        if (activityModel != null) {
            txtViewHead2.setText(activityModel.getStrActivityName());
            String strHead = activityModel.getStrProviderID() + getActivity().getResources().getString(R.string.will_assist);
            txtViewHead1.setText(strHead);
            String strDate = getActivity().getResources().getString(R.string.at) + activityModel.getStrActivityDate();
            txtViewDate.setText(strDate);
            txtViewMSG.setText(activityModel.getStrActivityDesc());

            strCarlaImageName = utils.replaceSpace(activityModel.getStrProviderID());
            strCarlaImageUrl = utils.replaceSpace(activityModel.getStrProviderID());

            Utils.log(strCarlaImageUrl + " 1 ", " 0 ");
        }

        txtViewHeader.setText(getActivity().getResources().getString(R.string.upcoming_activity));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToList();

            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body",
                        activityModel != null ? activityModel.getStrActivityName() : "Activity Name");
                //sendIntent.putExtra("address",
                // activityModel != null ? activityModel.getStrActivityProviderContactNo() : "0000000000");
                sendIntent.setType("vnd.android-dir/mms-sms");
                //startActivity(sendIntent);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    //String strNo = "tel:" + String.valueOf(activityModel != null ? activityModel.getStrActivityProviderContactNo() : "0000000000");
                    //Utils.log(strNo," call intent ");
                    //callIntent.setData(Uri.parse(strNo));
                    startActivity(callIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void goToList() {
        ActivityFragment fragment = ActivityFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

                if(strCarlaImageName!=null&&!strCarlaImageName.equalsIgnoreCase("")) {

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



