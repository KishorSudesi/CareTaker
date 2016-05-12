package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    public static List<Bitmap> bitmapimages = new ArrayList<>();
    public static ImageView _gallery, imageGallery;
    public static LinearLayout _thumbnails;
    private static ArrayList<ImageModel> imageModels;
    private static ProgressDialog progressDialog;
    private static Context context;
    private Handler backgroundThreadHandler;
    private Utils utils;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance(ArrayList<ImageModel> _imageModels) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable("IMAGE_MODEL", _imageModels);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmapimages.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        imageModels = (ArrayList<ImageModel>) this.getArguments().getSerializable("IMAGE_MODEL");

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        _thumbnails = (LinearLayout) view.findViewById(R.id.thumbnails);
        imageGallery = (ImageView)view.findViewById(R.id.imageViewGallery);
        utils = new Utils(getActivity());
        context = getActivity();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        backgroundThreadHandler = new BackgroundThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public static class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            try {

                _thumbnails.removeAllViews();

                if (bitmapimages.size() > 0) {

                    for (int m = 0; m < bitmapimages.size(); m++) {

                        _gallery = new ImageView(context);
                        _gallery.setPadding(0, 0, 7, 0);
                        _gallery.setImageBitmap(bitmapimages.get(m));
                        _gallery.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        final int finalM = m;

                        _gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imageGallery.setImageBitmap(bitmapimages.get(finalM));
                            }
                        });

                        _thumbnails.addView(_gallery);
                    }
                } else {
                    TextView textView = new TextView(context);

                  /*  textView.setTextAppearance(context, R.style.MilestoneStyle);
                    textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    textView.setPadding(10, 10, 10, 10);
                    textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_success));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    params.setMargins(10, 10, 10, 10);
                    textView.setLayoutParams(params);*/
                    //
                    textView.setText(context.getString(R.string.no_images));
                    _thumbnails.addView(textView);
                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }

        }

    }
        public class BackgroundThread extends Thread {
            @Override
            public void run() {
                //
                try {

                    bitmapimages.clear();

                    Utils.log(String.valueOf(imageModels.size()), " 1 ");

                    try {

                        for (int i = 0; i < imageModels.size(); i++) {

                            ImageModel imageModel = imageModels.get(i);

                            if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().equalsIgnoreCase("")) {

                                utils.loadImageFromWeb(imageModel.getStrImageName(), imageModel.getStrImageUrl());

                                File file = utils.getInternalFileImages(utils.replaceSpace(imageModel.getStrImageName()));

                                Bitmap bitmap = utils.getBitmapFromFile(file.getAbsolutePath(), Config.intWidth, Config.intHeight);
                                bitmapimages.add(bitmap);
                                bitmap.recycle();
                            }

                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        bitmapimages.clear();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            }
        }
    }
