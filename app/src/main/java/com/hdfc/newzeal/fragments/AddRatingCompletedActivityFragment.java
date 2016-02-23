package com.hdfc.newzeal.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hdfc.newzeal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRatingCompletedActivityFragment extends Fragment {

    EditText editFeedBack;
    Button btnSubmit;
    CheckBox checkReport;
    boolean checked;

    public AddRatingCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {

        return Fragment.instantiate(context, ImagesFragment.class.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_rating_completed_activity, container, false);
        editFeedBack = (EditText) view.findViewById(R.id.editFeedBack);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        checkReport = (CheckBox) view.findViewById(R.id.checkReport);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editFeedBack.getText().toString())) {
                    Toast.makeText(getActivity(), "Your response has been recorded", Toast.LENGTH_LONG).show();
                } else {
                    editFeedBack.setError(getString(R.string.error_field_required));
                }
                checked = checkReport.isChecked();
                //Toast.makeText(getActivity(),"Check box is "+checked,Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

}
