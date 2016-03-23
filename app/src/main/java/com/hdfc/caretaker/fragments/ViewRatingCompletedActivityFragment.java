package com.hdfc.caretaker.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.RatingCompletedAdapter;
import com.hdfc.caretaker.R;
import com.hdfc.libs.Libs;
import com.hdfc.models.FeedBackModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRatingCompletedActivityFragment extends Fragment {

    private static ArrayList<FeedBackModel> _activityModel;
    private static Libs libs;
    private static ListView listView;
    private static RatingCompletedAdapter ratingCompletedAdapter;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static Context context;
    private static TextView emptyTextView;

    public ViewRatingCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static ViewRatingCompletedActivityFragment newInstance(ArrayList<FeedBackModel> _activityModel) {
        ViewRatingCompletedActivityFragment fragment = new ViewRatingCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY_COMPLETE", _activityModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_rating_completed_activity, container, false);
        listView = (ListView) view.findViewById(R.id.listViewRatings);
        emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        libs = new Libs(getActivity());

        context = getActivity();

        _activityModel = (ArrayList<FeedBackModel>) this.getArguments().getSerializable("ACTIVITY_COMPLETE");
        progressDialog = new ProgressDialog(getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (progressDialog.isShowing())
                progressDialog.dismiss();

            ratingCompletedAdapter = new RatingCompletedAdapter(context, _activityModel);
            listView.setAdapter(ratingCompletedAdapter);
            listView.setEmptyView(emptyTextView);
            //ratingCompletedAdapter.notifyDataSetChanged();
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                if (_activityModel != null) {
                    for (int i = 0; i < _activityModel.size(); i++) {
                        //Libs.log(_activityModel.get(i).getStrFeedBackByUrl(), " URL ");
                        libs.loadImageFromWeb(_activityModel.get(i).getStrFeedBackBy().trim(), _activityModel.get(i).getStrFeedBackByUrl().trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }
}
