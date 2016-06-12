package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.DependAdapter;
import com.hdfc.caretaker.AdditionalServicesActivity;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.RoundedImageView;

import java.io.File;


public class MyAccountFragment extends Fragment {
    public final static int PAGES = Config.dependentModels.size();
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1;//Config.intDependentsCount;
    //public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static ViewPager dependpager;
    private static Bitmap bitmap;
    private static RoundedImageView roundedImageView;
    private static Handler threadHandler;
    private static RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    private static Context context;
    private static int iPosition;
    public DependAdapter adapter;
    TextView txtviewBuyServices;
    TextView txtNumber, txtAddress, textViewName, textViewEmail, textViewLogout;
    private Utils utils;

    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        txtviewBuyServices = (TextView) view.findViewById(R.id.txtviewBuyServices);
        txtNumber = (TextView) view.findViewById(R.id.editText3);

        utils = new Utils(getActivity());

        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        context = getActivity();

        progressDialog = new ProgressDialog(getActivity());

        // dependpager = (ViewPager)view.findViewById(R.id.dependCarousel);
        roundedImageView = (RoundedImageView) view.findViewById(R.id.imageView5);


        /*if(Config.dependentNames.size()<=1)
            roundedImageView.setVisibility(View.INVISIBLE);*/

        try {
           /* if(Config.dependentNames.size()>0) {
                int intPosition = 0;

                if (Config.dependentNames.size() > 1)
                    intPosition = 1;

                iPosition = intPosition;*/

                threadHandler = new ThreadHandler();
                Thread backgroundThread = new BackgroundThread();
                backgroundThread.start();

            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewLogout = (TextView) view.findViewById(R.id.logout);

        txtAddress = (TextView) view.findViewById(R.id.editText31);

        ImageView imageView = (ImageView) view.findViewById(R.id.imgPen);

        if (Config.customerModel != null) {
            txtNumber.setText(Config.customerModel.getStrContacts());
            textViewName.setText(Config.customerModel.getStrName());
            textViewEmail.setText(Config.customerModel.getStrEmail());

            if (Config.customerModel.getStrAddress() != null)
                txtAddress.setText(Config.customerModel.getStrCountryCode());
        }

        txtviewBuyServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdditionalServicesActivity.class);
                startActivity(intent);
            }
        });

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccountEdit();
            }
        });

       /* roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int intPosition, intReversePosition;

                if (dependpager.getCurrentItem() == Config.dependentNames.size() - 1) {
                    intPosition = 0;
                    intReversePosition = intPosition + 1;
                } else {
                    intPosition = dependpager.getCurrentItem() + 1;
                    intReversePosition = dependpager.getCurrentItem();
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

                dependpager.setCurrentItem(intPosition, true);
            }
        });
*/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccountEdit();
            }
        });

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.confirm_logout));
                builder.setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.logout();
                    }
                });
                builder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        loadingPanel.setVisibility(View.VISIBLE);

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        /*progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        return view;
    }

    public void goToAccountEdit() {
        MyAccountEditFragment myAccountEditFragment = MyAccountEditFragment.newInstance();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_dashboard, myAccountEditFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        //listViewActivities.setEmptyView(emptyTextView);
        // loadData(0);

        // adapter = new DependAdapter(getActivity(), getChildFragmentManager());

        // new setAdapterTask().execute();
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
//            progressDialog.dismiss();
            DashboardActivity.loadingPanel.setVisibility(View.GONE);

            if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);
            else {

            }

            loadingPanel.setVisibility(View.GONE);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                //if(Config.customerModel!=null) {
                    File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                //}

            } catch (Exception e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }

   /* public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);
            else
                roundedImageView.setImageBitmap(Utils.noBitmap);
        }
    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            dependpager.setAdapter(adapter);
            dependpager.setOnPageChangeListener(adapter);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            dependpager.setCurrentItem(0);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            dependpager.setOffscreenPageLimit(Config.dependentModels.size()); //1
            //

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            //pager.setPageMargin(-200); //-200
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {

                if(Config.customerModel!=null) {
                    File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(iPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);

                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }*/
}
