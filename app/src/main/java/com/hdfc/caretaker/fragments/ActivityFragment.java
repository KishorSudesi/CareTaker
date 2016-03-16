package com.hdfc.caretaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.caretaker.AddNewActivityActivity;
import com.hdfc.caretaker.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the *
 * to handle interaction events.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment implements View.OnClickListener {

    private Button buttonActivity;
    private  Libs libs;
    private LinearLayout dynamicUserTab;

    private ImageView prevMonth;
    private ImageView nextMonth;
    private int month, year;
    private TextView currentMonth;

    //public static int iSelectedDependent=0;

    private Calendar calendar;

    private static final String dateTemplate = "MMMM yyyy";

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

        calendar = Calendar.getInstance(Locale.getDefault());
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        Log.d(" TAG ", "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year);

        TextView selectedDayMonthYearButton = (TextView) view.findViewById(R.id.currentMonth);
        selectedDayMonthYearButton.setText("Selected: ");

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);
        //

        /*TextView textViewHeader = (TextView) view.findViewById(R.id.header);

        String strCurrentPeriod = Config.months[month - 1] + "-" + year;

        textViewHeader.setText(strCurrentPeriod);*/

        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);

        buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActivityView();
            }
        });

        libs = new Libs(getActivity());

        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

        libs.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu);

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
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment1);
            transaction.addToBackStack(null);
            transaction.commit();
        }else {
            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
            ActivityMonthFragment fragment1 = ActivityMonthFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {

            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
            Config.intSelectedMenu = Config.intActivityScreen;

            ActivityMonthFragment fragment = ActivityMonthFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        libs.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu);
    }

    /**
     *
     * @param month
     * @param year
     */
    private void setGridCellAdapterToDate(int month, int year) {

        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));

        currentMonth.setText(DateFormat.format(dateTemplate,calendar.getTime()));

        if(ActivityMonthFragment.adapter!=null) {

            ActivityMonthFragment.adapter = new CalendarAdapter(getContext(), month, year);
            ActivityMonthFragment.adapter.notifyDataSetChanged();
            ActivityMonthFragment.calendarView.setAdapter(ActivityMonthFragment.adapter);

            ActivityMonthFragment.activityListAdapter.notifyDataSetChanged();
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
            Log.d(" 1 ", "Setting Prev Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }

        if (v == nextMonth) {
            if (month > 11) {
                month = 1; year++;
            } else {
                month++;
            }
            Log.d(" 2 ", "Setting Next Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }
    }

}
