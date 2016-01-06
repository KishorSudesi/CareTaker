package com.hdfc.newzeal;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hdfc.adapters.DependantViewAdapter;
import com.hdfc.model.DependantModel;

import java.util.ArrayList;

public class AddDependantFragment extends Fragment {

    public static ArrayList<DependantModel> CustomListViewValuesArr = new ArrayList<DependantModel>();
    public ListView list;
    public DependantViewAdapter adapter;

    public AddDependantFragment() {
    }

    public void setListData()
    {
        NewZeal.dbCon.retrieveDependants(SignupActivity.longUserId);
    }

    public static AddDependantFragment newInstance() {
        AddDependantFragment fragment = new AddDependantFragment();
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

        View addFragment = inflater.inflate(R.layout.fragment_add_dependant, container, false);

        list = ( ListView )addFragment.findViewById( R.id.listViewDpndnts);
        return addFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListView();
    }

    public void setListView(){
        setListData();
        Resources res =getResources();
        adapter = new DependantViewAdapter( getContext(), CustomListViewValuesArr, res);
        list.setAdapter( adapter );
    }
}
