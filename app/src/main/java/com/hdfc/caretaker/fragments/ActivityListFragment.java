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
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityListFragment extends Fragment {

    public static ListView listView;
    public static List<ActivityListModel> activitiesModelArrayList = new ArrayList<>();
    public static List<ActivityModel> activityModels = new ArrayList<>();
    public static ActivityListAdapter activityListAdapter;
    private Libs libs;

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

        libs = new Libs(getActivity());

        /////
        try {

            activitiesModelArrayList.clear();
            activityModels.clear();

            if (Config.jsonObject != null && Config.jsonObject.has("customer_name")) {

                if (Config.jsonObject.has("dependents")) {

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    //Notifications
                    if (jsonObject.has("activities")) {

                        JSONArray jsonArrayNotifications = jsonObject.getJSONArray("activities");

                        for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                            JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                            if (jsonObjectNotification.has("activity_date")) {

                                ActivityListModel activityListModel = new ActivityListModel();

                                String strDisplayDateTime = libs.formatDate(jsonObjectNotification.getString("activity_date"));
                                String strDisplayDate = libs.formatDateActivity(jsonObjectNotification.getString("activity_date"));
                                String strDisplayDateMonthYear = libs.formatDateActivityMonthYear(jsonObjectNotification.getString("activity_date"));

                                activityListModel.setStrDate(strDisplayDateMonthYear);//month-year
                                activityListModel.setStrDateTime(strDisplayDateTime);
                                activityListModel.setStrDateNumber(strDisplayDate.substring(0, 2));//date

                                activityListModel.setStrActualDate(jsonObjectNotification.getString("activity_date"));

                                activityListModel.setStrDependentName(jsonObject.getString("dependent_name"));

                                activityListModel.setStrPerson(jsonObjectNotification.getString("provider_name"));
                                activityListModel.setStrMessage(jsonObjectNotification.getString("activity_message"));
                                activityListModel.setStrStatus(jsonObjectNotification.getString("status"));
                                activityListModel.setStrDesc(jsonObjectNotification.getString("provider_description"));
                                activityListModel.setStrImageUrl(jsonObjectNotification.getString("provider_image_url"));

                                activitiesModelArrayList.add(activityListModel);

                                ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                                ArrayList<VideoModel> videoModels = new ArrayList<>();
                                ArrayList<ImageModel> imageModels = new ArrayList<>();

                                if (jsonObjectNotification.has("feedbacks")) {

                                    JSONArray jsonArrayFeedback = jsonObjectNotification.getJSONArray("feedbacks");

                                    for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                        JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                                        FeedBackModel feedBackModel = new FeedBackModel(
                                                jsonObjectFeedback.getString("feedback_message"), jsonObjectFeedback.getString("feedback_by"),
                                                jsonObjectFeedback.getInt("feedback_rating"), jsonObjectFeedback.getBoolean("feedback_report"),
                                                jsonObjectFeedback.getString("feedback_time"),
                                                jsonObjectFeedback.getString("feedback_by_url")
                                        );

                                        feedBackModels.add(feedBackModel);

                                    }
                                }

                                if (jsonObjectNotification.has("videos")) {

                                    JSONArray jsonArrayVideos = jsonObjectNotification.getJSONArray("videos");

                                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                        JSONObject jsonObjectVideo = jsonArrayVideos.getJSONObject(k);

                                        VideoModel videoModel = new VideoModel(
                                                jsonObjectVideo.getString("video_name"),
                                                jsonObjectVideo.getString("video_url"),
                                                jsonObjectVideo.getString("video_description"),
                                                jsonObjectVideo.getString("video_taken")
                                        );

                                        videoModels.add(videoModel);

                                    }
                                }

                                if (jsonObjectNotification.has("images")) {

                                    JSONArray jsonArrayVideos = jsonObjectNotification.getJSONArray("images");

                                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                        JSONObject jsonObjectImage = jsonArrayVideos.getJSONObject(k);

                                        ImageModel imageModel = new ImageModel(
                                                jsonObjectImage.getString("image_name"),
                                                jsonObjectImage.getString("image_url"),
                                                jsonObjectImage.getString("image_description"),
                                                jsonObjectImage.getString("image_taken")
                                        );



                                        imageModels.add(imageModel);
                                    }
                                }

                                ActivityModel activityModel = new ActivityModel(
                                        jsonObjectNotification.getString("activity_name"), jsonObjectNotification.getString("activity_message"),
                                        jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("activity_date"),
                                        jsonObjectNotification.getString("status"),
                                        jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("provider_contact_no"),
                                        jsonObjectNotification.getString("provider_name"), jsonObjectNotification.getString("provider_description"),
                                        videoModels, feedBackModels, imageModels);

                                activityModels.add(activityModel);
                            }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ActivityListModel activityListModel = activitiesModelArrayList.get(position);

                ActivityModel activityModel;

                if (position <= activityModels.size()) {
                    activityModel = activityModels.get(position);
                } else activityModel = null;

                if (activityListModel.getStrStatus().equalsIgnoreCase("upcoming")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityListModel, activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                    ActivityCompletedFragment completedFragment = ActivityCompletedFragment.newInstance(activityListModel, activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();
                }
            }
        });

        return view;
    }
}

