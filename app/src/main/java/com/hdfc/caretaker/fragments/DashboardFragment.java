package com.hdfc.caretaker.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hdfc.adapters.CarouselPagerAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.CheckInCareActivity;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1;//Config.intDependentsCount;
    //public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static int PAGES = Config.dependentModels.size();
    public static ImageView leftNav, rightNav;
    public static ViewPager pager;
    private static Utils utils;
    //public static ViewPager pager;
    //public static ArrayList<ActivityModel> activitiesModelArrayList = new ArrayList<>();
    //public static RoundedImageView roundedImageView;
    /*private static ListView listViewActivities;
    private static TextView textView1;
    private static TextView textView2;
    private static TextView textView3;
    private static TextView textView4;*/
   /* private static Bitmap bitmap;
    private static Handler threadHandler;
    private static int iPosition;*/
    //private static Context context;
    //private static Utils utils;
    public CarouselPagerAdapter adapter;
    public Button checkInCare;
    private SessionManager sessionManager = null;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        PAGES = Config.dependentModels.size();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

  /*  public static void loadData(int intIndex) {

        //Utils.log(String.valueOf(intIndex), " INDEX ");

        try {

            //activitiesModelArrayList.clear();
            *//*activitiesModelArrayList = Config.dependentModels.get(intIndex).getActivityModels();

            ActivitiesAdapter activitiesAdapter = new ActivitiesAdapter(context, activitiesModelArrayList);
            listViewActivities.setAdapter(activitiesAdapter);*//*

            //activitiesAdapter.notifyDataSetChanged();

            //Utils.log(String.valueOf(Config.dependentModels.get(intIndex).getActivityModels().size()), " INDEX 0 ");

           *//* textView1.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthBp()));
            textView2.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthHeartRate()));
            textView3.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthBp()));
            textView4.setText(String.valueOf(Config.dependentModels.get(intIndex).getIntHealthHeartRate()));*//*

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            utils = new Utils(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        try {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

            //listViewActivities = (ListView) rootView.findViewById(R.id.listViewActivities);

            pager = (ViewPager) rootView.findViewById(R.id.dpndntCarousel);

            leftNav = (ImageView) rootView.findViewById(R.id.left_nav);
            rightNav = (ImageView) rootView.findViewById(R.id.right_nav);
            PAGES = Config.dependentModels.size();
            checkInCare = (Button) rootView.findViewById(R.id.buttonCheckInCare);

            sessionManager = new SessionManager(getActivity());

            checkInCare.setVisibility(View.GONE);

            adapter = new CarouselPagerAdapter(getActivity(), getChildFragmentManager());

            // Images left navigation
            leftNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = pager.getCurrentItem();
                    if (tab > 0) {
                        tab--;
                        pager.setCurrentItem(tab);
                    } else if (tab == 0) {
                        pager.setCurrentItem(tab);
                    }
                }
            });

            // Images right navigatin
            rightNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = pager.getCurrentItem();
                    tab++;
                    pager.setCurrentItem(tab);

                }
            });

            leftNav.setVisibility(View.GONE);
            new setAdapterTask().execute();


            checkInCare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Config.checkInCareActivityNames.size() > 0) {

                        Intent intent = new Intent(getActivity(), CheckInCareActivity.class);
                        intent.putExtra(Config.KEY_START_FROM, Config.START_FROM_DASHBOARD);
                        startActivity(intent);
                    }

                }
            });


      /*  textView1 = (TextView) rootView.findViewById(R.id.textView1);
        textView2 = (TextView) rootView.findViewById(R.id.textView2);
        textView3 = (TextView) rootView.findViewById(R.id.textView3);
        textView4 = (TextView) rootView.findViewById(R.id.textView4);*/

       /* roundedImageView = (RoundedImageView) rootView.findViewById(R.id.roundedImageView);

        if(Config.dependentNames.size()<=1)
            roundedImageView.setVisibility(View.INVISIBLE);

        try {
            if(Config.dependentNames.size()>0) {
                int intPosition = 0;

                if (Config.dependentNames.size() > 1)
                    intPosition = 1;

                iPosition = intPosition;

                threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

      /*  TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int intPosition, intReversePosition;

                if (pager.getCurrentItem() == Config.dependentNames.size() - 1) {
                    intPosition = 0;
                    intReversePosition = intPosition + 1;
                } else {
                    intPosition = pager.getCurrentItem() + 1;
                    intReversePosition = pager.getCurrentItem();
                }

                try {
                    if (intReversePosition >= Config.dependentNames.size() ||
                            intReversePosition < 0)
                        intReversePosition = 0;

                    iPosition = intReversePosition;

                    threadHandler = new ThreadHandler();
                    Thread backgroundThread = new BackgroundThread();
                    backgroundThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                TranslateAnimation ta = new TranslateAnimation(0, Animation.RELATIVE_TO_SELF, 0, 0);
                ta.setDuration(1000);
                ta.setFillAfter(true);
                roundedImageView.startAnimation(ta);

                pager.setCurrentItem(intPosition, true);
            }
        });*/
            //
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //listViewActivities.setEmptyView(emptyTextView);
        //loadData(0);


    }

    public void fetchLatestCheckInCare(String iMonth, String iYear, String CustomerId) {

        //iMonth = iMonth; // - 1
        try {
            Config.checkInCareActivityNames.clear();
            Cursor cursor = null;
            if (sessionManager.getCheckInCareStatus()) {

                DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);


                try {

                    // WHERE   clause
                    String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                    // WHERE clause arguments
                    String[] selectionArgs = {Config.collectionCheckInCare};
                    cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
                    Log.i("TAG", "Cursor count:" + cursor.getCount());
                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {

                            String strDocument = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT));
                            String strActivityId = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID));
                            //JSONObject jsonObjectActivity = new JSONObject(strDocument);


                            utils.createCheckInCareModel(strActivityId, strDocument);
                        } while (cursor.moveToNext());

                        checkInCare.setVisibility(View.VISIBLE);
                    }/* else {

                    }*/


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                    if (cursor != null)
                        cursor.close();
                }
            }

            if (utils.isConnectingToInternet() && (cursor == null || cursor.getCount() <= 0)) {


                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionCheckInCare);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }

                StorageService storageService = new StorageService(getActivity());

                Query q1 = QueryBuilder.build("year", iYear, QueryBuilder.
                        Operator.EQUALS);
                Query q2 = QueryBuilder.build("month", iMonth, QueryBuilder.
                        Operator.EQUALS);
                Query q3 = QueryBuilder.build("customer_id", CustomerId, QueryBuilder.
                        Operator.EQUALS);

                // Build query q1 for key1 equal to name and value1 equal to Nick

                // Build query q2 for key2 equal to age and value2

                Query q4 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                Query q5 = QueryBuilder.compoundOperator(q3, QueryBuilder.Operator.AND, q4);
                if (sessionManager.getCheckInCareStatus()) {
                    // Build query q2
                    Query q6 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);
                    q5 = QueryBuilder.compoundOperator(q5, QueryBuilder.Operator.AND, q6);
                }

                storageService.findDocsByQueryOrderBy(Config.collectionCheckInCare, q5, 3000, 0, "created_date", 1, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {


                                Storage response = (Storage) o;
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                if (response != null) {

                                    Utils.log(response.toString(), " S ");
                                    Utils.log("Size : " + response.getJsonDocList().size(), " S ");
                                    if (response.getJsonDocList().size() > 0) {
                                        try {
                                            try {
                                                for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                                    Storage.JSONDocument jsonDocument = response.
                                                            getJsonDocList().get(i);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strActivityId = jsonDocument.getDocId();
                                                    //JSONObject jsonObjectActivity = new JSONObject(strDocument);

                                                    String values[] = {strActivityId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionCheckInCare, "", "1", "", ""};

                                                    if (sessionManager.getCheckInCareStatus()) {
                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strActivityId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                                    } else {

                                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                                        utils.createCheckInCareModel(strActivityId, strDocument);

                                                    }


                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if (!sessionManager.getCheckInCareStatus()) {
                                                sessionManager.saveCheckInCareStatus(true);
                                                checkInCare.setVisibility(View.VISIBLE);
                                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

               *//* bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(iPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);*//*

                //threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }*/

    /*  public static class ThreadHandler extends Handler {
          @Override
          public void handleMessage(Message msg) {

            *//*  if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);
            else
                roundedImageView.setImageBitmap(Utils.noBitmap);*//*
        }
    }
*/
    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {

                try {
                    pager.setAdapter(adapter);
                    pager.setOnPageChangeListener(adapter);

                    // Set current item to the middle page so we can fling to both
                    // directions left and right

                    int i = Config.dependentModels.size();

                    if (i > 0)
                        pager.setCurrentItem(0);

                    // Necessary or the pager will only have one extra page to show
                    // make this at least however many pages you can see
                    pager.setOffscreenPageLimit(Config.dependentModels.size()); //1
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //

                // Set margin for pages as a negative number, so a part of next and
                // previous pages will be showed
                // pager.setPageMargin(-110); //-200

                Calendar c = Calendar.getInstance();
                String iyear = String.valueOf(c.get(Calendar.YEAR));
                String imonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                fetchLatestCheckInCare(imonth, iyear, Config.customerModel.getStrCustomerID());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
