package com.hdfc.caretaker.fragments;

import android.content.Context;
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

import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.caretaker.AddNewActivityActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the *
 * to handle interaction events.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment implements View.OnClickListener {

    private static final String dateTemplate = "MMMM yyyy";
    public static int month, year;
    public static List<ActivityModel> activitiesModelArrayList = new ArrayList<>();
    private static TextView currentMonth;
    private static Context _context;
    private static Calendar calendar;
    private Button buttonActivity;
    private Utils utils;
    private LinearLayout dynamicUserTab;
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
    public static ActivityFragment newInstance() {
        return new ActivityFragment();
    }

    /**
     * @param month
     * @param year
     */
    public static void setGridCellAdapterToDate(int month, int year) {

        Utils.log(String.valueOf(month + "-" + year), " DATE ");

        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));

        currentMonth.setText(DateFormat.format(dateTemplate, calendar.getTime()));

        if (ActivityMonthFragment.adapter != null) {

            ActivityMonthFragment.adapter = new CalendarAdapter(_context, month, year,
                    ActivityFragment.activitiesModelArrayList);
            ActivityMonthFragment.calendarView.setAdapter(ActivityMonthFragment.adapter);
            ActivityMonthFragment.adapter.notifyDataSetChanged();

            //ActivityMonthFragment.activitiesModelSelected.clear();
            ActivityMonthFragment.activityListAdapter.notifyDataSetChanged();
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

        dynamicUserTab = (LinearLayout) view.findViewById(R.id.dynamicUserTab);

        _context = getActivity();

        calendar = Calendar.getInstance(Locale.getDefault());
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        TextView selectedDayMonthYearButton = (TextView) view.findViewById(R.id.currentMonth);
        selectedDayMonthYearButton.setText("Selected: ");

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);

        buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActivityView();
            }
        });

        utils = new Utils(getActivity());

        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

        utils.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });

        if(Config.intSelectedMenu==Config.intListActivityScreen){
            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));
            ActivityListFragment fragment1 = ActivityListFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                    beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment1);
            transaction.addToBackStack(null);
            transaction.commit();
        }else {
            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
            ActivityMonthFragment fragment1 = ActivityMonthFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                    beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment1);
            transaction.addToBackStack(null);
            transaction.commit();
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

        utils.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu);
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
                month = 1; year++;
            } else {
                month++;
            }
            setGridCellAdapterToDate(month, year);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
