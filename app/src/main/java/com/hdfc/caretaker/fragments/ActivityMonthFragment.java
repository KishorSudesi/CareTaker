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
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

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
    public static List<ActivityModel> activityModels = new ArrayList<>();
    public static List<ActivityModel> activitiesModelSelected = new ArrayList<>();
    public static GridView calendarView;
    public static CalendarAdapter adapter=null;
    public static ListView listView;
    public static ActivityMonthListAdapter activityListAdapter;
    private static int iSelectedPosition = -1, iSelectedColor = 0;
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

        Utils utils = new Utils(getActivity());

        calendarView = (GridView) view.findViewById(R.id.calendar);

        listView = (ListView) view.findViewById(R.id.listView);

        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        // Initialised
        adapter = new CalendarAdapter(getContext(), month, year, ActivityFragment.activitiesModelArrayList);
        calendarView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        activityListAdapter = new ActivityMonthListAdapter(getActivity(), ActivityFragment.activitiesModelArrayList);
        listView.setAdapter(activityListAdapter);
        listView.setEmptyView(emptyTextView);

        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //int count = parent.getChildCount();
                View v;

               /* for (int i = 0; i < count; i++) {
                    if (i != position) {
                        v = parent.getChildAt(i);
                        Button gridcell = (Button) v.findViewById(R.id.calendar_day_gridcell);
                        //gridcell.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
                        //gridcell.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
                    }
                }*/

                Button gridcell = (Button) view.findViewById(R.id.calendar_day_gridcell);

                String date_month_year = (String) gridcell.getTag();

                if(position>6) {

                    String[] day_color = date_month_year.split("-");

                    int theday = Integer.parseInt(day_color[0]);
                    String themonth = day_color[1];
                    int theyear = Integer.parseInt(day_color[2]);

                    //
                    String strDate = getString(R.string.activities_on) + theday + "-" + themonth
                            + "-" + theyear;

                    if (iSelectedPosition > -1) {
                        v = parent.getChildAt(iSelectedPosition);
                        Button gridcell1 = (Button) v.findViewById(R.id.calendar_day_gridcell);
                        gridcell1.setTextColor(iSelectedColor);
                    }

                    if (iSelectedPosition != position) {
                        iSelectedPosition = position;
                        iSelectedColor = gridcell.getCurrentTextColor();
                    }
                    gridcell.setTextColor(getActivity().getResources().getColor(
                            R.color.colorPrimaryDark));

                    txtViewDate.setText(strDate);

                    activitiesModelSelected.clear();

                    if (position > 6) {

                        try {
                            for (ActivityModel activityModel :
                                    ActivityFragment.activitiesModelArrayList) {

                                Date date = readFormat.parse(activityModel.getStrActivityDate());

                                String strActivityMonth = writeFormatMonth.format(date);
                                int iActivityYear = Integer.parseInt(writeFormatYear.format(date));
                                int iActivityDate = Integer.parseInt(writeFormatDate.format(date));

                                //Utils.log(String.valueOf(iActivityYear + " == " + theyear + " && "
                                // + strActivityMonth + " EQS " + themonth + " && " + iActivityDate
                                // + " == " + theday), " Compare ");

                                if (iActivityYear == theyear &&
                                        strActivityMonth.trim().equalsIgnoreCase(themonth) &&
                                        iActivityDate == theday) {
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

                ActivityModel activityModel;

                if (position <= activityModels.size()) {
                    activityModel = activityModels.get(position);
                } else activityModel = null;

                if (activityModel != null
                        && activityModel.getStrActivityStatus().equalsIgnoreCase("upcoming")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                    ActivityCompletedFragment completedFragment = ActivityCompletedFragment.
                            newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
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
