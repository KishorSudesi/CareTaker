package com.hdfc.caretaker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hdfc.caretaker.PackageBuyActivity;
import com.hdfc.caretaker.R;

public class BasePackageFragment extends Fragment {

    public BasePackageFragment() {
        // Required empty public constructor
    }


    public static BasePackageFragment newInstance() {
        BasePackageFragment fragment = new BasePackageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_base_package, container, false);

        Button btnbuy = (Button) rootView.findViewById(R.id.buttonBuy);

        btnbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), PackageBuyActivity.class);
                getActivity().startActivity(newIntent);
            }
        });

        return rootView;
    }
}
