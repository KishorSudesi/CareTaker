package com.hdfc.caretaker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hdfc.adapters.MileStoneAdapter;
import com.hdfc.adapters.RatingCompletedAdapter;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FieldModel;
import com.hdfc.views.TouchImageView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CompletedActivity extends AppCompatActivity {

    private static boolean bWhichScreen;
    private static Bitmap bitmapImg;
    private static Handler threadHandler;
    private static ImageView imageViewCarla;
    private static RelativeLayout loadingPanel;
    private static ActivityModel activityModel;
    private static ExpandableListView expandableListView;
    private static List<Bitmap> bitmapimages = new ArrayList<>();
    private static LinearLayout _thumbnails;
    private static Context context;
    private static int iRating = 0, iActivityPosition = 0;
    private static View previousViewButton;
    private static String strUserName;
    private static JSONObject jsonObjectMess;
    private static RatingCompletedAdapter ratingCompletedAdapter;
    private static ArrayList<String> strImageTitles = new ArrayList<>();
    private static ListView listView;
    private static boolean bReload;
    private String strCarlaImageName, strCarlaImageUrl;
    private Utils utils;
    private EditText editFeedBack;
    //private CheckBox checkReport;
    private boolean checked = false;
    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<FieldModel>> listDataChild = new HashMap<>();
    private int[] locationOnScreen = new int[2];
    private ScrollView sv;
    private TextView tvTasks;
    private TextView smileyMessage;
    private LinearLayout linearLayoutRatingAdd;
    private byte START_FROM = 0;
    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            bitmapImg = bitmap;


            if (bitmap != null)
                imageViewCarla.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        try {
            context = CompletedActivity.this;

            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                bWhichScreen = extras.getBoolean("WHICH_SCREEN", false);
                activityModel = (ActivityModel) extras.getSerializable("ACTIVITY");
                iActivityPosition = extras.getInt("ACTIVITY_POSITION", -1);
                if (getIntent().hasExtra(Config.KEY_START_FROM)) {
                    START_FROM = getIntent().getByteExtra(Config.KEY_START_FROM, (byte) 0);
                }
            }

            Button backButton = (Button) findViewById(R.id.buttonBack);

            if (backButton != null) {
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goBack();
                    }
                });
            }

            bReload = false;

           /* try {
                ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
                if (imgBg != null) {
                    imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                            R.drawable.app_bg, Config.intScreenWidth, Config.intScreenHeight));

                    //CrashLogger.getInstance().init(MainActivity.this);
                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }*/
            TextView txtAdditionalServices = (TextView) findViewById(R.id.txtAdditionalServices);
            TextView txtViewMSG = (TextView) findViewById(R.id.textViewMSG);
            TextView txtViewDate = (TextView) findViewById(R.id.textViewDate);
            TextView txtViewHead1 = (TextView) findViewById(R.id.textViewHead1);
            TextView txtViewHead2 = (TextView) findViewById(R.id.textViewHead2);
            imageViewCarla = (ImageView) findViewById(R.id.imageViewCarla);
            loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
            tvTasks = (TextView) findViewById(R.id.tvTasks);

            linearLayoutRatingAdd = (LinearLayout) findViewById(R.id.linearLayoutRatingAdd);
            final TextView textViewAddRating = (TextView) findViewById(R.id.textViewAddRating);

            sv = (ScrollView) findViewById(R.id.scrollView);

            smileyMessage = (TextView) findViewById(R.id.smileyMessage);

            if (textViewAddRating != null) {

                if (activityModel.getFeedBackModels() != null && activityModel.getFeedBackModels().size() > 0 && activityModel.isRatingAddedByCutsm()) {
                    textViewAddRating.setVisibility(View.GONE);
                }

                textViewAddRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TextView textView = (TextView)v;

                        /*TranslateAnimation ta = new TranslateAnimation(0, 0, 15, Animation.RELATIVE_TO_SELF);
                        ta.setDuration(1000);
                        ta.setFillAfter(true);*/
                        //relLayout.startAnimation(ta);

                      /*  Animation animation   =    AnimationUtils.loadAnimation(context, R.anim.float_anim);
                        animation.setDuration(500);*/

                        // in future need to add a check based on customer id and freedbacktype=customer
                        if (activityModel.getFeedBackModels() != null && activityModel.getFeedBackModels().size() > 0 && activityModel.isRatingAddedByCutsm()) {
                            utils.toast(2, 2, getString(R.string.validation_rating));
                        } else {

                            if (linearLayoutRatingAdd != null) {
                                //linearLayoutRatingAdd.startAnimation(ta);

                                //linearLayoutRatingAdd.startAnimation(animation);


                                if (linearLayoutRatingAdd.getVisibility() == View.GONE) {

                                    if (sv != null) {
                                        sv.scrollTo(0, sv.getScrollY() + 50);
                                        //linearLayoutRatingAdd.startAnimation(ta);
                                    }

                                    linearLayoutRatingAdd.setVisibility(View.VISIBLE);
                                    textViewAddRating.setTextAppearance(context, -1);
                                    textViewAddRating.setTextAppearance(context, R.style.AddRatingStyleMedium);


                                } else {
                                    if (sv != null) {
                                        sv.scrollTo(0, sv.getScrollY() - 50);
                                        //linearLayoutRatingAdd.startAnimation(ta);
                                    }

                                    linearLayoutRatingAdd.setVisibility(View.GONE);
                                    textViewAddRating.setTextAppearance(context, -1);
                                    textViewAddRating.setTextAppearance(context, R.style.AddRatingStyleLarge);
                                }
                            }
                        }

                    }
                });
            }

            utils = new Utils(CompletedActivity.this);

            if (activityModel != null) {

                int iPosition = Config.strProviderIds.indexOf(activityModel.getStrProviderID());

                if (iPosition > -1 && iPosition < Config.providerModels.size())
                    strCarlaImageUrl = Config.providerModels.get(iPosition).getStrImgUrl();
                String strDate = "\n" + utils.formatDate(activityModel.getStrActivityDate());
                if (txtAdditionalServices != null) {
                    txtAdditionalServices.setText(activityModel.getStrActivityName() + strDate);
                }
                if (txtViewHead2 != null) {
                    txtViewHead2.setText(activityModel.getStrActivityName());
                    txtViewHead2.setVisibility(View.GONE);
                }
                String strHead = getResources().getString(R.string.assisted_by);

                if (iPosition > -1 && iPosition < Config.providerModels.size())
                    strHead += Config.providerModels.get(iPosition).getStrName();

                if (txtViewHead1 != null) {
                    txtViewHead1.setText(strHead);
                }

                if (txtViewDate != null) {
                    txtViewDate.setText(strDate);
                    txtViewDate.setVisibility(View.GONE);

                }
                if (txtViewMSG != null) {
                    txtViewMSG.setText(getString(R.string.text_description) + activityModel.getStrActivityDesc());
                }

                strCarlaImageName = utils.replaceSpace(activityModel.getStrProviderID());

                expandableListView = (ExpandableListView) findViewById(R.id.listViewAdditionalServices);

                refreshAdapter();

                MileStoneAdapter mileStoneAdapter = new MileStoneAdapter(CompletedActivity.this, listDataChild,
                        listDataHeader);

                expandableListView.setAdapter(mileStoneAdapter);

                //Utils.setListViewHeightBasedOnChildren(expandableListView);
                mileStoneAdapter.notifyDataSetChanged();
                tvTasks.getLocationOnScreen(locationOnScreen);
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        utils.setListViewHeight(expandableListView, groupPosition);
                        return false;
                    }
                });
                /*expandableListView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });*/
                _thumbnails = (LinearLayout) findViewById(R.id.thumbnails);
                //imageGallery = (ImageView) findViewById(R.id.imageViewGallery);

                editFeedBack = (EditText) findViewById(R.id.editFeedBack);
                Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

                /* checkReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkReport.isChecked()) {
                            checkReport.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.tick_red));
                        } else {
                            checkReport.setButtonDrawable(getResources().
                                    getDrawable(R.mipmap.tick));
                        }
                    }
                });*/

                if (btnSubmit != null) {
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //checked = checkReport.isChecked();

                            try {
                                boolean b = true;

                                editFeedBack.setError(null);

                                if (!activityModel.getStrActivityStatus().
                                        equalsIgnoreCase("completed")) { //
                                    b = false;
                                    utils.toast(2, 2, getString(R.string.activity_not_completed));

                                } else {
                                    if (iRating == 0) {
                                        b = false;
                                        utils.toast(2, 2, getString(R.string.select_rating));

                                    } else if (iRating <= 3) {
                                        //b = false;
                                        //utils.toast(2, 2, getString(R.string.select_rating));

                                        if (TextUtils.isEmpty(editFeedBack.getText().toString())) {
                                            b = false;
                                            editFeedBack.setError(getString(R.string.error_field_required));
                                            utils.toast(2, 2, getString(R.string.validation_feedback));
                                        }
                                    }
                                }


                                if (b) {
                                    textViewAddRating.setVisibility(View.GONE);
                                    linearLayoutRatingAdd.setVisibility(View.GONE);
                                    smileyMessage.setVisibility(View.GONE);
                                    uploadCheckBox();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

                listView = (ListView) findViewById(R.id.listViewRatings);
                TextView emptyTextView = (TextView) findViewById(android.R.id.empty);

                ratingCompletedAdapter = new RatingCompletedAdapter(context, activityModel.
                        getFeedBackModels());

                if (listView != null) {
                    listView.setAdapter(ratingCompletedAdapter);
                    listView.setEmptyView(emptyTextView);
                    Utils.setListViewHeightBasedOnChildren(listView);

                   /* listView.setOnTouchListener(new View.OnTouchListener() {
                        // Setting on Touch Listener for handling the touch inside ScrollView
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // Disallow the touch request for parent scroll on touch of child view

                           // sv.get

                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });*/
                }

                loadingPanel.setVisibility(View.VISIBLE);
                if (strCarlaImageName != null && !strCarlaImageName.equalsIgnoreCase("")) {

                    loadImageSimpleTarget(strCarlaImageUrl);
                }
                threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadImageSimpleTarget(String url) {

        try {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.person_icon)
                    .into(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadCheckBox() {

        try {
            if (utils.isConnectingToInternet()) {

                loadingPanel.setVisibility(View.VISIBLE);

                Date doneDate = new Date();

                final String strDoneDate = utils.convertDateToString(doneDate);

                final StorageService storageService = new StorageService(context);

                JSONObject jsonObjectFeedback = new JSONObject();

                try {

                    JSONObject jsonObjectFeedbacks;

                    JSONArray jsonArrayFeedback = new JSONArray();

                    for (FeedBackModel feedBackModel : activityModel.getFeedBackModels()) {

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
                    sJsonObjectFeedback.put("feedback_by", Config.customerModel.getStrCustomerID());
                    sJsonObjectFeedback.put("feedback_report", "0"); //String.valueOf(checked)
                    sJsonObjectFeedback.put("feedback_time", strDoneDate);
                    sJsonObjectFeedback.put("feedback_by_type", "customer");

                    jsonArrayFeedback.put(sJsonObjectFeedback);

                    jsonObjectFeedback.put("feedbacks", jsonArrayFeedback);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                storageService.updateDocs(jsonObjectFeedback, activityModel.getStrActivityID(),
                        Config.collectionActivity, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {

                                    try {

                                        Storage storage = (Storage) o;

                                        if (storage.getJsonDocList().size() > 0) {

                                            Storage.JSONDocument jsonDocList = storage.getJsonDocList().get(0);

                                            JSONObject jsonObject = new JSONObject(jsonDocList.getJsonDoc());

                                            if (jsonObject.has("feedbacks")) {

                                                try {
                                                    Storage.JSONDocument jsonDocument = storage.getJsonDocList().get(0);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strActivityId = jsonDocument.getDocId();
                                                    JSONObject jsonObjectActivity =
                                                            new JSONObject(strDocument);
                                                    JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                                    jsonObjectActivity.remove("milestones");
                                                    strDocument = jsonObjectActivity.toString();
                                                    String values[] = {strActivityId, storage.getJsonDocList().get(0).getUpdatedAt(), strDocument, Config.collectionActivity, "", "1", jsonObjectActivity.optString("activity_date"), ""};
                                                    Log.i("TAG", "Date :" + jsonObjectActivity.optString("activity_date"));

                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {strActivityId, Config.collectionActivity};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                    for (int j = 0; j < jArray.length(); j++) {
                                                        JSONObject jObj = jArray.optJSONObject(j);
                                                        strDocument = jObj.toString();
                                                        selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " =? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgsMile = {strActivityId, Config.collectionMilestones, jObj.optString("id")};
                                                        String valuesMilestone[] = {strActivityId, storage.getJsonDocList().get(0).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, valuesMilestone, Config.names_collection_table, selectionArgsMile);


                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                                JSONArray jsonArrayFeedback = jsonObject.
                                                        getJSONArray("feedbacks");

                                                activityModel.getFeedBackModels().clear();

                                              /*  Config.dependentModels.get(Config.intSelectedDependent).
                                                        getMonthActivityModel().get(iActivityPosition).clearFeedBackModel();*/

                                                for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                                    JSONObject jsonObjectFeedback =
                                                            jsonArrayFeedback.getJSONObject(k);

                                                    if (jsonObjectFeedback.has("feedback_message")) {

                                                        FeedBackModel feedBackModel = new FeedBackModel(
                                                                jsonObjectFeedback.getString("feedback_message"),
                                                                jsonObjectFeedback.getString("feedback_by"),
                                                                jsonObjectFeedback.getInt("feedback_rating"),
                                                                false, // jsonObjectFeedback.getBoolean("feedback_report")
                                                                jsonObjectFeedback.getString("feedback_time"),
                                                                jsonObjectFeedback.getString("feedback_by_type"));

                                                        activityModel.setFeedBackModel(feedBackModel);
                                                        if (feedBackModel.getStrFeedBackByType().equalsIgnoreCase("customer") &&
                                                                feedBackModel.getStrFeedBackBy().equalsIgnoreCase(activityModel.getStrustomerID())) {
                                                            activityModel.setRatingAddedByCutsm(true);
                                                        }

                                                       /* Config.dependentModels.get(Config.intSelectedDependent).
                                                                getActivityModels().get(iActivityPosition).setFeedBackModel(feedBackModel);*/
                                                    }
                                                }



                                              /*  int iActivityPos = Config.strActivityIds.indexOf(activityModel.getStrActivityID());

                                                if(iActivityPos>-1)
                                                    Config.dependentModels
    */

                                                int iPosition = Config.strProviderIds.indexOf(activityModel.getStrProviderID());
                                                strUserName = Config.providerModels.get(iPosition).getStrEmail();


                                                String strPushMessage = Config.customerModel.getStrName() + getString(R.string.has_given_feedback) +
                                                        activityModel.getStrActivityName() + getString(R.string.dated) +
                                                        utils.convertStringToDate(activityModel.getStrActivityDate());

                                                /* +
                                                        getString(R.string.on) + strDoneDate;*/

                                                //
                                                String strDateNow = "";
                                                Calendar calendar = Calendar.getInstance();
                                                Date dateNow = calendar.getTime();
                                                strDateNow = utils.convertDateToString(dateNow);

                                                jsonObjectMess = new JSONObject();

                                                try {

                                                    jsonObjectMess.put("created_by", Config.customerModel.getStrCustomerID());
                                                    jsonObjectMess.put("time", strDateNow);
                                                    jsonObjectMess.put("user_type", "provider");
                                                    jsonObjectMess.put("user_id", activityModel.getStrProviderID());
                                                    jsonObjectMess.put("activity_id", activityModel.getStrActivityID());//todo add to care taker
                                                    jsonObjectMess.put("created_by_type", "customer");
                                                    jsonObjectMess.put(App42GCMService.ExtraMessage, strPushMessage);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                //

                                                insertNotification();
                                            }
                                        } else {
                                           /* if (progressDialog.isShowing())
                                                progressDialog.dismiss();*/
                                            loadingPanel.setVisibility(View.GONE);
                                            utils.toast(2, 2, getString(R.string.error));
                                        }

                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                      /*  if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                   /* if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    loadingPanel.setVisibility(View.GONE);
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                loadingPanel.setVisibility(View.GONE);
                                Utils.log(e.getMessage(), " RESP ");
                                if (e != null) {
                                    utils.toast(2, 2, getString(R.string.error));
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        }
                );


            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushToProvider(String strUserName, String strMessage) {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(context);

            pushNotificationService.sendPushToUser(strUserName, strMessage,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            populateFeedbacks();
                        }

                        @Override
                        public void onException(Exception ex) {
                            populateFeedbacks();
                        }
                    });
        } else {
            populateFeedbacks();
        }
    }


    ///////////////////////
    private void insertNotification() {

        if (utils.isConnectingToInternet()) {

            final StorageService storageService = new StorageService(context);

            storageService.insertDocs(jsonObjectMess,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    sendPushToProvider(strUserName, jsonObjectMess.toString());
                                } else {
                                    populateFeedbacks();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            populateFeedbacks();
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    },
                    Config.collectionNotification);
        } else {
            populateFeedbacks();
        }
    }

    private void populateFeedbacks() {

        try {

           /* if (progressDialog.isShowing())
                progressDialog.dismiss();*/

            loadingPanel.setVisibility(View.GONE);

           /* ActivityCompletedFragment.setMenuInitView();
            ActivityCompletedFragment.imageButtonRating.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            ViewRatingFragment newFragment = ViewRatingFragment.newInstance();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_completed_activity, newFragment);
            transaction.addToBackStack(null);

            transaction.commit();*/

            ratingCompletedAdapter.notifyDataSetChanged();
            bReload = true;
            Utils.setListViewHeightBasedOnChildren(listView);

            utils.toast(2, 2, getString(R.string.rating_added));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRating(View v) {

        iRating = Integer.parseInt((String) v.getTag());

        if (iRating == 1) {
            smileyMessage.setVisibility(View.VISIBLE);
            smileyMessage.setText("Sorry to Hear that, New Zeal Team will contact you shortly");
        }
        if (iRating == 2) {
            smileyMessage.setVisibility(View.VISIBLE);
            smileyMessage.setText("Not Happy with us, Your Feedback will Help us Improve.");
        }
        if (iRating == 3) {
            smileyMessage.setVisibility(View.VISIBLE);
            smileyMessage.setText("Something went wrong-Tell us more.");
        }
        if (iRating == 4) {
            smileyMessage.setVisibility(View.GONE);
        }
        if (iRating == 5) {
            smileyMessage.setVisibility(View.GONE);
        }

        if (previousViewButton != null)
            previousViewButton.setBackgroundDrawable(null);

        previousViewButton = v;

        //v.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_cell_blue));
        v.setBackgroundColor(context.getResources().getColor(R.color.blue));
    }

    private void refreshAdapter() {

        try {

            if (expandableListView != null) {

                listDataHeader.clear();
                listDataChild.clear();

                for (int i = 0; i < activityModel.getMilestoneModels().size(); i++) {

                    listDataHeader.add(activityModel.getMilestoneModels().get(i).getStrMilestoneName());

                    if (activityModel.getMilestoneModels().size() - 1 == i) {
                        for (int j = 0; j < activityModel.getMilestoneModels().get(i).getFieldModels().size(); j++) {
                            int index = j > 0 ? j - 1 : 0;
                            if (activityModel.getMilestoneModels().get(i).getFieldModels().size() - 1 == j &&
                                    activityModel.getMilestoneModels().get(i).getFieldModels().get(index).getStrFieldData().equalsIgnoreCase("SuccessFul")) {
                                activityModel.getMilestoneModels().get(i).getFieldModels().remove(j);
                            }
                        }
                    }

                    listDataChild.put(
                            activityModel.getMilestoneModels().get(i).getStrMilestoneName(),
                            activityModel.getMilestoneModels().get(i).getFieldModels()
                    );
                }
                 /* method to set expandable lsit view's height based on children */

                //mileStoneAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goBack() {
        Intent dashboardIntent = new Intent(CompletedActivity.this,
                DashboardActivity.class);
        if (START_FROM == Config.START_FROM_NOTIFICATION) {

            Config.intSelectedMenu = Config.intNotificationScreen;

        } else {


            Bundle args = new Bundle();
            args.putBoolean(Config.strReload, bReload);
            dashboardIntent.putExtras(args);

            if (bWhichScreen)
                Config.intSelectedMenu = Config.intListActivityScreen;
            else
                Config.intSelectedMenu = Config.intActivityScreen;

            //dashboardIntent.putExtra("RELOAD", true);//todo may relaod for refreshing feedback
        }
        startActivity(dashboardIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {


            try {

                _thumbnails.removeAllViews();

                if (bitmapimages.size() > 0) {

                    for (int m = 0; m < bitmapimages.size(); m++) {

                        ImageView _gallery = new ImageView(context);
                        _gallery.setPadding(0, 0, 7, 0);
                        _gallery.setImageBitmap(bitmapimages.get(m));
                        _gallery.setTag(bitmapimages.get(m));
                        _gallery.setTag(R.id.image_title, strImageTitles.get(m));
                        _gallery.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        _gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //imageGallery.setImageBitmap(bitmapimages.get(finalM));

                                final Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                dialog.setContentView(R.layout.image_dialog_layout);

                                TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                                TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                                TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);

                                textViewTitle.setText((String) v.getTag(R.id.image_title));

                                textViewClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mOriginal.
                                        dialog.dismiss();
                                    }
                                });

                                try {
                                    mOriginal.setImageBitmap((Bitmap) v.getTag());
                                } catch (OutOfMemoryError oOm) {
                                    oOm.printStackTrace();
                                }
                                dialog.setCancelable(true);

                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                                dialog.show();
                            }
                        });

                        _thumbnails.addView(_gallery);
                    }
                } else {
                    TextView textView = new TextView(context);

                  /*  textView.setTextAppearance(context, R.style.MilestoneStyle);
                    textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    textView.setPadding(10, 10, 10, 10);
                    textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_success));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    params.setMargins(10, 10, 10, 10);
                    textView.setLayoutParams(params);*/
                    //
                    textView.setText(context.getString(R.string.no_images));
                    _thumbnails.addView(textView);
                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }

            loadingPanel.setVisibility(View.GONE);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {


                try {

                    bitmapimages.clear();

                    //Utils.log(String.valueOf(imageModels.size()), " 1 ");

                    //ArrayList<ImageModel> imageModels = activityModel.getImageModels();

                    for (int i = 0; i < activityModel.getImageModels().size(); i++) {

                        //ImageModel imageModel = activityModel.getImageModels().get(i);

                        if (activityModel.getImageModels().get(i).getStrImageUrl() != null
                                && !activityModel.getImageModels().get(i).getStrImageUrl().equalsIgnoreCase("")) {

                          /*  utils.loadImageFromWeb(activityModel.getImageModels().get(i).getStrImageName(),
                                    activityModel.getImageModels().get(i).getStrImageUrl());*/

                            File file = utils.getInternalFileImages(utils.replaceSpace(activityModel
                                    .getImageModels().get(i).getStrImageName()));

                            Bitmap bitmap = utils.getBitmapFromFile(file.getAbsolutePath(),
                                    Config.intWidth, Config.intHeight);
                            bitmapimages.add(bitmap);

                            strImageTitles.add(activityModel.getStrActivityName());
                            // bitmap.recycle();
                        }
                    }

                    for (int j = 0; j < activityModel.getMilestoneModels().size(); j++) {

                        for (int k = 0; k < activityModel.getMilestoneModels().get(j).getFileModels().size(); k++) {

                            if (activityModel.getMilestoneModels().get(j).getFileModels().get(k).getStrFileUrl() != null
                                    && !activityModel.getMilestoneModels().get(j).getFileModels().get(k).getStrFileUrl().equalsIgnoreCase("")) {

                        /*utils.loadImageFromWeb(activityModel.getImageModels().get(i).getStrImageName(),
                                activityModel.getImageModels().get(i).getStrImageUrl());*/

                                Utils.log(activityModel.getMilestoneModels().get(j).getFileModels().get(k).getStrFileName(), " MS IMAGE ");

                                File file = utils.getInternalFileImages(utils.replaceSpace(
                                        activityModel.getMilestoneModels().get(j).getFileModels().get(k).getStrFileName()
                                ));

                                Bitmap bitmap = utils.getBitmapFromFile(file.getAbsolutePath(),
                                        Config.intWidth, Config.intHeight);
                                bitmapimages.add(bitmap);
                                // bitmap.recycle();
                                strImageTitles.add(activityModel.getMilestoneModels().get(j).getStrMilestoneName());
                            }
                        }
                    }

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    bitmapimages.clear();
                }

                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            tvTasks.getLocationOnScreen(locationOnScreen);
            sv.smoothScrollTo(locationOnScreen[0], locationOnScreen[1] - (locationOnScreen[1] / 2));


        }
    }
}
