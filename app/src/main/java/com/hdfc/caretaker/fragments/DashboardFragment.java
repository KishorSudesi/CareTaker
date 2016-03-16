package com.hdfc.caretaker.fragments;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ActivitiesModel;
import com.hdfc.caretaker.R;
import com.hdfc.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public final static int PAGES = Config.intDependentsCount;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1;//Config.intDependentsCount;
    //public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static ViewPager pager;
    public static ArrayList<ActivitiesModel> activitiesModelArrayList = new ArrayList<>();
    public static RoundedImageView roundedImageView;
    private static ListView listViewActivities;
    private static ActivitiesAdapter activitiesAdapter;
    private static TextView textView1, textView2, textView3, textView4, emptyTextView;
    public CarouselPagerAdapter adapter;
    private Libs libs;


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

        Libs.log(String.valueOf(intIndex), "");

        JSONArray jsonArrayDependant, jsonArrayActivities;
        JSONObject jsonObjectActivity;

        try {

            activitiesModelArrayList.clear();

            if (Config.jsonObject != null) {

                jsonArrayDependant = Config.jsonObject.getJSONArray("dependents");

                String strBp;
                String strHeartRate;
                if (jsonArrayDependant.length() > 0 && intIndex <= jsonArrayDependant.length()) {

                    if (jsonArrayDependant.getJSONObject(intIndex).has("activities")) {

                        strBp = jsonArrayDependant.getJSONObject(intIndex).getString("health_bp");
                        strHeartRate = jsonArrayDependant.getJSONObject(intIndex).getString("health_heart_rate");
                        //String strTime = "";

                    } else {
                        strBp = "0";
                        strHeartRate = "0";
                    }

                   /* if (jsonArrayDependant.getJSONObject(intIndex).has("health_status")) {

                        jsonObjectHealth = jsonArrayDependant.getJSONObject(intIndex).getJSONArray("health_status").getJSONObject(0);

                        strBp = jsonObjectHealth.getString("bp");
                        strHeartRate = jsonObjectHealth.getString("heart_rate");
                        strTime = jsonObjectHealth.getString("time_taken");
                    } else {
                        strBp = "0";
                        strHeartRate = "0";
                    }
*/
                    if (jsonArrayDependant.getJSONObject(intIndex).has("activities")) {

                        jsonArrayActivities = jsonArrayDependant.getJSONObject(intIndex).getJSONArray("activities");

                        if (jsonArrayActivities.length() > 0) {

                            for (int i = 0; i < jsonArrayActivities.length(); i++) {

                                jsonObjectActivity = jsonArrayActivities.getJSONObject(i);

                                if (jsonObjectActivity.has("activity_name")) {

                                    ActivitiesModel activitiesModel = new ActivitiesModel(jsonObjectActivity.getString("provider_image_url"),
                                            jsonObjectActivity.getString("activity_name"),
                                            jsonObjectActivity.getString("activity_date"),
                                            "feedback",//jsonObjectActivity.getString("activity_name"),//feedback
                                            jsonObjectActivity.getString("provider_name"));

                                    activitiesModelArrayList.add(activitiesModel);
                                }
                            }

                        }
                    } else activitiesModelArrayList.clear();

                } else {
                    strBp = "0";
                    strHeartRate = "0";
                    activitiesModelArrayList.clear();
                }

                activitiesAdapter.notifyDataSetChanged();

                textView1.setText(strBp);
                textView2.setText(strHeartRate);
                textView3.setText(strBp);
                textView4.setText(strHeartRate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        libs = new Libs(getActivity());
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

                roundedImageView.setImageBitmap(BitmapFactory.decodeFile(libs.getInternalFileImages(
                        libs.replaceSpace(Config.dependentNames.get(intPosition))).getAbsolutePath()));
            }
        } catch (Exception | OutOfMemoryError e) {
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

                    roundedImageView.setImageBitmap(BitmapFactory.decodeFile(
                            libs.getInternalFileImages(libs.replaceSpace(
                                    Config.dependentNames.get(intReversePosition)))
                                    .getAbsolutePath()));

                } catch (Exception | OutOfMemoryError e) {
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
}
