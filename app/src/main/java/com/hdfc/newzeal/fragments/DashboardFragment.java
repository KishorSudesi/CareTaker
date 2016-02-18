package com.hdfc.newzeal.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hdfc.adapters.ActivitesAdapter;
import com.hdfc.adapters.CarouselPagerAdapter;
import com.hdfc.libs.Libs;
import com.hdfc.model.ActivitiesModel;
import com.hdfc.newzeal.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public final static int PAGES = 3;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static ViewPager pager;
    public static ArrayList<ActivitiesModel> activitiesModelArrayList = new ArrayList<>();
    private static ListView listViewActivities;
    private static ActivitesAdapter activitesAdapter;
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

        adapter = new CarouselPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(adapter);


        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(3); //1

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(-90); //-200
        //

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO get data from notfications

        for (int i = 0; i < 3; i++) {

            ActivitiesModel activitiesModel = new ActivitiesModel("", "Activity " + i, "17-01-2016 14:14:09", "Good " + i, " Carla " + i);
            activitiesModelArrayList.add(activitiesModel);
        }

        activitesAdapter = new ActivitesAdapter(getContext(), activitiesModelArrayList);
        listViewActivities.setAdapter(activitesAdapter);
    }
}
