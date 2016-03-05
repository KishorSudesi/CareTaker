package com.hdfc.newzeal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.AddNewActivityActivity;
import com.hdfc.newzeal.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the *
 * to handle interaction events.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment {

    private Button buttonActivity;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivityFragment.
     */
    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        LinearLayout dynamicUserTab = (LinearLayout) view.findViewById(R.id.dynamicUserTab);

        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);

        buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActivityView();
            }
        });

        Libs libs = new Libs(getActivity());

        ImageView addActivity = (ImageView) view.findViewById(R.id.addActivity);

        Config.intSelectedMenu = Config.intActivityScreen;

        libs.populateHeaderDependents(dynamicUserTab, Config.intSelectedMenu);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });


        ActivityMonthFragment fragment1 = ActivityMonthFragment.newInstance();
        Bundle args = new Bundle();
        fragment1.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutActivity, fragment1);
        transaction.addToBackStack(null);
        transaction.commit();

        return view;
    }

    public void toggleActivityView() {

        if (buttonActivity.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.activity_list))) {

            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_month));
            Config.intSelectedMenu = Config.intActivityScreen;
            ActivityMonthFragment fragment = ActivityMonthFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {

            buttonActivity.setText(getActivity().getResources().getString(R.string.activity_list));
            Config.intSelectedMenu = Config.intListActivityScreen;
            ActivityListFragment fragment = ActivityListFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActivity, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
