package com.hdfc.caretaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.hdfc.adapters.MileStoneAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MileStoneFragment extends Fragment {

    private static ExpandableListView expandableListView;
    private static MileStoneAdapter mileStoneAdapter;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<FieldModel>> listDataChild = new HashMap<>();

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

        mileStoneAdapter = new MileStoneAdapter(getActivity(), listDataChild, listDataHeader);

        expandableListView.setAdapter(mileStoneAdapter);

        refreshAdapter();

        return view;
    }

    public void refreshAdapter() {

        try {

            if (expandableListView != null) {

                listDataHeader.clear();
                listDataChild.clear();

                for (MilestoneModel milestoneModel : ActivityCompletedFragment._activityModel.getMilestoneModels()) {

                    listDataHeader.add(milestoneModel.getStrMilestoneName());
                    listDataChild.put(milestoneModel.getStrMilestoneName(), milestoneModel.getFieldModels());
                }
                mileStoneAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
