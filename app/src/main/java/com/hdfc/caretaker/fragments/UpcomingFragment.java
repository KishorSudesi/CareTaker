package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ProviderModel;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class UpcomingFragment extends Fragment {
    //private static Bitmap bitmapImg;

    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static String strCarlaImageUrl, imageUrl;
    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;
    private ImageView imageViewCarla;
    private String strCarlaImageName, strNo = "";
    private int iPosition;
    private Context mContext;
    private byte START_FROM = 0;
    //    private ImageButton buttonCancel;
    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            //bitmapImg = bitmap;

            progressDialog.dismiss();

            if (bitmap != null)
                imageViewCarla.setImageBitmap(bitmap);
        }
    };

    public static UpcomingFragment newInstance(ActivityModel activityModel, byte statusFrom) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", activityModel);
        args.putByte(Config.KEY_START_FROM, statusFrom);
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
        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonBack);
        mContext = getActivity();
        txtViewHeader = (TextView) view.findViewById(R.id.header);
        txtViewMSG = (TextView) view.findViewById(R.id.textViewMSG);
        txtViewDate = (TextView) view.findViewById(R.id.textViewDate);
        txtViewHead1 = (TextView) view.findViewById(R.id.textViewHead1);
        txtViewHead2 = (TextView) view.findViewById(R.id.textViewHead2);
        imageViewCarla = (ImageView) view.findViewById(R.id.imageViewCarla);
        ImageButton msg = (ImageButton) view.findViewById(R.id.buttonMsg);
        ImageButton call = (ImageButton) view.findViewById(R.id.buttonCallUpcoming);
//        buttonCancel = (ImageButton) view.findViewById(R.id.buttonCancel);

        Utils utils = new Utils(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        final ActivityModel activityModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY");
        if (this.getArguments().containsKey(Config.KEY_START_FROM)) {
            START_FROM = this.getArguments().getByte(Config.KEY_START_FROM, (byte) 0);
        }
        if (activityModel != null) {
            txtViewHead2.setText(activityModel.getStrActivityName());
            if (Config.strProviderIdsAdded.size() > 0) {
                int iPosition = Config.strProviderIdsAdded.indexOf(activityModel.getStrProviderID());
            }
            ProviderModel providerModel = null;
            if (iPosition > -1 && Config.providerModels.size() > 0) {
                providerModel = Config.providerModels.get(iPosition);
            }
            if (providerModel != null) {
                String strHead = providerModel.getStrName() + getActivity().getResources().getString(R.string.will_assist);
                txtViewHead1.setText(strHead);
                String strDate = getActivity().getResources().getString(R.string.at) + utils.formatDate(activityModel.getStrActivityDate());
                txtViewDate.setText(strDate);
                txtViewMSG.setText(activityModel.getStrActivityDesc());

                strCarlaImageName = utils.replaceSpace(activityModel.getStrProviderID());
                strCarlaImageUrl = utils.replaceSpace(activityModel.getStrProviderID());
                imageUrl = providerModel.getStrImgUrl();
            }
        }

        txtViewHeader.setText(getActivity().getResources().getString(R.string.upcoming_activity));

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToList();

            }
        });

//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //utils.toast(2, 2, getString(R.string.coming_soon));
//                goToList();
//            }
//        });

        try {
            iPosition = -1;
            if (activityModel != null && activityModel.getStrProviderID() != null) {
                if (Config.strProviderIds.size() > 0) {
                    iPosition = Config.strProviderIds.indexOf(activityModel.getStrProviderID());
                }
                if (Config.providerModels.size() > 0 && iPosition > -1) {
                    strNo = Config.providerModels.get(iPosition).getStrContacts();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body",
                            activityModel != null ? activityModel.getStrActivityName() : "Activity Name");

                    sendIntent.putExtra("address", activityModel != null ? strNo : "0000000000");
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    String _strNo = "tel:" + String.valueOf(!strNo.equalsIgnoreCase("") ? strNo :
                            "0000000000");
                    callIntent.setData(Uri.parse(_strNo));
                    startActivity(callIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void goToList() {

        if (START_FROM == Config.START_FROM_NOTIFICATION) {
            NotificationFragment fragment = NotificationFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            ActivityFragment fragment = ActivityFragment.newInstance(false);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_dashboard, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

//        threadHandler = new ThreadHandler();
//        Thread backgroundThread = new BackgroundThread();
//        backgroundThread.start();

        loadImageSimpleTarget(imageUrl);

        /*progressDialog.setMessage(getResources().getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
    }

    private void loadImageSimpleTarget(String url) {

        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(mContext))
                .placeholder(R.drawable.person_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(target);
    }

//    public static class ThreadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
////            progressDialog.dismiss();
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
//                if(strCarlaImageName!=null&&!strCarlaImageName.equalsIgnoreCase("")) {
//
//                    File f = utils.getInternalFileImages(strCarlaImageName);
//
//                    if (!f.exists()) {
//                        if (strCarlaImageUrl != null && !strCarlaImageUrl.equalsIgnoreCase(""))
//                            utils.loadImageFromWeb(strCarlaImageName, strCarlaImageUrl);
//                    }
//
//                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intScreenWidth,
//                            Config.intHeight);
//                }
//
//                threadHandler.sendEmptyMessage(0);
//            } catch (Exception | OutOfMemoryError e) {
//                e.printStackTrace();
//            }
//        }
//    }

}



