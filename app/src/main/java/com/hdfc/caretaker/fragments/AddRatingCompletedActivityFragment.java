package com.hdfc.caretaker.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.FeedBackModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRatingCompletedActivityFragment extends Fragment {

    private static int iRating = 0;
    private static View previousViewButton;
    private static Context context;
    private static ArrayList<FeedBackModel> _feedBackModels;
    private EditText editFeedBack;
    private CheckBox checkReport;
    private boolean checked = false;
    private Utils utils;
    private ProgressDialog progressDialog;

    public AddRatingCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static AddRatingCompletedActivityFragment newInstance() {

        return new AddRatingCompletedActivityFragment();
    }

    public static void setRating(View v) {

        iRating = Integer.parseInt((String) v.getTag());

        if (previousViewButton != null)
            previousViewButton.setBackgroundDrawable(null);

        previousViewButton = v;

        v.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_cell_blue));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_rating_completed_activity, container, false);
        editFeedBack = (EditText) view.findViewById(R.id.editFeedBack);
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        checkReport = (CheckBox) view.findViewById(R.id.checkReport);

        context = getActivity();

        utils = new Utils(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = checkReport.isChecked();

                boolean b = true;

                if (TextUtils.isEmpty(editFeedBack.getText().toString())) {
                    b = false;
                    editFeedBack.setError(getString(R.string.error_field_required));
                }

                if (iRating == 0) {
                    b = false;
                    utils.toast(2, 2, getString(R.string.select_rating));
                }

                if (b)
                    uploadCheckBox();

            }
        });
        return view;
    }


    public void uploadCheckBox() {

        if (utils.isConnectingToInternet()) {

            progressDialog.setMessage(getString(R.string.uploading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageService storageService = new StorageService(getActivity());

            JSONObject jsonObjectFeedback = new JSONObject();

            try {

                JSONObject jsonObjectFeedbacks;

                Date doneDate = new Date();

                final String strDoneDate = utils.convertDateToString(doneDate);

                JSONArray jsonArrayFeedback = new JSONArray();

                for (FeedBackModel feedBackModel : ActivityCompletedFragment.feedBackModels) {

                    jsonObjectFeedbacks = new JSONObject();

                    jsonObjectFeedbacks.put("feedback_message", feedBackModel.getStrFeedBackMessage());
                    jsonObjectFeedbacks.put("feedback_rating", feedBackModel.getIntFeedBackRating());
                    jsonObjectFeedbacks.put("feedback_by", feedBackModel.getStrFeedBackBy());
                    jsonObjectFeedbacks.put("feedback_report", feedBackModel.getBoolFeedBackReport());
                    jsonObjectFeedbacks.put("feedback_time", feedBackModel.getStrFeedBackTime());
                    jsonObjectFeedbacks.put("feedback_by_type", feedBackModel.getStrFeedBackByType());

                    jsonArrayFeedback.put(jsonObjectFeedbacks);
                }

                JSONObject sJsonObjectFeedback = new JSONObject();

                sJsonObjectFeedback.put("feedback_message", editFeedBack.getText().toString().trim());
                sJsonObjectFeedback.put("feedback_rating", iRating);
                sJsonObjectFeedback.put("feedback_by", Config.customerModel.getStrName());
                sJsonObjectFeedback.put("feedback_report", String.valueOf(checked));
                sJsonObjectFeedback.put("feedback_time", strDoneDate);
                sJsonObjectFeedback.put("feedback_by_type", Config.customerModel.getStrImgUrl());

                jsonArrayFeedback.put(sJsonObjectFeedback);

                jsonObjectFeedback.put("feedbacks", jsonArrayFeedback);

            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.updateDocs(jsonObjectFeedback, ActivityCompletedFragment.strActivityId,
                    Config.collectionActivity, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (o != null) {

                                try {

                                    Storage storage = (Storage) o;

                                    if (storage.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocList = storage.getJsonDocList().get(0);

                                        JSONObject jsonObject = new JSONObject(jsonDocList.getJsonDoc());

                                        if (jsonObject.has("feedbacks")) {

                                            JSONArray jsonArrayFeedback = jsonObject.
                                                    getJSONArray("feedbacks");

                                            ActivityCompletedFragment.feedBackModels.clear();

                                            for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                                JSONObject jsonObjectFeedback =
                                                        jsonArrayFeedback.getJSONObject(k);

                                                if (jsonObjectFeedback.has("feedback_message")) {

                                                    FeedBackModel feedBackModel = new FeedBackModel(
                                                            jsonObjectFeedback.getString("feedback_message"),
                                                            jsonObjectFeedback.getString("feedback_by"),
                                                            jsonObjectFeedback.getInt("feedback_rating"),
                                                            jsonObjectFeedback.getBoolean("feedback_report"),
                                                            jsonObjectFeedback.getString("feedback_time"),
                                                            jsonObjectFeedback.getString("feedback_by_type"));

                                                    ActivityCompletedFragment.feedBackModels.add(feedBackModel);
                                                }
                                            }
                                            populateFeedbacks();
                                        }
                                    } else {
                                        utils.toast(2, 2, getActivity().getString(R.string.error));
                                    }

                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    utils.toast(2, 2, getActivity().getString(R.string.error));
                                }
                            } else {
                                utils.toast(2, 2, getActivity().getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            if (e != null) {
                                utils.toast(2, 2, getActivity().getString(R.string.error));
                            } else {
                                utils.toast(2, 2, getActivity().getString(R.string.warning_internet));
                            }
                        }
                    }
            );


        } else {
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void populateFeedbacks() {

        try {

            ActivityCompletedFragment.setMenuInitView();
            ActivityCompletedFragment.imageButtonRating.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            ViewRatingCompletedActivityFragment newFragment = ViewRatingCompletedActivityFragment.newInstance();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_completed_activity, newFragment);
            transaction.addToBackStack(null);

            transaction.commit();

            utils.toast(2, 2, getActivity().getString(R.string.rating_added));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
