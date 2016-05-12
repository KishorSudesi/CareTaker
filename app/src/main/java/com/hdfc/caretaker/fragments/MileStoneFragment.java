package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.hdfc.caretaker.R;


public class MileStoneFragment extends Fragment {

    private static ExpandableListView expandableListView;

    public MileStoneFragment() {
        // Required empty public constructor
    }

    public static MileStoneFragment newInstance() {

        return new MileStoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mile_stone, container, false);

        expandableListView = (ExpandableListView) view.findViewById(R.id.listViewAdditionalServices);

        return view;
    }

}
