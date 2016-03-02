package com.hdfc.newzeal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.AddNewActivityActivity;
import com.hdfc.newzeal.R;

import java.util.Calendar;
import java.util.Locale;


public class ActivityMonthFragment extends Fragment {

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

        Button btnMonthly = (Button) view.findViewById(R.id.buttonList);
        txtViewDate = (TextView) view.findViewById(R.id.activity_header_date);

        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), AddNewActivityActivity.class);
                newIntent.putExtra("WHICH_SCREEN", Config.intActivityScreen);
                startActivity(newIntent);
            }
        });

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToList();
            }
        });

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        day = _calendar.get(Calendar.DATE);

        txtViewDate.setText("Activities on " + day + "-" + Config.months[month - 1] + "-" + year);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        // Initialised
        adapter = new CalendarAdapter(getContext(), month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        LinearLayout dynamicUserTab = (LinearLayout) view.findViewById(R.id.dynamicUserTab);

        Libs libs = new Libs(getActivity());
        libs.populateHeaderDependents(dynamicUserTab, Config.intActivityScreen);

        return view;
    }

    public void goToList() {
        ActivityListFragment fragment = ActivityListFragment.newInstance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
