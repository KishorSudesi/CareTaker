package com.hdfc.newzeal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ActivityListAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.ActivityFeedBackModel;
import com.hdfc.model.ActivityListModel;
import com.hdfc.model.ActivityModel;
import com.hdfc.model.ActivityVideoModel;
import com.hdfc.newzeal.AddNewActivityActivity;
import com.hdfc.newzeal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityList extends Fragment {

    public static ListView listView;
    public static List<ActivityListModel> activitiesModelArrayList = new ArrayList<ActivityListModel>();
    public static List<ActivityModel> activityModels = new ArrayList<ActivityModel>();
    public static ActivityListAdapter activityListAdapter;
    private static TextView emptyTextView;

    public static ActivityList newInstance() {
        ActivityList fragment = new ActivityList();
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

        LinearLayout dynamicUserTab = (LinearLayout) view.findViewById(R.id.dynamicUserTab);

        Libs libs = new Libs(getActivity());

        Button monthly = (Button) view.findViewById(R.id.buttonMonthly);
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMonth();
            }
        });
        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

        emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), AddNewActivityActivity.class);
                newIntent.putExtra("WHICH_SCREEN", Config.intListActivityScreen);
                startActivity(newIntent);
            }
        });

        /////
        try {

            activitiesModelArrayList.clear();

            if (Config.jsonObject.has("customer_name")) {

                if (Config.jsonObject.has("dependants")) {

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependants");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    //Notifications
                    if (jsonObject.has("activities")) {

                        JSONArray jsonArrayNotifications = jsonObject.getJSONArray("activities");

                        for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                            JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                            ActivityListModel activityListModel = new ActivityListModel();
                            activityListModel.setStrDate(jsonObjectNotification.getString("activity_date").substring(3, 10));
                            activityListModel.setStrDateTime(jsonObjectNotification.getString("activity_date"));
                            activityListModel.setStrPerson(jsonObjectNotification.getString("provider_name"));
                            activityListModel.setStrDateNumber(jsonObjectNotification.getString("activity_date").substring(0, 2));
                            activityListModel.setStrMessage(jsonObjectNotification.getString("activity_name"));
                            activityListModel.setStrStatus(jsonObjectNotification.getString("status"));
                            activityListModel.setStrDesc(jsonObjectNotification.getString("provider_description"));

                            activitiesModelArrayList.add(activityListModel);

                            ArrayList<ActivityFeedBackModel> activityFeedBackModels = new ArrayList<>();
                            ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();

                            if (jsonObjectNotification.has("feedbacks")) {

                                JSONArray jsonArrayFeedback = jsonObjectNotification.getJSONArray("feedbacks");

                                for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                    JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                                    ActivityFeedBackModel activityFeedBackModel = new ActivityFeedBackModel(
                                            jsonObjectFeedback.getString("feedback_message"), jsonObjectFeedback.getString("feedback_by"),
                                            jsonObjectFeedback.getInt("feedback_raring"), 0,
                                            jsonObjectFeedback.getString("feedback_time"),
                                            jsonObjectFeedback.getString("feedback_raring")
                                    );

                                    activityFeedBackModels.add(activityFeedBackModel);

                                }
                            }

                            if (jsonObjectNotification.has("videos")) {

                                JSONArray jsonArrayVideos = jsonObjectNotification.getJSONArray("videos");

                                for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                    JSONObject jsonObjectVideo = jsonArrayVideos.getJSONObject(k);

                                    ActivityVideoModel activityVideoModel = new ActivityVideoModel(
                                            jsonObjectVideo.getString("video_name"),
                                            jsonObjectVideo.getString("video_url"),
                                            jsonObjectVideo.getString("video_description"),
                                            jsonObjectVideo.getString("video_taken")
                                    );

                                    activityVideoModels.add(activityVideoModel);

                                }
                            }

                            ActivityModel activityModel = new ActivityModel(
                                    jsonObjectNotification.getString("activity_name"), jsonObjectNotification.getString("activity_name"),
                                    jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("activity_date"),
                                    jsonObjectNotification.getString("status"),
                                    jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("provider_contact_no"),
                                    jsonObjectNotification.getString("provider_name"), jsonObjectNotification.getString("provider_description"),
                                    activityVideoModels, activityFeedBackModels);

                            activityModels.add(activityModel);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /////


        activityListAdapter = new ActivityListAdapter(getActivity(), activitiesModelArrayList);
        listView.setAdapter(activityListAdapter);
        listView.setEmptyView(emptyTextView);

        libs.populateHeaderDependants(dynamicUserTab, Config.intActivityScreen);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ActivityListModel activityListModel = activitiesModelArrayList.get(position);

                ActivityModel activityModel = null;

                if (position < activityModels.size()) {
                    activityModel = activityModels.get(position);
                } else activityModel = null;

                if (activityListModel.getStrStatus().equalsIgnoreCase("upcoming")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityListModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                    ActivityCompleted completedFragment = ActivityCompleted.newInstance(activityListModel, activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();
                }
            }
        });

        return view;
    }

    public void gotoMonth() {
        ActivityMonthFragment fragment1 = ActivityMonthFragment.newInstance();
        Bundle args = new Bundle();
        fragment1.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment1);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

