package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.CheckInCareActivity;
import com.hdfc.caretaker.CompletedActivity;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.NotificationModel;
import com.hdfc.models.VideoModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//import com.shephertz.app42.paas.sdk.android.storage.Storage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnListItemClickListener,
        SlideAndDragListView.OnItemDeleteListener{
    public static SlideAndDragListView listViewActivities;
    public static NotificationAdapter notificationAdapter;
    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    public static LinearLayout dynamicUserTab;
    private static Utils utils;
    public static String currentDate = "";
    private static Handler threadHandler;
    private SessionManager sessionManager;
    private ActivityModel activityModel = null;
    private NotificationModel notificationModel;
    public static String strDocument,strActivityId,strDocID;
    public boolean result = false;

    private Menu mMenu;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        initMenu();

        listViewActivities = (SlideAndDragListView) rootView.findViewById(R.id.listViewActivity);
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        dynamicUserTab = (LinearLayout) rootView.findViewById(R.id.dynamicUserTab);

        utils = new Utils(getActivity());
        sessionManager = new SessionManager(getActivity());

        listViewActivities.setEmptyView(emptyTextView);
        listViewActivities.setMenu(mMenu);
        listViewActivities.setOnSlideListener(this);
        listViewActivities.setOnMenuItemClickListener(this);
        listViewActivities.setOnItemDeleteListener(this);

        Date myDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(myDate);
        Date time = calendar.getTime();
//        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS zz");
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = outputFmt.format(time);
        System.out.println("Current Date =>"+currentDate);

        utils.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen,currentDate);
        //fetchDocumentID();

        listViewActivities.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                ActivityModel activityModel = null;
//
//                if (ActivityFragment.activitiesModelArrayList != null && ActivityFragment.activitiesModelArrayList.size() > 0) {
//
//                    if (Config.intSelectedDependent > -1
//                            && Config.intSelectedDependent < Config.dependentModels.size()) {
//                        for (int j = 0; j < ActivityFragment.activitiesModelArrayList.size(); j++) {
//
//                            if (position > -1 && position < Config.dependentModels.get
//                                    (Config.intSelectedDependent).getNotificationModels().size()) {
//                                if (ActivityFragment.activitiesModelArrayList.get(j).getStrActivityID().
//                                        equalsIgnoreCase(Config.dependentModels.get(Config.intSelectedDependent).getNotificationModels().get(position).getStrActivityId())) {
//                                    if (j < ActivityFragment.activitiesModelArrayList.size()) {
//                                        activityModel = ActivityFragment.activitiesModelArrayList.get(j);
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                } else {
                //2016-08-12T05:36:54.520Z
                if (Config.dependentModels.get(Config.intSelectedDependent).
                        getNotificationModels().get(position).isCheckincare() && Config.checkInCareActivityNames.size() > 0) {
                    Intent intent = new Intent(getActivity(), CheckInCareActivity.class);
                    intent.putExtra(Config.KEY_START_FROM, Config.START_FROM_NOTIFICATION);
                    startActivity(intent);

                } else if (Config.intSelectedDependent > -1 &&
                        Config.intSelectedDependent < Config.dependentModels.size()) {
                    String whereClause = " where " + DbHelper.COLUMN_COLLECTION_NAME + " = '"
                            + Config.collectionActivity + "' AND " + DbHelper.COLUMN_OBJECT_ID
                            + " = '" + Config.dependentModels.get(Config.intSelectedDependent).
                            getNotificationModels().get(position).getStrActivityId() + "'";

                    Cursor cursor = CareTaker.dbCon.fetchFromSelect(DbHelper.strTableNameCollection, whereClause);
                    Cursor cursorMilestone = null;

                    if (cursor != null && cursor.getCount() > 0) {
                        Log.i("TAG", "cursor count:" + cursor.getCount());
                        cursor.moveToFirst();
                        do {

                            try {
                                //String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ? AND " + DbHelper.COLUMN_OBJECT_ID + " =?";
                                // WHERE clause arguments
                                //String selectionArgsMile[] = {Config.collectionMilestones, cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID))};
                                String whereClauseMile = " where " + DbHelper.COLUMN_COLLECTION_NAME + " = '" + Config.collectionMilestones + "' AND " + DbHelper.COLUMN_OBJECT_ID + " = '" + cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)) + "'";

                                //Cursor cursorMilestone = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgsMile, DbHelper.COLUMN_DOC_DATE, null, false, null, null);
                                cursorMilestone = CareTaker.dbCon.fetchFromSelect(DbHelper.strTableNameCollection, whereClauseMile);
                                JSONArray jArray = new JSONArray();
                                int index = 0;
                                if (cursorMilestone != null && cursorMilestone.getCount() > 0) {
                                    cursorMilestone.moveToFirst();
                                    do {

                                        String strDocument = cursorMilestone.getString(cursorMilestone.getColumnIndex(DbHelper.COLUMN_DOCUMENT));
                                        JSONObject jsonObjectActivity = new JSONObject(strDocument);


                                        jArray.put(index, jsonObjectActivity);
                                        index++;
                                    }
                                    while (cursorMilestone.moveToNext());


                                }
                                if (cursorMilestone != null) {
                                    cursorMilestone.close();
                                }
                                activityModel = createActivityModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)), 1, jArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                CareTaker.dbCon.closeCursor(cursorMilestone);
                            }


                        } while (cursor.moveToNext());
                    } else {
                        activityModel = getActivityModel(Config.dependentModels.get(Config.intSelectedDependent).getNotificationModels().get(position).getStrActivityId());
                    }
                }
                // }

                if (activityModel != null) {
                    if (activityModel.getStrActivityStatus().equalsIgnoreCase("new")) {

                        DashboardActivity.updateActivityIconMenu();
                        UpcomingFragment completedFragment = UpcomingFragment.newInstance(activityModel, Config.START_FROM_NOTIFICATION);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().
                                beginTransaction();
                        ft.replace(R.id.fragment_dashboard, completedFragment);
                        ft.commit();

                    } else {
                        DashboardActivity.updateActivityIconMenu();
                        Intent intent = new Intent(getActivity(), CompletedActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ACTIVITY", activityModel);
                        args.putBoolean("WHICH_SCREEN", true);
                        args.putInt("ACTIVITY_POSITION", position);
                        args.putByte(Config.KEY_START_FROM, Config.START_FROM_NOTIFICATION);
                        intent.putExtras(args);
                        startActivity(intent);
                    }
                } /*else {

                }*/

            }

        });

        return rootView;
    }


    public static void refreshNotification() {
        try {
            utils.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen,currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ActivityModel getActivityModel(String activityId) {
        try {
            activityModel = null;
            // storageService.findDocsByQuery(Config.collectionActivity, q5, //1 for descending
            //new App42CallBack()
            Query q3 = QueryBuilder.build("docId", activityId, QueryBuilder.Operator.EQUALS);
            StorageService storageService = new StorageService(getActivity());

            storageService.findDocsByQuery(Config.collectionActivity, q3, new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();
    */
                            Storage response = (Storage) o;

                            if (response != null) {


                                if (response.getJsonDocList().size() > 0) {
                                    try {
                                        for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                            Storage.JSONDocument jsonDocument = response.
                                                    getJsonDocList().get(i);

                                             strDocument = jsonDocument.getJsonDoc();
                                             strActivityId = jsonDocument.getDocId();
                                            JSONObject jsonObjectActivity =
                                                    new JSONObject(strDocument);
                                            JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                            jsonObjectActivity.remove("milestones");
                                            strDocument = jsonObjectActivity.toString();
                                            String values[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionActivity, "", "1", jsonObjectActivity.optString("activity_date")};
                                            Log.i("TAG", "Date :" + jsonObjectActivity.optString("activity_date"));
                                            activityModel = createActivityModel(strDocument, strActivityId, 1, jArray);

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }

                            }/* else {

                            }*/

                        }

                        @Override
                        public void onException(Exception e) {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/

                            activityModel = null;
                        }
                    }
            );
        } catch (Exception e) {
            activityModel = null;
        }
        return activityModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        //notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);
    }

    public ActivityModel createActivityModel(String strActivityId, String strDocument, int iFlag, JSONArray jArray) {

        ActivityModel activityModel = null;
        try {

            JSONObject jsonObjectActivity =
                    new JSONObject(strDocument);


            if (jsonObjectActivity.has("activity_name")) {

                activityModel = new ActivityModel();

                activityModel.setStrActivityName(jsonObjectActivity.optString("activity_name"));
                activityModel.setStrActivityID(strActivityId);
                activityModel.setStrProviderID(jsonObjectActivity.optString("provider_id"));
                activityModel.setStrDependentID(jsonObjectActivity.optString("dependent_id"));
                activityModel.setStrustomerID(jsonObjectActivity.optString("customer_id"));
                activityModel.setStrActivityStatus(jsonObjectActivity.optString("status"));
                activityModel.setStrActivityDesc(jsonObjectActivity.optString("activity_desc"));
              /*  activityModel.setStrActivityMessage(jsonObjectActivity.
                        optString("activity_message"));*/

                activityModel.setStrCreatedBy(jsonObjectActivity.optString("created_by"));

                if (!Config.strProviderIds.contains(jsonObjectActivity.optString("provider_id"))) {
                    Config.strProviderIds.add(jsonObjectActivity.optString("provider_id"));
                    //sessionManager.saveProvidersIds(Config.strProviderIds);
                }

                activityModel.setStrServcieID(jsonObjectActivity.optString("service_id"));
                activityModel.setStrServiceName(jsonObjectActivity.optString("service_name"));
               /* activityModel.setStrServiceDesc(jsonObjectActivity.optString("service_desc"));*/

                if (jsonObjectActivity.has("activity_date"))
                    activityModel.setStrActivityDate(jsonObjectActivity.optString("activity_date"));
                else
                    activityModel.setStrActivityDate("");

                if (jsonObjectActivity.has("activity_done_date"))
                    activityModel.setStrActivityDoneDate(jsonObjectActivity.
                            optString("activity_done_date"));

                activityModel.setbActivityOverdue(jsonObjectActivity.optBoolean("overdue"));

                activityModel.setStrActivityProviderStatus(jsonObjectActivity.
                        optString("provider_status"));

                activityModel.setStrActivityProviderMessage(jsonObjectActivity.
                        optString("provider_message"));

                ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                ArrayList<VideoModel> videoModels = new ArrayList<>();
                ArrayList<ImageModel> imageModels = new ArrayList<>();

                if (jsonObjectActivity.has("feedbacks")) {

                    JSONArray jsonArrayFeedback = jsonObjectActivity.
                            optJSONArray("feedbacks");

                    for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                        JSONObject jsonObjectFeedback =
                                jsonArrayFeedback.optJSONObject(k);

                        if (jsonObjectFeedback.has("feedback_message")) {

                            FeedBackModel feedBackModel = new FeedBackModel(
                                    jsonObjectFeedback.optString("feedback_message"),
                                    jsonObjectFeedback.optString("feedback_by"),
                                    jsonObjectFeedback.optInt("feedback_rating"),
                                    false, //jsonObjectFeedback.optBoolean("feedback_report")
                                    jsonObjectFeedback.optString("feedback_time"),
                                    jsonObjectFeedback.optString("feedback_by_type"));

                            feedBackModels.add(feedBackModel);

                            if (feedBackModel.getStrFeedBackByType().equalsIgnoreCase("customer") &&
                                    feedBackModel.getStrFeedBackBy().equalsIgnoreCase(activityModel.getStrustomerID())) {
                                activityModel.setRatingAddedByCutsm(true);
                            }
                        }
                    }
                    activityModel.setFeedBackModels(feedBackModels);
                }

               /* if (jsonObjectActivity.has("videos")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            optJSONArray("videos");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectVideo = jsonArrayVideos.
                                optJSONObject(k);

                        if (jsonObjectVideo.has("video_name")) {

                            VideoModel videoModel = new VideoModel(
                                    jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"),
                                    jsonObjectVideo.optString("video_description"),
                                    jsonObjectVideo.optString("video_taken"));

                            Config.fileModels.add(new FileModel(jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"), "VIDEO"));

                            videoModels.add(videoModel);
                        }
                    }
                    activityModel.setVideoModels(videoModels);
                }*/

                if (jsonObjectActivity.has("images")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            optJSONArray("images");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectImage = jsonArrayVideos.
                                optJSONObject(k);

                        if (jsonObjectImage.has("image_name")) {

                            ImageModel imageModel = new ImageModel(
                                    jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"),
                                    jsonObjectImage.optString("image_description"),
                                    jsonObjectImage.optString("image_taken"));

                            Config.fileModels.add(new FileModel(jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"), "IMAGE"));

                            imageModels.add(imageModel);

                        }
                    }
                    activityModel.setImageModels(imageModels);
                }

                //milestones start
                if (jsonObjectActivity.has("milestones") || (jArray != null && jArray.length() > 0)) {
                    JSONArray jsonArrayMilestones;

                    if (jsonObjectActivity.has("milestones")) {
                        jsonArrayMilestones = jsonObjectActivity.
                                optJSONArray("milestones");
                    } else {
                        jsonArrayMilestones = jArray;
                    }


                    for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                        JSONObject jsonObjectMilestone =
                                jsonArrayMilestones.optJSONObject(k);

                        MilestoneModel milestoneModel = new MilestoneModel();

                        milestoneModel.setiMilestoneId(jsonObjectMilestone.optInt("id"));
                        milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.optString("status"));
                        milestoneModel.setStrMilestoneName(jsonObjectMilestone.optString("name"));
                        milestoneModel.setStrMilestoneDate(jsonObjectMilestone.optString("date"));
                        milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));

                        // ArrayList<FileModel> fileModels = new ArrayList<>();

                        if (jsonObjectMilestone.has("files")) {

                            JSONArray jsonArrayMsFiles = jsonObjectMilestone.
                                    optJSONArray("files");

                            for (int m = 0; m < jsonArrayMsFiles.length(); m++) {

                                JSONObject jsonObjectMsFile = jsonArrayMsFiles.
                                        optJSONObject(m);

                                if (jsonObjectMsFile.has("file_name")) {

                                    Utils.log(jsonObjectMsFile.toString(), " JSONOBJECT ");

                                    FileModel fileModel = new FileModel(
                                            jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"),
                                            jsonObjectMsFile.optString("file_type"),
                                            jsonObjectMsFile.optString("file_desc"),
                                            jsonObjectMsFile.optString("file_path"),
                                            jsonObjectMsFile.optString("file_time"));

                                    Config.fileModels.add(new FileModel(jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"), jsonObjectMsFile.optString("file_type")));

                                    milestoneModel.setFileModel(fileModel);
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("show")) {

                            try {
                                milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));
                            } catch (Exception e) {
                                boolean b = true;
                                try {
                                    if (jsonObjectMilestone.optInt("show") == 0)
                                        b = false;
                                    milestoneModel.setVisible(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("reschedule")) {

                            try {
                                milestoneModel.setReschedule(jsonObjectMilestone.optBoolean("reschedule"));
                            } catch (Exception e) {
                                boolean b = true;
                                try {
                                    if (jsonObjectMilestone.optInt("reschedule") == 0)
                                        b = false;
                                    milestoneModel.setReschedule(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("scheduled_date"))
                            milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                    optString("scheduled_date"));

                        if (jsonObjectMilestone.has("fields")) {

                            JSONArray jsonArrayFields = jsonObjectMilestone.
                                    optJSONArray("fields");

                            for (int l = 0; l < jsonArrayFields.length(); l++) {

                                JSONObject jsonObjectField =
                                        jsonArrayFields.optJSONObject(l);

                                FieldModel fieldModel = new FieldModel();

                                fieldModel.setiFieldID(jsonObjectField.optInt("id"));

                                if (jsonObjectField.has("hide"))
                                    fieldModel.setFieldView(jsonObjectField.optBoolean("hide"));

                                fieldModel.setFieldRequired(jsonObjectField.optBoolean("required"));
                                fieldModel.setStrFieldData(jsonObjectField.optString("data"));
                                fieldModel.setStrFieldLabel(jsonObjectField.optString("label"));
                                fieldModel.setStrFieldType(jsonObjectField.optString("type"));

                                if (jsonObjectField.has("values")) {

                                    fieldModel.setStrFieldValues(utils.jsonToStringArray(jsonObjectField.
                                            optJSONArray("values")));
                                }

                                if (jsonObjectField.has("child")) {

                                    fieldModel.setChild(jsonObjectField.optBoolean("child"));

                                    if (jsonObjectField.has("child_type"))
                                        fieldModel.setStrChildType(utils.jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_type")));

                                    if (jsonObjectField.has("child_value"))
                                        fieldModel.setStrChildValue(utils.jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_value")));

                                    if (jsonObjectField.has("child_condition"))
                                        fieldModel.setStrChildCondition(utils.jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_condition")));

                                    if (jsonObjectField.has("child_field"))
                                        fieldModel.setiChildfieldID(utils.jsonToIntArray(jsonObjectField.
                                                optJSONArray("child_field")));
                                }
                                if (jsonObjectField.has("array_fields")) {

                                    try {
                                        fieldModel.setiArrayCount(jsonObjectField.optInt("array_fields"));
                                    } catch (Exception e) {
                                        int i = 0;
                                        try {
                                            i = Integer.parseInt(jsonObjectField.optString("array_fields"));
                                            fieldModel.setiArrayCount(i);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    if (jsonObjectField.has("array_type"))
                                        fieldModel.setStrArrayType(utils.jsonToStringArray(jsonObjectField.
                                                optJSONArray("array_type")));

                                    if (jsonObjectField.has("array_data"))
                                        fieldModel.setStrArrayData(jsonObjectField.optString("array_data"));

                                }
                                ////

                                milestoneModel.setFieldModel(fieldModel);
                            }
                        }
                        activityModel.setMilestoneModel(milestoneModel);
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return activityModel;
    }

    public void loadAllFiles() {
        for (int j = 0; j < activityModel.getMilestoneModels().size(); j++) {
            for (int i = 0; i < activityModel.getMilestoneModels().get(j).getFileModels().size(); i++) {
                FileModel fileModel = activityModel.getMilestoneModels().get(j).getFileModels().get(i);

                if (fileModel != null && fileModel.getStrFileUrl() != null &&
                        !fileModel.getStrFileUrl().equalsIgnoreCase("")) {

                    utils.loadImageFromWeb(fileModel.getStrFileName(),
                            fileModel.getStrFileUrl());
                }
            }
        }
    }


    private void initMenu() {

        mMenu = new Menu(new ColorDrawable(Color.LTGRAY), true);

      /*  //swipe right

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.pen))
                .build());
*/

        //swipe left

      /*  mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.delete))
                .build());
*/
    }

    @Override
    public void onDragViewStart(int position) {

    }

    @Override
    public void onDragViewMoving(int position) {

    }

    @Override
    public void onDragViewDown(int position) {

    }

    @Override
    public void onItemDelete(View view, int position) {

        Log.e("test =>","Working");
    }

    @Override
    public void onListItemClick(View v, int position) {

    }

    @Override
    public void onListItemLongClick(View view, int position) {

    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:

                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:

                /*String strNo = "";

                if (activityModels.get(itemPosition).getStrDependentID() != null &&
                        !activityModels.get(itemPosition).getStrDependentID().equalsIgnoreCase("")) {

                    Cursor cursor1 = CareGiver.getDbCon().fetch(
                            DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                            DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID
                                    + "=?" + " and " + DbHelper.COLUMN_PROVIDER_ID + "=?",
                            new String[]{Config.collectionDependent,
                                    activityModels.get(itemPosition).getStrDependentID(),
                                    activityModels.get(itemPosition).getStrProviderID()
                            },
                            null, "0,1", true, null, null
                    );


                    if (cursor1.getCount() > 0) {
                        cursor1.moveToFirst();
                        try {
                            if (cursor1.getString(0) != null && !cursor1.getString(0).
                                    equalsIgnoreCase("")) {
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(cursor1.getString(0));
                                strNo = jsonObject.optString("dependent_contact_no");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    CareGiver.getDbCon().closeCursor(cursor1);
                }
*/
                switch (buttonPosition) {

                    case 0:
                        strDocID = Config.dependentModels.get(Config.intSelectedDependent).
                                getNotificationModels().get(itemPosition).getStrNotificationID();
                      //  deleteNotification(strDocID);
                        if(result){
//                            utils.NotifyNotification(itemPosition);
                        }
                         return Menu.ITEM_SCROLL_BACK;

                }


        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                loadAllFiles();

            } catch (Exception e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }

    public void fetchDocumentID() {

        try {
        if (utils.isConnectingToInternet()) {

            storageService = new StorageService(getActivity());

            storageService.findAllDocs(Config.collectionNotification, new App42CallBack() {

                @Override
                public void onSuccess(Object o) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Storage response = (Storage) o;

                    if (response != null) {

                        if (response.getJsonDocList().size() > 0) {
                            try {
                                for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                    Storage.JSONDocument jsonDocument = response.
                                            getJsonDocList().get(i);

                                    strDocument = jsonDocument.getJsonDoc();
                                    strDocID = jsonDocument.getDocId();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    } else {
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    if (e != null) {
                        utils.toast(2, 2, getString(R.string.error));
                    } else {
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });

        }
        else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteNotification(final String strDocID) {

        if (utils.isConnectingToInternet()) {

            storageService = new StorageService(getActivity());

            storageService.deleteDocById(Config.collectionNotification, strDocID,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (o != null) {
                                String selection = DbHelper.COLUMN_OBJECT_ID;

                                String selectionArgs[] = {strDocID};
                                result = CareTaker.dbCon.delete(Config.collectionNotification, selection, selectionArgs);
                                //utils.refreshNotifications();

                                utils.toast(2, 2, getString(R.string.notification_deleted));
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                Utils.log(e.getMessage(), " Failure ");
                                utils.toast(2, 2, getString(R.string.error));
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

}