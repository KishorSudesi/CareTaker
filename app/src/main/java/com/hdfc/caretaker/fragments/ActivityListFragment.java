package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ActivityListAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.models.ActivityModel;


public class ActivityListFragment extends Fragment {

    public static ListView listView;
    public static ActivityListAdapter activityListAdapter;

    public static ActivityListFragment newInstance() {
        ActivityListFragment fragment = new ActivityListFragment();
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
        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        activityListAdapter = new ActivityListAdapter(getActivity(),
                ActivityFragment.activitiesModelArrayList);
        listView.setAdapter(activityListAdapter);
        listView.setEmptyView(emptyTextView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ActivityModel activityModel = ActivityFragment.activitiesModelArrayList.
                        get(position);

                if (activityModel.getStrActivityStatus().equalsIgnoreCase("upcoming")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                    ActivityCompletedFragment completedFragment =
                            ActivityCompletedFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();
                }
            }
        });

        return view;
    }
}

