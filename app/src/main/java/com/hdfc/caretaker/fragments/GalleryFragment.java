package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.FileModel;
import com.hdfc.models.GalleryModel;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class GalleryFragment extends Fragment {

    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";

    private static Handler threadHandler;
    public List<Integer> images = new ArrayList<Integer>();
    public List<GalleryModel> galleryModelArrayList;
    @InjectView(R.id.pager)
    public ViewPager _pager;
    ImageView thumbView;
    LinearLayout _thumbnails;
    private GalleryPagerAdapter _adapter;
    private Libs libs;
    private ProgressDialog progressDialog;


    public GalleryFragment() {
        // Required empty public constructor
    }


    public static GalleryFragment newInstance(String param1, String param2) {
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
        ButterKnife.inject(getActivity());

        _pager = (ViewPager) view.findViewById(R.id.pager);
        _thumbnails = (LinearLayout) view.findViewById(R.id.thumbnails);
        libs = new Libs(getActivity());

        progressDialog = new ProgressDialog(getActivity());

      //  threadHandler = new ThreadHandler();
      //  Thread backgroundThread = new BackgroundThread();
      //  backgroundThread.start();

      // progressDialog.setMessage(getString(R.string.loading));
      //  progressDialog.setCancelable(false);
      //  progressDialog.show();
        // _images = (ArrayList<String>;
        Assert.assertNotNull(images);

      /*  _adapter = new GalleryPagerAdapter(getActivity(),getChildFragmentManager());
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(6);*/ // how many images to load into memory
        images.add(R.drawable.ic_launcher);
        images.add(R.drawable.carla1);
        images.add(R.drawable.carla2);
        images.add(R.drawable.farmer);

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


        _adapter = new GalleryPagerAdapter(getActivity(), getChildFragmentManager());

        new setAdapterTask().execute();
    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            _pager.setAdapter(_adapter);
            //  _pager.setOnPageChangeListener(_adapter);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            // _pager.setCurrentItem(0);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            //  _pager.setOffscreenPageLimit(Config.intDependentsCount); //1

            _pager.setOffscreenPageLimit(6);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            //pager.setPageMargin(-200); //-200
        }
    }


    public class GalleryPagerAdapter extends PagerAdapter {


        Context _context;
        LayoutInflater _inflater;
        private FragmentManager fm;

        public GalleryPagerAdapter(Context context, FragmentManager fm) {
            super();
            this.fm = fm;

            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
            // Get the border size to show around each image
            int borderSize = 5 * 5;

            // Get the size of the actual thumbnail image
            int thumbnailSize = 30 * 10;
            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);
            // imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            thumbView = new ImageView(getContext());
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);

            //  thumbView.setImageDrawable(getActivity().getDrawable(R.drawable.ic_launcher));
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // Set the pager position when thumbnail clicked
                    _pager.setCurrentItem(position);
                }
            });
            _thumbnails.addView(thumbView);


            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(getActivity())
                    .load(images.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                            thumbView.setImageBitmap(bitmap);
                        }
                    });

            return itemView;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }



   /* public class BackgroundThread extends Thread {
        @Override
        public void run() {
                        //
            try {

                galleryModelArrayList= new ArrayList<>();
                galleryModelArrayList.clear();

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
                                       // galleryModel.setStrImageName(jsonObjectimages.getString("image_name"));
                                       // galleryModel.setStrImageDesc(jsonObjectimages.getString("image_description"));
                                       // galleryModel.setStrImageTime(jsonObjectimages.getString("image_taken"));
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
                for (int i = 0; i < galleryModelArrayList.size(); i++) {
                    GalleryModel galleryModel = galleryModelArrayList.get(i);

                    if (galleryModel.getStrImageUrl() != null && !galleryModel.getStrImageUrl().equalsIgnoreCase("")) {

                        for(int p=0 ; p<galleryModelArrayList.size();p++) {
                            System.out.println("XXXXXXXX : "+galleryModelArrayList);
                            libs.loadImageFromWeb(galleryModel.getStrImageName(), galleryModel.getStrImageUrl());

                        }
                    }
                    System.out.println("aniketttttttttttttttttt"+galleryModel.getStrImageName());
                }
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}