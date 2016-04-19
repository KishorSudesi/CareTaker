package com.hdfc.caretaker.fragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ActivitiesAdapter;
import com.hdfc.adapters.CarouselPagerAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.views.RoundedImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public final static int PAGES = Config.dependentModels.size();
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1;//Config.intDependentsCount;
    //public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static ViewPager pager;
    public static ArrayList<ActivityModel> activitiesModelArrayList = new ArrayList<>();
    public static RoundedImageView roundedImageView;
    private static ListView listViewActivities;
    private static ActivitiesAdapter activitiesAdapter;
    private static TextView textView1, textView2, textView3, textView4, emptyTextView;
    private static Bitmap bitmap;
    private static Handler threadHandler;
    private static int iPosition;
    public CarouselPagerAdapter adapter;
    private Utils utils;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void loadData(int intIndex) {

        Utils.log(String.valueOf(intIndex), " INDEX ");

        try {

            activitiesModelArrayList.clear();
            activitiesModelArrayList = Config.dependentModels.get(intIndex).getActivityModels();
            activitiesAdapter.notifyDataSetChanged();


            Utils.log(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthBp()), " INDEX 0 ");

            textView1.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthBp()));
            textView2.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthHeartRate()));
            textView3.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthBp()));
            textView4.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthHeartRate()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils = new Utils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        listViewActivities = (ListView) rootView.findViewById(R.id.listViewActivities);

        pager = (ViewPager) rootView.findViewById(R.id.dpndntCarousel);

        textView1 = (TextView) rootView.findViewById(R.id.textView1);
        textView2 = (TextView) rootView.findViewById(R.id.textView2);
        textView3 = (TextView) rootView.findViewById(R.id.textView3);
        textView4 = (TextView) rootView.findViewById(R.id.textView4);

        roundedImageView = (RoundedImageView) rootView.findViewById(R.id.roundedImageView);

        if(Config.dependentNames.size()<=1)
            roundedImageView.setVisibility(View.INVISIBLE);

        try {
            if(Config.dependentNames.size()>0) {
                int intPosition = 0;

                if (Config.dependentNames.size() > 1)
                    intPosition = 1;

                iPosition = intPosition;

                threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int intPosition, intReversePosition;

                if (pager.getCurrentItem() == Config.intDependentsCount - 1) {
                    intPosition = 0;
                    intReversePosition = intPosition + 1;
                } else {
                    intPosition = pager.getCurrentItem() + 1;
                    intReversePosition = pager.getCurrentItem();
                }

                try {
                    if (intReversePosition >= Config.dependentNames.size() ||
                            intReversePosition < 0)
                        intReversePosition = 0;

                    iPosition = intReversePosition;

                    threadHandler = new ThreadHandler();
                    Thread backgroundThread = new BackgroundThread();
                    backgroundThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                TranslateAnimation ta = new TranslateAnimation(0, Animation.RELATIVE_TO_SELF, 0, 0);
                ta.setDuration(1000);
                ta.setFillAfter(true);
                roundedImageView.startAnimation(ta);

                pager.setCurrentItem(intPosition, true);
            }
        });
        //

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        activitiesAdapter = new ActivitiesAdapter(getContext(), activitiesModelArrayList);
        listViewActivities.setAdapter(activitiesAdapter);
        loadData(0);

        listViewActivities.setEmptyView(emptyTextView);

        adapter = new CarouselPagerAdapter(getActivity(), getChildFragmentManager());

        new setAdapterTask().execute();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);
        }
    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(adapter);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            pager.setCurrentItem(0);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            pager.setOffscreenPageLimit(Config.intDependentsCount); //1

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            //pager.setPageMargin(-200); //-200
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(iPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);

                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }
}
