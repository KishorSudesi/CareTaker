package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.DependantViewAdapter;
import com.hdfc.config.NewZeal;
import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;

import java.util.ArrayList;

public class AddDependantFragment extends Fragment {

    public static ArrayList<DependantModel> CustomListViewValuesArr = new ArrayList<DependantModel>();
    public static ListView list;
    public static DependantViewAdapter adapter;

    public static Button buttonContinue;

    public AddDependantFragment() {
    }

    public static AddDependantFragment newInstance() {
        AddDependantFragment fragment = new AddDependantFragment();
        return fragment;
    }

    public void setListData() {

        int intCount = 0;

        Log.e("AddDependantFragment", "setListData");

        if (SignupActivity.longUserId > 0)
            intCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addFragment = inflater.inflate(R.layout.fragment_add_dependant, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewDpndnts);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity._mViewPager.setCurrentItem(2);
            }
        });
        Log.e("AddDependantFragment", "onCreateView");

        setListView();

        return addFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setListView() {
        try {
            setListData();
            adapter = new DependantViewAdapter(getContext(), CustomListViewValuesArr);
            list.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
