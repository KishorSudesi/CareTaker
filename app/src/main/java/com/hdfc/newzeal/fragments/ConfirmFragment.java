package com.hdfc.newzeal.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.ConfirmListViewAdapter;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.newzeal.AccountSuccessActivity;
import com.hdfc.newzeal.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<ConfirmViewModel>();
    public ListView list;
    public ConfirmListViewAdapter adapter;

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

        return addFragment;
    }

    public void setListData() {

        CustomListViewValuesArr.clear();

        ConfirmViewModel confirmModel = new ConfirmViewModel();

        confirmModel.setStrName("Mr Gurunathan");
        confirmModel.setStrDesc("");
        confirmModel.setStrImg("1");
        confirmModel.setStrAddress("H.No:34-3243-32, Tamaka Secunderabad");
        confirmModel.setStrEmail("xyzo@abc.com");
        confirmModel.setStrContacts("1234569875");
        CustomListViewValuesArr.add(confirmModel);

        confirmModel = new ConfirmViewModel();
        confirmModel.setStrName("Mr Hungal");
        confirmModel.setStrDesc("70yrs, diabetic with high BP, Loves morning walk with dog.");
        confirmModel.setStrImg("2");
        confirmModel.setStrAddress("H.No:34-3243-32, Tamaka Secunderabad");
        confirmModel.setStrEmail("xyz@abc.com");
        confirmModel.setStrContacts("1234569870");
        CustomListViewValuesArr.add(confirmModel);

        confirmModel = new ConfirmViewModel();
        confirmModel.setStrName("Mrs Hungal");
        confirmModel.setStrDesc("60yrs, diabetic with high BP, Loves morning walk with dog.");
        confirmModel.setStrImg("3");
        confirmModel.setStrAddress("H.No:34-3243-32, Tamaka Secunderabad");
        confirmModel.setStrEmail("xyzz@abc.com");
        confirmModel.setStrContacts("1234569873");
        CustomListViewValuesArr.add(confirmModel);

    }

    @Override
    public void onResume() {
        super.onResume();
        setListView();
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
