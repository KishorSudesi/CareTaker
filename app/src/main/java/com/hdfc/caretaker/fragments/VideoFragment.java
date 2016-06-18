package com.hdfc.caretaker.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.VideoCompletedAdapter;
import com.hdfc.caretaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    ListView listView;
    TextView emptyTextView;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_completed_activity, container, false);
        // Inflate the layout for this fragment

        listView = (ListView) view.findViewById(R.id.listVideo);

        emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        VideoCompletedAdapter activityCompletedAdapter = new VideoCompletedAdapter(getActivity(), ActivityCompletedFragment._activityModel.getVideoModels());
        listView.setAdapter(activityCompletedAdapter);
        listView.setEmptyView(emptyTextView);

        return view;
    }
}
