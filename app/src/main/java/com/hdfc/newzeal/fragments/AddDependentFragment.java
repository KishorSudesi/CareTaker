package com.hdfc.newzeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.DependentViewAdapter;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;

public class AddDependentFragment extends Fragment {

    //public static ArrayList<DependentModel> CustomListViewValuesArr = new ArrayList<DependentModel>();
    public static ListView list;
    public static DependentViewAdapter adapter;
    public static Button buttonContinue;
    private Libs libs;

    public AddDependentFragment() {
    }

    public static AddDependentFragment newInstance() {
        AddDependentFragment fragment = new AddDependentFragment();
        return fragment;
    }

    public void setListData() {

        int intCount = 0;

        Log.e("AddDependentFragment", "setListData");

        if (SignupActivity.strUserId != null && !SignupActivity.strUserId.equalsIgnoreCase(""))
            intCount = libs.retrieveDependants();

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

        View addFragment = inflater.inflate(R.layout.fragment_add_dependent, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewDpndnts);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        libs = new Libs(getActivity());

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity._mViewPager.setCurrentItem(2);
            }
        });
        Log.e("AddDependentFragment", "onCreateView");

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
            adapter = new DependentViewAdapter(getContext(), SignupActivity.dependentModels);
            list.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
