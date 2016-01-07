package com.hdfc.newzeal.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<DependantModel> CustomListViewValuesArr = new ArrayList<DependantModel>();
    public ListView list;
    public DependantViewAdapter adapter;

    private Button buttonContinue;

    public ConfirmFragment() {
        // Required empty public constructor
    }

    public static ConfirmFragment newInstance() {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListData() {
        int intCount = NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);

        if (intCount > 0)
            buttonContinue.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addFragment = inflater.inflate(R.layout.fragment_confirm, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewDpndnts);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);
        return addFragment;
    }

}
