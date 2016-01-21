package com.hdfc.newzeal.fragments;

import android.content.res.Resources;
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
        Log.e("AddDependantFragment", "AddDependantFragment");
    }

    public static AddDependantFragment newInstance() {
        AddDependantFragment fragment = new AddDependantFragment();
        Log.e("AddDependantFragment", "newInstance");
        return fragment;
    }

    public void setListData() {

        int intCount = 0;
        //Log.e("AddDependantFragment", String.valueOf(SignupActivity.longUserId));

        //if(SignupActivity.longUserId>0)
        intCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AddDependantFragment", "onCreate");
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
        Log.e("AddDependantFragment", "onResume");
    }

    public void setListView() {
        try {
            setListData();
            Resources res = getResources();
            adapter = new DependantViewAdapter(getContext(), CustomListViewValuesArr, res);
            list.setAdapter(adapter);
            //list.setEmptyView(R);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
