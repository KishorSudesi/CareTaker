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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ActivityMonthListAdapter;
import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.caretaker.CompletedActivity;
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

    public final static SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Utils.locale);
    public final static SimpleDateFormat writeFormatDate = new SimpleDateFormat("dd", Locale.US);
    public final static SimpleDateFormat writeFormatMonth = new SimpleDateFormat("MMMM", Locale.US);
    public final static SimpleDateFormat writeFormatYear = new SimpleDateFormat("yyyy", Locale.US);
    public static List<ActivityModel> activitiesModelSelected = new ArrayList<>();
    public static GridView calendarView;
    public static CalendarAdapter adapter = null;
    public static ListView listView;
    public static ActivityMonthListAdapter activityListAdapter;
    private static int iSelectedPosition = -1, iSelectedColor = 0;
    private static Context context;
    private TextView txtViewDate;

    public static ActivityMonthFragment newInstance() {
        ActivityMonthFragment fragment = new ActivityMonthFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void reload() {

        try {

            adapter = new CalendarAdapter(context, ActivityFragment.month, ActivityFragment.year, ActivityFragment.activitiesModelArrayList);

            if (calendarView != null)
                calendarView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();

            /*activityListAdapter = new ActivityMonthListAdapter(context, ActivityFragment.activitiesModelArrayList);
            listView.setAdapter(activityListAdapter);*/

            if (activitiesModelSelected != null)
                activitiesModelSelected.clear();

            if (activityListAdapter != null)
                activityListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }


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

        context = getActivity();

        calendarView = (GridView) view.findViewById(R.id.calendar);

        listView = (ListView) view.findViewById(R.id.listView);

        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        // Initialised
        adapter = new CalendarAdapter(getContext(), month, year, ActivityFragment.activitiesModelArrayList);
        calendarView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        activitiesModelSelected.clear();

        activityListAdapter = new ActivityMonthListAdapter(getActivity(), activitiesModelSelected);
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

                activitiesModelSelected.clear();

                Button gridcell = (Button) view.findViewById(R.id.calendar_day_gridcell);

                String date_month_year = (String) gridcell.getTag();

                if (position > 6) {


                    int theday = 0, theyear = 0;
                    String themonth = "";
                    try {

                        String[] day_color = date_month_year.split("-");

                        theday = Integer.parseInt(day_color[0]);
                        themonth = day_color[1];
                        theyear = Integer.parseInt(day_color[2]);

                        //
                        String strDate = getString(R.string.activities_on) + theday + "-" + themonth
                                + "-" + theyear;


//                        if (iSelectedPosition > -1) {
//                            v = parent.getChildAt(iSelectedPosition);
//                            if (v == null) {
//                                v = view;
//                            }
//                            Button gridcell1 = (Button) v.findViewById(R.id.calendar_day_gridcell);
//                            gridcell1.setTextColor(iSelectedColor);
//                            //Log.i("TAG"," first I selected color:"+iSelectedColor);
//                        }
//
//
//                        if (iSelectedPosition != position) {
//                            iSelectedPosition = position;
//                            iSelectedColor = gridcell.getCurrentTextColor();
//                            //Log.i("TAG","Second I selected color:"+iSelectedColor);
//                        }
//                        gridcell.setTextColor(getActivity().getResources().getColor(
//                                R.color.colorPrimaryDark));
                        adapter.setmCurrentPosition(position);

                        txtViewDate.setText(strDate);

                        activitiesModelSelected.clear();
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (position > 6) {

                        try {
                            for (ActivityModel activityModel :
                                    ActivityFragment.activitiesModelArrayList) {

                                Date date = new Date();

                                Utils utils = new Utils(getActivity());

                                String strTimeStamp = utils.convertDateToString(date);

                                if (activityModel.getStrActivityDate() != null && !activityModel.getStrActivityDate().equalsIgnoreCase(""))
                                    strTimeStamp = activityModel.getStrActivityDate();

                                //todo check string
                                Date fullDate = utils.convertStringToDate(strTimeStamp);
                                //String strFullTimeStamp = utils.convertDateToStringFormat(fullDate, Utils.readFormat);

                                String strActivityMonth = writeFormatMonth.format(fullDate);

                                int iActivityYear = Integer.parseInt(writeFormatYear.format(fullDate));
                                int iActivityDate = Integer.parseInt(writeFormatDate.format(fullDate));

                             /*   Utils.log(String.valueOf(iActivityYear + " == " + theyear + " && "
                                        + strActivityMonth + " EQS " + themonth + " && " + iActivityDate
                                        + " == " + theday), " Compare ");*/

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

                TextView textView = (TextView) view.findViewById(R.id.textViewActivity);

                ActivityModel activityModel = (ActivityModel) textView.getTag();

              /*  if (position <= activityModels.size()) {
                    activityModel = activityModels.get(position);
                } else activityModel = null;*/

                if (activityModel != null
                        && activityModel.getStrActivityStatus().equalsIgnoreCase("new")) {

                    UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();

                } else {
                   /* ActivityCompletedFragment completedFragment = ActivityCompletedFragment.
                            newInstance(activityModel);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().
                            beginTransaction();
                    ft.replace(R.id.fragment_dashboard, completedFragment);
                    ft.commit();
*/

                    //Utils.log(String.valueOf(Config.dependentModels.get(Config.intSelectedDependent).getActivityModels().indexOf(activityModel)), " POS ");
                    Intent intent = new Intent(getActivity(), CompletedActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ACTIVITY", activityModel);
                    args.putBoolean("WHICH_SCREEN", false);
                    //args.putInt("ACTIVITY_POSITION", position);//todo
                    intent.putExtras(args);
                    startActivity(intent);
                }
            }
        });
        //

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }
}
