package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.hdfc.caretaker.CompletedActivity;
import com.hdfc.caretaker.R;
import com.hdfc.models.ActivityModel;


public class ActivityListFragment extends Fragment {

    public static ListView listView;
    public static ActivityListAdapter activityListAdapter;
    private static Context context;

    public static ActivityListFragment newInstance() {
        ActivityListFragment fragment = new ActivityListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void reload() {
        activityListAdapter = new ActivityListAdapter(context, ActivityFragment.activitiesModelArrayList);
        listView.setAdapter(activityListAdapter);
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
        listView.setEmptyView(emptyTextView);

        context = getActivity();

        reload();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ActivityModel activityModel = ActivityFragment.activitiesModelArrayList.
                        get(position);

                if (activityModel.getStrActivityStatus().equalsIgnoreCase("new")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                   /* ActivityCompletedFragment completedFragment =
                            ActivityCompletedFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();
*/
                    Intent intent = new Intent(getActivity(), CompletedActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ACTIVITY", activityModel);
                    args.putBoolean("WHICH_SCREEN", true);
                    args.putInt("ACTIVITY_POSITION", position);
                    intent.putExtras(args);
                    startActivity(intent);


                }
            }
        });

        return view;
    }
}

