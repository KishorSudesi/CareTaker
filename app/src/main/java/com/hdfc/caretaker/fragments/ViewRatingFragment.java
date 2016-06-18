package com.hdfc.caretaker.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.RatingCompletedAdapter;
import com.hdfc.caretaker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRatingFragment extends Fragment {

    private static ListView listView;
    private static TextView emptyTextView;

    public ViewRatingFragment() {
        // Required empty public constructor
    }

    public static ViewRatingFragment newInstance() {
        return new ViewRatingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_rating_completed_activity, container, false);
        listView = (ListView) view.findViewById(R.id.listViewRatings);
        emptyTextView = (TextView) view.findViewById(android.R.id.empty);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        RatingCompletedAdapter ratingCompletedAdapter = new RatingCompletedAdapter(getActivity(), ActivityCompletedFragment.feedBackModels);
        listView.setAdapter(ratingCompletedAdapter);
        listView.setEmptyView(emptyTextView);
    }
}
