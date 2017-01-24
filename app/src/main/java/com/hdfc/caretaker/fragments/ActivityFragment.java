package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.AddNewActivityActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the *
 * to handle interaction events.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment implements View.OnClickListener {

    private static final String dateTemplate = "MMMM yyyy";
    public static List<ActivityModel> activitiesModelArrayList = new ArrayList<>();
    static int month, year;
    private static TextView currentMonth;
    private static Calendar calendar;
    private static Button buttonActivity;
    private static Utils utils;
    private static ProgressDialog progressDialog;
    public static String currentDate = "";
    //private static AppCompatActivity appCompatActivity;
    //public static int iSelectedDependent=0;
    private ImageView prevMonth;
    private ImageView nextMonth;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivityFragment.
     */
    public static ActivityFragment newInstance(boolean bReload) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putBoolean("RELOAD", bReload);
        fragment.setArguments(args);
        return fragment;
        //return new ActivityFragment(bReload);
    }

    /**
     * @param month
     * @param year
     */
    public static void setGridCellAdapterToDate(int month, int year) {

        // Utils.log(String.valueOf(month + "-" + year), " DATE ");

        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));

        currentMonth.setText(DateFormat.format(dateTemplate, calendar.getTime()));

        Utils.log(" Called ", " setGridCellAdapterToDate ");

        refreshData(month, year);
    }

    public static void refreshData(int iMonth, int iYear) {
       /* progressDialog.setMessage(_context.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        utils.clearActivityMonthModel();

        utils.fetchLatestActivitiesByMonth(iMonth, iYear, progressDialog);

    }

    public static void reload() {

        if (Config.intSelectedDependent < Config.dependentModels.size())
            activitiesModelArrayList = Config.dependentModels.get(Config.intSelectedDependent).
                    getMonthActivityModel();

        if (Config.intSelectedMenu == Config.intListActivityScreen) {
            ActivityListFragment.reload();
        } else {
            ActivityMonthFragment.reload();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        LinearLayout dynamicUserTab = (LinearLayout) view.findViewById(R.id.dynamicUserTab);
        utils = new Utils(getActivity());

        Date myDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(myDate);
        Date time = calendar.getTime();
//        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS zz");
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = outputFmt.format(time);
        System.out.println("Current Date =>"+currentDate);

        //Context _context = getActivity();

        try {
            //appCompatActivity = (AppCompatActivity) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        calendar = Calendar.getInstance(Locale.getDefault());
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        progressDialog = new ProgressDialog(getActivity());

        /*TextView selectedDayMonthYearButton = (TextView) view.findViewById(R.id.currentMonth);
        selectedDayMonthYearButton.setText(getActivity().getString(R.string.selected_date));*/

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);

        buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActivityView();
            }
        });

        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

       /* final SimpleTooltip simpleTooltip = new SimpleTooltip.Builder(getActivity())
                .anchorView(addActivity)
                .text(getString(R.string.create_activity))
                .gravity(Gravity.TOP)
                .build();
        simpleTooltip.show();
*/
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(simpleTooltip!=null&&simpleTooltip.isShowing())
                    simpleTooltip.dismiss();*/
                Intent newIntent = new Intent(getActivity(), AddNewActivityActivity.class);

                startActivity(newIntent);
            }
        });

        try {

            if (Config.intSelectedMenu == Config.intListActivityScreen) {
                buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));
                ActivityListFragment fragment1 = ActivityListFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                        beginTransaction();
                transaction.replace(R.id.frameLayoutActivity, fragment1);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
                ActivityMonthFragment fragment1 = ActivityMonthFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                        beginTransaction();
                transaction.replace(R.id.frameLayoutActivity, fragment1);
                transaction.addToBackStack(null);
                transaction.commit();

               /* if (ActivityMonthFragment.adapter != null) {

                    ActivityMonthFragment.adapter = new CalendarAdapter(_context, month, year,
                            ActivityFragment.activitiesModelArrayList);
                    ActivityMonthFragment.calendarView.setAdapter(ActivityMonthFragment.adapter);
                    ActivityMonthFragment.adapter.notifyDataSetChanged();

                    //ActivityMonthFragment.activitiesModelSelected.clear();
                    ActivityMonthFragment.activityListAdapter.notifyDataSetChanged();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        utils.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu,currentDate);

        boolean bReload = false;

        if (this.getArguments() != null)
            bReload = this.getArguments().getBoolean("RELOAD", false);


        if (bReload)
            refreshData(month, year);
        else {
            reload();
            /*if (Config.intSelectedMenu == Config.intListActivityScreen) {

            }else{

            }*/
        }


        return view;
    }

    public void toggleActivityView() {

        if (buttonActivity.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.activity_list))) {

            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));
            Config.intSelectedMenu = Config.intListActivityScreen;

            ActivityListFragment fragment = ActivityListFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                    beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {

            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
            Config.intSelectedMenu = Config.intActivityScreen;

            ActivityMonthFragment fragment = ActivityMonthFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                    beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            setGridCellAdapterToDate(month, year);
        }

        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            setGridCellAdapterToDate(month, year);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Utils.log(" Called ", " OnResume ");

    }
}
