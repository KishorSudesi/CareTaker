package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public static LinearLayout dynamicUserTab;
    private static TextView emptyTextView;
    private Utils utils;

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
        emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        dynamicUserTab = (LinearLayout) rootView.findViewById(R.id.dynamicUserTab);

        utils = new Utils(getActivity());

        listViewActivities.setEmptyView(emptyTextView);
        utils.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);
    }
}