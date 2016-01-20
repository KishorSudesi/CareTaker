package com.hdfc.newzeal.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdfc.newzeal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageCompletedActivityFragment extends Fragment {


    public ImageCompletedActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_completed_activity, container, false);
    }

}
