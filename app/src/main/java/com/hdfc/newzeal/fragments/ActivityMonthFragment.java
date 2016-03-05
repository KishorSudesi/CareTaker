package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.config.Config;
import com.hdfc.model.ActivityFeedBackModel;
import com.hdfc.model.ActivityListModel;
import com.hdfc.model.ActivityModel;
import com.hdfc.model.ActivityVideoModel;
import com.hdfc.newzeal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ActivityMonthFragment extends Fragment {

    public static List<ActivityListModel> activitiesModelArrayList = new ArrayList<>();
    public static List<ActivityModel> activityModels = new ArrayList<>();
    private GridView calendarView;
    private CalendarAdapter adapter;
    private Calendar _calendar;
    private int month, year, day;
    private TextView txtViewDate;

    public static ActivityMonthFragment newInstance() {
        ActivityMonthFragment fragment = new ActivityMonthFragment();
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
        View view = inflater.inflate(R.layout.fragment_activity_month, container, false);

        txtViewDate = (TextView) view.findViewById(R.id.activity_header_date);

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        day = _calendar.get(Calendar.DATE);

        txtViewDate.setText("Activities on " + day + "-" + Config.months[month - 1] + "-" + year);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        //////////////
        /////
        try {

            activitiesModelArrayList.clear();

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /////
        //////////////


        // Initialised
        adapter = new CalendarAdapter(getContext(), month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        /*calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {


            }
        });*/

        return view;
    }
}
