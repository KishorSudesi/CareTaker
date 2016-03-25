package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ActivityMonthListAdapter;
import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ActivityListModel;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ActivityVideoModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.ImageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ActivityMonthFragment extends Fragment {

    public final static SimpleDateFormat readFormat = new SimpleDateFormat("kk:mm aa dd MMM yyyy", Locale.US);
    public final static SimpleDateFormat writeFormatDate = new SimpleDateFormat("dd", Locale.US);
    public final static SimpleDateFormat writeFormatMonth = new SimpleDateFormat("MMMM", Locale.US);
    public final static SimpleDateFormat writeFormatYear = new SimpleDateFormat("yyyy", Locale.US);
    public static List<ActivityListModel> activitiesModelArrayList = new ArrayList<>();
    public static List<ActivityModel> activityModels = new ArrayList<>();
    public static GridView calendarView;
    public static CalendarAdapter adapter=null;
    public static ListView listView;
    public static ActivityMonthListAdapter activityListAdapter;
    public static List<ActivityListModel> activitiesModelSelected = new ArrayList<>();
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

        txtViewDate = (TextView) view.findViewById(R.id.textViewHeader);

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        int month = _calendar.get(Calendar.MONTH) + 1;
        int year = _calendar.get(Calendar.YEAR);

        Libs libs = new Libs(getActivity());

        calendarView = (GridView) view.findViewById(R.id.calendar);

        listView = (ListView) view.findViewById(R.id.listView);

        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);

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

                                activityListModel.setStrDependentName(jsonObject.getString("dependent_name"));

                                activityListModel.setStrActualDate(jsonObjectNotification.getString("activity_date"));

                                activityListModel.setStrPerson(jsonObjectNotification.getString("provider_name"));

                                //activityListModel.setStr(jsonObjectNotification.getString("activity_name"));
                                activityListModel.setStrMessage(jsonObjectNotification.getString("activity_message"));

                                activityListModel.setStrStatus(jsonObjectNotification.getString("status"));
                                activityListModel.setStrDesc(jsonObjectNotification.getString("provider_description"));
                                activityListModel.setStrImageUrl(jsonObjectNotification.getString("provider_image_url"));


                                ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                                ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();
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

                                        ActivityVideoModel activityVideoModel = new ActivityVideoModel(
                                                jsonObjectVideo.getString("video_name"),
                                                jsonObjectVideo.getString("video_url"),
                                                jsonObjectVideo.getString("video_description"),
                                                jsonObjectVideo.getString("video_taken")
                                        );

                                        activityVideoModels.add(activityVideoModel);
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

                                activitiesModelArrayList.add(activityListModel);

                                ActivityModel activityModel = new ActivityModel(
                                        jsonObjectNotification.getString("activity_name"), jsonObjectNotification.getString("activity_message"),
                                        jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("activity_date"),
                                        jsonObjectNotification.getString("status"),
                                        jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("provider_contact_no"),
                                        jsonObjectNotification.getString("provider_name"), jsonObjectNotification.getString("provider_description"),
                                        activityVideoModels, feedBackModels, imageModels);

                                activityModels.add(activityModel);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Initialised
        adapter = new CalendarAdapter(getContext(), month, year, activitiesModelArrayList);
        calendarView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        activityListAdapter = new ActivityMonthListAdapter(getActivity(), activitiesModelSelected);
        listView.setAdapter(activityListAdapter);
        listView.setEmptyView(emptyTextView);

        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                //TODO background color for selected date except today date

                int count = parent.getChildCount();
                View v;

                for (int i = 0; i < count; i++) {
                    if (i != position) {
                        v = parent.getChildAt(i);
                        Button gridcell = (Button) v.findViewById(R.id.calendar_day_gridcell);
                        //gridcell.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
                        //gridcell.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
                    }
                }

                Button gridcell = (Button) view.findViewById(R.id.calendar_day_gridcell);
                //gridcell.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                gridcell.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

                String date_month_year = (String) gridcell.getTag();

                if(position>6) {

                    String[] day_color = date_month_year.split("-");

                    int theday = Integer.parseInt(day_color[0]);
                    String themonth = day_color[1];
                    int theyear = Integer.parseInt(day_color[2]);

                    //
                    String strDate = "Activities on " + theday + "-" + themonth + "-" + theyear;

                    txtViewDate.setText(strDate);
                    //

                    activitiesModelSelected.clear();

                    if (position > 6) {

                        try {
                            for (ActivityListModel activityModel : ActivityMonthFragment.activitiesModelArrayList) {

                                Date date = readFormat.parse(activityModel.getStrDateTime());

                                String strActivityMonth = writeFormatMonth.format(date);
                                int iActivityYear = Integer.parseInt(writeFormatYear.format(date));
                                int iActivityDate = Integer.parseInt(writeFormatDate.format(date));

                                //Libs.log(String.valueOf(iActivityYear + " == " + theyear + " && " + strActivityMonth + " EQS " + themonth + " && " + iActivityDate + " == " + theday), " Compare ");

                                if (iActivityYear == theyear && strActivityMonth.trim().equalsIgnoreCase(themonth) && iActivityDate == theday) {
                                    activitiesModelSelected.add(activityModel);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    activityListAdapter.notifyDataSetChanged();
                }

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ActivityListModel activityListModel = activitiesModelSelected.get(position);

                ActivityModel activityModel;

                if (position <= activityModels.size()) {
                    activityModel = activityModels.get(position);//TODO java.lang.IndexOutOfBoundsException
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
        //

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter=null;
    }
}
