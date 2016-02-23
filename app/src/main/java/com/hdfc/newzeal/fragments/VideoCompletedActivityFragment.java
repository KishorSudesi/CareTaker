package com.hdfc.newzeal.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.VideoCompletedAdapter;
import com.hdfc.model.ActivityModel;
import com.hdfc.model.ActivityVideoModel;
import com.hdfc.model.VideoCompletedModel;
import com.hdfc.newzeal.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCompletedActivityFragment extends Fragment {

    public static ArrayList<VideoCompletedModel> activityCompletedArrayList;
    ListView listView;
    TextView emptyTextView;
    private ActivityModel activityListModel;
    private VideoCompletedAdapter activityCompletedAdapter;

    public VideoCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static VideoCompletedActivityFragment newInstance(ActivityModel _activityModel) {
        VideoCompletedActivityFragment fragment = new VideoCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY_COMPLETE", _activityModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_completed_activity, container, false);
        // Inflate the layout for this fragment

        listView = (ListView) view.findViewById(R.id.listVideo);

        emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        activityListModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY_COMPLETE");

        activityCompletedArrayList = new ArrayList<VideoCompletedModel>();

        activityCompletedArrayList.clear();


        activityCompletedAdapter = new VideoCompletedAdapter(getActivity(), activityCompletedArrayList);
        listView.setAdapter(activityCompletedAdapter);
        listView.setEmptyView(emptyTextView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (activityListModel != null) {

            for (int i = 0; i < activityListModel.getActivityVideoModels().size(); i++) {
                ActivityVideoModel activityFeedBackModel = activityListModel.getActivityVideoModels().get(i);

                VideoCompletedModel activityCompletedModel = new VideoCompletedModel();
                activityCompletedModel.setDateTime(activityFeedBackModel.getStrVideoTime());
                activityCompletedModel.setInformation(activityFeedBackModel.getStrVideoName());
                activityCompletedModel.setImgVid(R.drawable.vidlink);

                activityCompletedArrayList.add(activityCompletedModel);
            }

            activityCompletedAdapter.notifyDataSetChanged();
        }

    }
}
