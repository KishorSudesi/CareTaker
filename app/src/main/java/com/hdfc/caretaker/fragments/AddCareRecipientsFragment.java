package com.hdfc.caretaker.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hdfc.adapters.DependentListViewAdapter;
import com.hdfc.caretaker.DependentDetailPersonal;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;


/**
 * Created by Admin on 24-06-2016.
 */
public class AddCareRecipientsFragment extends Fragment {

    public static DependentListViewAdapter adapter;
    Button addrecipient;
    ListView listview;
    private Utils utils;

    public static AddCareRecipientsFragment newInstance() {
        AddCareRecipientsFragment fragment = new AddCareRecipientsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListData() {

        int intCount = 0;

        if (SignupActivity.dependentModels != null)
            intCount = utils.retrieveDependants();

        if (intCount > 1)
            addrecipient.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_care_recipients, container, false);

        listview = (ListView) view.findViewById(R.id.addrecipientlistview);
        addrecipient = (Button) view.findViewById(R.id.btnaddrecipient);
        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonback);


        utils = new Utils(getActivity());
        addrecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DependentDetailPersonal.class);
                startActivity(intent);
            }
        });


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();

            }
        });
        setListView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setListView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    public void goBack() {
        Config.intSelectedMenu = Config.intAccountScreen;
        MyAccountFragment fragment = MyAccountFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void setListView() {
        try {
            setListData();
            adapter = new DependentListViewAdapter(getContext(), Config.dependentModels);
            listview.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
