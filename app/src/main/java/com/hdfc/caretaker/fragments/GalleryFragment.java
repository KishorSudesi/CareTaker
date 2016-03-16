package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class GalleryFragment extends Fragment {

    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";

    public List<Integer> images = new ArrayList<Integer>();
    @InjectView(R.id.pager)
    public ViewPager _pager;
    ImageView thumbView;
    LinearLayout _thumbnails;
    private GalleryPagerAdapter _adapter;


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
            Glide.with(getContext())
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
}