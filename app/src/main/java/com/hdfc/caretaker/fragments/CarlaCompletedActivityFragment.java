package com.hdfc.caretaker.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.models.ActivityListModel;
import com.hdfc.caretaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarlaCompletedActivityFragment extends Fragment {

    TextView txtViewHeader, txtViewMSG, txtViewDate, txtViewHead1, txtViewHead2;

    public static CarlaCompletedActivityFragment newInstance(ActivityListModel _activityListModel) {

        CarlaCompletedActivityFragment fragment = new CarlaCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", _activityListModel);
        fragment.setArguments(args);
        return fragment;
    }

    /*public CarlaCompletedActivityFragment() {
        // Required empty public constructor
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carla_completed_activity, container, false);
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

        //txtViewHeader.setText("Activity Completed");


        return view;
    }

}
