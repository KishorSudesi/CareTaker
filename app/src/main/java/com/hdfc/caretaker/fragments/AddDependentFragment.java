package com.hdfc.caretaker.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.DependentViewAdapter;
import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;

public class AddDependentFragment extends Fragment {

    public static ListView list;
    public static DependentViewAdapter adapter;
    public static Button buttonContinue;
    private Utils utils;

    public AddDependentFragment() {
    }

    public static AddDependentFragment newInstance() {
        return new AddDependentFragment();
    }

    public void setListData() {

        int intCount = 0;

        if (Config.customerModel != null && Config.customerModel.getStrName() != null
                && !Config.customerModel.getStrName().equalsIgnoreCase(""))
            intCount = utils.retrieveDependants();

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addFragment = inflater.inflate(R.layout.fragment_add_dependent, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewDpndnts);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        utils = new Utils(getActivity());

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.add_recipients);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),DependentDetailPersonalActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignupActivity._mViewPager.setCurrentItem(2);
                    }
                });
                alertDialogBuilder.show();
            }
        });

        setListView();

        return addFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
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
