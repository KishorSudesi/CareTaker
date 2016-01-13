package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hdfc.newzeal.R;

public class DashboardActivityFragment extends Fragment {

    public DashboardActivityFragment() {

    }

    public static DashboardActivityFragment newInstance() {
        DashboardActivityFragment fragment = new DashboardActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard_activity, container, false);

        LinearLayout listActivity1 = (LinearLayout) rootView.findViewById(R.id.activityList1);
        LinearLayout listActivity2 = (LinearLayout) rootView.findViewById(R.id.activityList2);

        listActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBasePackage();
            }
        });

        listActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBasePackage();
            }
        });


        return rootView;
    }

    public void goToBasePackage() {
        Log.e("EEORORO", "Clicked");
        // Create fragment and give it an argument specifying the article it should show
        BasePackageFragment newFragment = BasePackageFragment.newInstance();
        Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, newFragment);
        transaction.addToBackStack(null);
        getFragmentManager().executePendingTransactions();
        transaction.commit();
    }

}
