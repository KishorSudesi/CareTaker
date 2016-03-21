package com.hdfc.caretaker.fragments;


import android.app.ProgressDialog;
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

import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.models.ActivityListModel;
import com.hdfc.models.ActivityModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRatingCompletedActivityFragment extends Fragment {

    private static int iRating = 0;
    private static View previousViewButton;
    private static Context context;
    private static ActivityListModel _activityListModel;
    private static ActivityModel _activityModel;
    private static JSONObject jsonObject;
    private EditText editFeedBack;
    private CheckBox checkReport;
    private boolean checked = false;
    private JSONObject responseJSONDoc;
    private JSONObject responseJSONDocCarla;
    private Libs libs;
    private ProgressDialog progressDialog;

    public AddRatingCompletedActivityFragment() {
        // Required empty public constructor
    }

    public static AddRatingCompletedActivityFragment newInstance(Context context, ActivityListModel activityListModel, ActivityModel activityModels) {

        AddRatingCompletedActivityFragment fragment = new AddRatingCompletedActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("ACTIVITY", activityListModel);
        args.putSerializable("ACTIVITY_COMPLETE", activityModels);
        fragment.setArguments(args);
        return fragment;
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

        _activityListModel = (ActivityListModel) this.getArguments().getSerializable("ACTIVITY");
        _activityModel = (ActivityModel) this.getArguments().getSerializable("ACTIVITY_COMPLETE");

        Libs.log(_activityModel.toString(), " OBJ ");

        context = getActivity();

        libs = new Libs(getActivity());
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
                    libs.toast(2, 2, getString(R.string.select_rating));
                }

                if (b)
                    uploadCheckBox();

            }
        });
        return view;
    }


    public void uploadCheckBox() {

        if (libs.isConnectingToInternet()) {

            progressDialog.setMessage(getString(R.string.uploading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageService storageService = new StorageService(getActivity());

            try {

                Date doneDate = new Date();

                final String strDoneDate = libs.convertDateToString(doneDate);

                jsonObject = new JSONObject();

                jsonObject.put("feedback_message", editFeedBack.getText().toString().trim());
                jsonObject.put("feedback_rating", iRating);
                jsonObject.put("feedback_by", Config.customerModel.getStrName());
                jsonObject.put("report", String.valueOf(checked));
                jsonObject.put("feedback_time", strDoneDate);
                jsonObject.put("feedback_by_url", Config.customerModel.getStrImgUrl());


            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.findDocsByIdApp42CallBack(Config.jsonDocId, Config.collectionName, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if (o != null) {

                        final Storage findObj = (Storage) o;

                        if (findObj.getJsonDocList().size() > 0) {

                            Storage.JSONDocument jsonDocument = findObj.getJsonDocList().get(0);

                            String strDocument = jsonDocument.getJsonDoc();

                            try {
                                responseJSONDocCarla = new JSONObject(strDocument);

                                if (responseJSONDocCarla.has("dependents")) {

                                    JSONArray dependantsA = responseJSONDocCarla.
                                            getJSONArray("dependents");

                                    for (int i = 0; i < dependantsA.length(); i++) {

                                        JSONObject jsonObjectActivities = dependantsA.
                                                getJSONObject(i);

                                        Libs.log(_activityListModel.getStrDependentName().toString() + " S " + jsonObjectActivities.getString("dependent_name"), " NAME ");

                                        if (jsonObjectActivities.getString("dependent_name").equalsIgnoreCase(_activityListModel.getStrDependentName())) {

                                            Libs.log(" 1 ", " IN ");

                                            if (jsonObjectActivities.has("activities")) {

                                                JSONArray dependantsActivities = jsonObjectActivities.
                                                        getJSONArray("activities");

                                                for (int j = 0; j < dependantsActivities.length(); j++) {

                                                    JSONObject jsonObjectActivity = dependantsActivities.getJSONObject(j);

                                                    Libs.log(jsonObjectActivity.getString("activity_date") + " ~ " +
                                                            jsonObjectActivity.getString("activity_name") +
                                                            " L " +
                                                            jsonObjectActivity.getString("activity_message"), " NAME1 ");

                                                    Libs.log(_activityListModel.getStrActualDate() + " ~ " +
                                                            _activityModel.getStrActivityName() +
                                                            " L " +
                                                            _activityModel.getStrActivityMessage(), " NAME2 ");

                                                    if (jsonObjectActivity.getString("activity_date").equalsIgnoreCase(_activityListModel.getStrActualDate()) &&
                                                            jsonObjectActivity.getString("activity_name").equalsIgnoreCase(_activityModel.getStrActivityName()) &&
                                                            jsonObjectActivity.getString("activity_message").equalsIgnoreCase(_activityModel.getStrActivityMessage())) {

                                                        JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("feedbacks");

                                                        Libs.log(" 2 ", " IN ");

                                                        jsonArrayFeatures.put(jsonObject);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Libs.log(responseJSONDocCarla.toString()," responseJSONDocCarla ");

                            storageService.updateDocs(responseJSONDocCarla, Config.jsonDocId, Config.collectionName, new App42CallBack() {
                                @Override
                                public void onSuccess(Object o) {

                                    if (o != null) {

                                        Config.jsonObject = responseJSONDocCarla;

                                        storageService.findDocsByKeyValue(Config.collectionNameProviders, "provider_email", _activityModel.getStrAtivityProvider(), new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                            @Override
                                            public void onDocumentInserted(Storage response) {
                                            }

                                            @Override
                                            public void onUpdateDocSuccess(Storage response) {
                                            }

                                            @Override
                                            public void onFindDocSuccess(Storage response) {

                                                if (response != null) {

                                                    if (response.getJsonDocList().size() > 0) {
                                                        final String strCarlaJsonId = response.getJsonDocList().get(0).getDocId();

                                                        try {
                                                            responseJSONDoc = new JSONObject(response.getJsonDocList().get(0).getJsonDoc());

                                                            if (responseJSONDoc.has("activities")) {
                                                                JSONArray dependantsA = responseJSONDoc.
                                                                        getJSONArray("activities");

                                                                for (int i = 0; i < dependantsA.length(); i++) {

                                                                    JSONObject jsonObjectActivity = dependantsA.getJSONObject(i);

                                                                    if (jsonObjectActivity.getString("activity_date").equalsIgnoreCase(_activityListModel.getStrActualDate()) &&
                                                                            jsonObjectActivity.getString("activity_name").equalsIgnoreCase(_activityModel.getStrActivityName()) &&
                                                                            jsonObjectActivity.getString("activity_message").equalsIgnoreCase(_activityModel.getStrActivityMessage())) {

                                                                        JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("feedbacks");

                                                                        jsonArrayImages.put(jsonObject);
                                                                    }
                                                                }
                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                        storageService.updateDocs(responseJSONDoc, strCarlaJsonId, Config.collectionNameProviders, new App42CallBack() {
                                                            @Override
                                                            public void onSuccess(Object o) {

                                                                if (o != null) {

                                                                    if (progressDialog.isShowing())
                                                                        progressDialog.dismiss();

                                                                    jsonObject = new JSONObject();
                                                                    iRating = 0;
                                                                    editFeedBack.setText("");

                                                                    if (previousViewButton != null) {
                                                                        previousViewButton.setBackgroundDrawable(null);
                                                                        previousViewButton = null;
                                                                    }

                                                                    libs.toast(2, 2, getString(R.string.rating_added));

                                                                } else {
                                                                    if (progressDialog.isShowing())
                                                                        progressDialog.dismiss();
                                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                                }
                                                            }

                                                            @Override
                                                            public void onException(Exception e) {
                                                                if (progressDialog.isShowing())
                                                                    progressDialog.dismiss();
                                                                if (e != null) {
                                                                    libs.toast(2, 2, e.getMessage());
                                                                } else {
                                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                            }

                                            @Override
                                            public void onInsertionFailed(App42Exception ex) {

                                            }

                                            @Override
                                            public void onFindDocFailed(App42Exception ex) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();

                                                if (ex != null) {
                                                    libs.toast(2, 2, ex.getMessage());
                                                } else {
                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                            }

                                            @Override
                                            public void onUpdateDocFailed(App42Exception ex) {

                                            }
                                        });

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        libs.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (e != null) {
                                        libs.toast(2, 2, e.getMessage());
                                    } else {
                                        libs.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (e != null) {
                        libs.toast(2, 2, e.getMessage());
                    } else {
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });
        } else {
            libs.toast(2, 2, getString(R.string.warning_internet));
        }
    }
}
