package com.hdfc.newzeal.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.ConfirmListViewAdapter;
import com.hdfc.config.NewZeal;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.newzeal.AccountSuccessActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<ConfirmViewModel>();
    public static ListView list;
    public static ConfirmListViewAdapter adapter;

    public static Button buttonContinue;

    public ConfirmFragment() {
        // Required empty public constructor
        Log.e("ConfirmFragment", "ConfirmFragment");
    }

    public static ConfirmFragment newInstance() {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.e("ConfirmFragment", "newInstance");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addFragment = inflater.inflate(R.layout.fragment_confirm, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewConfirm);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selection = new Intent(getActivity(), AccountSuccessActivity.class);
                startActivity(selection);
                getActivity().finish();
            }
        });
        Log.e("ConfirmFragment", "onCreateView");
        setListView();
        return addFragment;
    }

    public void setListData() {

        int intCount = 0;

        if (SignupActivity.longUserId > 0)
            intCount = NewZeal.dbCon.retrieveConfirmDependants(SignupActivity.longUserId);

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        //setListView();
        Log.e("ConfirmFragment", "onResume");
    }

    public void setListView() {
        try {
            setListData();
            Resources res = getResources();
            adapter = new ConfirmListViewAdapter(getContext(), CustomListViewValuesArr, res);
            list.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
