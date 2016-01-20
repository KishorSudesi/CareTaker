package com.hdfc.newzeal.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdfc.newzeal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCompletedActivityFragment extends Fragment {


    public VideoCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {

        return Fragment.instantiate(context, ImagesFragment.class.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_completed_activity, container, false);
    }

}
