package com.hdfc.caretaker.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.caretaker.DependentDetailPersonal;
import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.libs.Utils;


/**
 * Created by Admin on 24-06-2016.
 */
public class AddCareRecipientsFragment extends Fragment {

    Button addrecipient;
    ListView listview;
    private Utils utils;

    public static AddCareRecipientsFragment newInstance() {
        AddCareRecipientsFragment fragment = new AddCareRecipientsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_care_recipients, container, false);

        listview = (ListView)view.findViewById(R.id.addrecipientlistview);
        addrecipient = (Button)view.findViewById(R.id.btnaddrecipient);

        utils = new Utils(getActivity());
        addrecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DependentDetailPersonal.class);
                startActivity(intent);
            }
        });



        return view;
    }
}
