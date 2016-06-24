package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    public static ListView listViewActivities;
    public static NotificationAdapter notificationAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        listViewActivities = (ListView) rootView.findViewById(R.id.listViewActivity);
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        LinearLayout dynamicUserTab = (LinearLayout) rootView.findViewById(R.id.dynamicUserTab);

        Utils utils = new Utils(getActivity());

        listViewActivities.setEmptyView(emptyTextView);
        utils.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen);

        listViewActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*String strActivityId = Config.dependentModels.get(Config.intSelectedDependent).
                        getNotificationModels().get(position).getStrActivityId();

                if(strActivityId!=null && !strActivityId.equalsIgnoreCase("")) {

                    //Utils.log(String.valueOf(Config.dependentModels.get(Config.intSelectedDependent).getActivityModels().indexOf()), " NOTIFICATION ");
                   *//* Bundle args = new Bundle();
                    //
                    Intent intent = new Intent(getActivity(), FeatureActivity.class);
                    args.putSerializable("ACTIVITY", activityModel);
                    args.putBoolean("WHICH_SCREEN", true);
                    intent.putExtras(args);
                    startActivity(intent);*//*
                }*/
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);
    }
}