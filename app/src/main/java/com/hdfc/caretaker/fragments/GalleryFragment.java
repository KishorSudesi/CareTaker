package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.GalleryModel;
import com.hdfc.models.ImageModel;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class GalleryFragment extends Fragment {

    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";
    private Handler backgroundThreadHandler;



    public List<Bitmap> bitmapimages = new ArrayList<>();
    public List<GalleryModel> galleryModelArrayList;

    public ImageView _gallery,imageGallery;
    ImageView thumbView;
    LinearLayout _thumbnails;
    static Bitmap bitmap = null;

    private Libs libs;
    private ProgressDialog progressDialog;


    public GalleryFragment() {
        // Required empty public constructor
    }


    public static GalleryFragment newInstance(ArrayList<ImageModel> imageModels) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
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




        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
       // ButterKnife.inject(getActivity());

        _gallery = (ImageView) view.findViewById(R.id.imageviewThumbnails);
        _thumbnails = (LinearLayout) view.findViewById(R.id.thumbnails);
        imageGallery = (ImageView)view.findViewById(R.id.imageViewGallery);
        libs = new Libs(getActivity());

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        progressDialog = new ProgressDialog(getActivity());
        backgroundThreadHandler = new BackgroundThreadHandler();
        //threadHandler = new ThreadHandler();


        // progressDialog.setMessage(getString(R.string.loading));
        //  progressDialog.setCancelable(false);
        //  progressDialog.show();
        // _images = (ArrayList<String>;


      /*  _adapter = new GalleryPagerAdapter(getActivity(),getChildFragmentManager());
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(6);*/ // how many images to load into memory


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // _adapter = new GalleryPagerAdapter(getActivity());
        // how many images to load into memory

    }

    @Override
    public void onResume() {
        super.onResume();


        // new setAdapterTask().execute();
    }

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //  mProgress.dismiss();

            // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.carla1);


            _gallery = new ImageView(getActivity());

            // if (bitmap != null)
            try {

                for ( int m = 0; m < bitmapimages.size(); m++) {
                  //  Libs.log(" 2 " + String.valueOf(bitmap.getHeight()), " IN ");
                    _thumbnails.removeView(_gallery);
                    _gallery.setPadding(0, 0, 10, 0);
                   //
                    _gallery.setImageBitmap(bitmapimages.get(m));

                    _gallery.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    _thumbnails.addView(_gallery);

                    final int finalM = m;
                    _gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageGallery.setImageBitmap(bitmapimages.get(finalM));
                        }
                    });
                //
                }
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }



            /*for (int i = 0; i < 1; i++) {
                imageView.setId(i);
                imageView.setPadding(0, 0, 10, 0);
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                layout.addView(imageView);
            }

*/
            //System.out.println("helooooooooooooooo"+_gallery);


            // }
        }

    }
        public class BackgroundThread extends Thread {
            @Override
            public void run() {
                //
                try {

                    galleryModelArrayList = new ArrayList<>();
                    galleryModelArrayList.clear();
                    bitmapimages.clear();

                    if (Config.jsonObject != null && Config.jsonObject.has("customer_name")) {

                        if (Config.jsonObject.has("dependents")) {

                            JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            if (jsonObject.has("activities")) {

                                JSONArray jsonArrayNotifications = jsonObject.getJSONArray("activities");


                                for (int i = 0; i < jsonArrayNotifications.length(); i++) {
                                    JSONObject jsonImageObject = jsonArrayNotifications.getJSONObject(i);

                                    if (jsonImageObject.has("images")) {

                                        JSONArray jsonArrayImage = jsonImageObject.getJSONArray("images");

                                        for (int j = 0; j < jsonArrayImage.length(); j++) {

                                            JSONObject jsonObjectimages = jsonArrayImage.getJSONObject(j);


                                            GalleryModel galleryModel = new GalleryModel();
                                            galleryModel.setStrImageName(jsonObjectimages.getString("image_name"));
                                            galleryModel.setStrImageDesc(jsonObjectimages.getString("image_description"));
                                            galleryModel.setStrImageTime(jsonObjectimages.getString("image_taken"));
                                            galleryModel.setStrImageUrl(jsonObjectimages.getString("image_url"));

                                            galleryModelArrayList.add(galleryModel);
                                        }

                                    }
                                }
                            }


                        }
                    }
                    //

                    Libs.log(String.valueOf(galleryModelArrayList.size()), " 1 ");

                    try {

                        for (int i = 0; i < galleryModelArrayList.size(); i++) {
                            GalleryModel galleryModel = galleryModelArrayList.get(i);

                            if (galleryModel.getStrImageUrl() != null && !galleryModel.getStrImageUrl().equalsIgnoreCase("")) {

                                for (int p = 0; p < galleryModelArrayList.size(); p++) {
                                    // System.out.println("XXXXXXXX : "+galleryModelArrayList);
                                    libs.loadImageFromWeb(libs.replaceSpace(galleryModel.getStrImageName()), galleryModel.getStrImageUrl());

                                    File file = libs.getInternalFileImages(libs.replaceSpace(galleryModel.getStrImageName()));

                                    // Bitmap bitmap =  ;
                                    bitmapimages.add(libs.getBitmapFromFile(file.getAbsolutePath(), Config.intWidth, Config.intHeight));

                                    //System.out.println("anikkkkketkkkkkkkkkkkk"+bitmapimages);

                                }
                            }

                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
