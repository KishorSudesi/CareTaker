package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.NotificationModel;
import com.hdfc.newzeal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    public static ViewPager pager;
    public static ArrayList<NotificationModel> staticNotificationModels = new ArrayList<>();
    public static ListView listViewActivities;
    public static NotificationAdapter notificationAdapter;
    public static LinearLayout dynamicUserTab;
    private static TextView emptyTextView;
    private Libs libs;

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

        libs = new Libs(getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

      /*  if(Config.notificationModels.size()>0&&Config.intSelectedDependent<Config.notificationModels.size())
            notificationModels = Config.notificationModels.get(Config.intSelectedDependent);
        else
            notificationModels.clear();*/

        /*if(Config.notificationModels.size()>0)
            staticNotificationModels = Config.notificationModels.get(0);*/

        try {

            staticNotificationModels.clear();

            if (Config.jsonObject.has("customer_name")) {

                if (Config.jsonObject.has("dependents")) {

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    //Notifications
                    if (jsonObject.has("notifications")) {

                        JSONArray jsonArrayNotifications = jsonObject.getJSONArray("notifications");

                        for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                            JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                            NotificationModel notificationModel = new NotificationModel("",
                                    jsonObjectNotification.getString("notification_message"),
                                    jsonObjectNotification.getString("time"),
                                    jsonObjectNotification.getString("author"));

                            staticNotificationModels.add(notificationModel);
                        }
                    }
                    //
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);
        listViewActivities.setAdapter(notificationAdapter);
        listViewActivities.setEmptyView(emptyTextView);

        libs.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen);

    }
}