package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.model.ActivityListModel;
import com.hdfc.newzeal.R;


public class UpcomingFragment extends Fragment {
    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;

    public static UpcomingFragment newInstance(ActivityListModel _activityListModel) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", _activityListModel);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
        txtViewHeader = (TextView) view.findViewById(R.id.header);
        txtViewMSG = (TextView) view.findViewById(R.id.textViewMSG);
        txtViewDate = (TextView) view.findViewById(R.id.textViewDate);
        txtViewHead1 = (TextView) view.findViewById(R.id.textViewHead1);
        txtViewHead2 = (TextView) view.findViewById(R.id.textViewHead2);

        ActivityListModel activityListModel = (ActivityListModel) this.getArguments().getSerializable("ACTIVITY");

        txtViewHead2.setText(activityListModel.getStrMessage());
        txtViewHead1.setText(activityListModel.getStrPerson() + " will Assist in");
        txtViewDate.setText("at " + activityListModel.getStrDateTime());
        txtViewMSG.setText(activityListModel.getStrDesc());

        txtViewHeader.setText("Upcoming Activity");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToList();

            }
        });
        return view;
    }

    public void goToList() {
        ActivityList fragment = ActivityList.newInstance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}



