package com.hdfc.caretaker.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hdfc.adapters.DependAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.AdditionalServicesActivity;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


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
    private static ImageView roundedImageView;
    //private static Handler threadHandler;
    private static RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    private static Context context;
    private static int iPosition;
    public DependAdapter adapter;
    TextView txtviewBuyServices, txtviewChangePassword;
    TextView txtNumber, txtAddress, textViewName, textViewEmail, textViewLogout, textViewRecipient;
    String strOldPass, strPass;
    private EditText editTextOldPassword, editTextPassword, editTextConfirmPassword;
    private Utils utils;
    private SessionManager sessionManager;

    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        txtviewChangePassword = (TextView) view.findViewById(R.id.txtviewChangePassword);
        txtNumber = (TextView) view.findViewById(R.id.editText3);

        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonBack);
        buttonBack.setVisibility(View.GONE);

        utils = new Utils(getActivity());
        sessionManager = new SessionManager(getActivity());

        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        context = getActivity();

        progressDialog = new ProgressDialog(getActivity());

        // dependpager = (ViewPager)view.findViewById(R.id.dependCarousel);
        roundedImageView = (ImageView) view.findViewById(R.id.imageView5);


        /*if(Config.dependentNames.size()<=1)
            roundedImageView.setVisibility(View.INVISIBLE);*/

        try {
           /* if(Config.dependentNames.size()>0) {
                int intPosition = 0;

                if (Config.dependentNames.size() > 1)
                    intPosition = 1;

                iPosition = intPosition;*/

//            threadHandler = new ThreadHandler();
//            Thread backgroundThread = new BackgroundThread();
//            backgroundThread.start();

            //}

            loadingPanel.setVisibility(View.VISIBLE);

            if (Config.customerModel != null) {
                loadImageSimpleTarget();

//            threadHandler = new ThreadHandler();
//            Thread backgroundThread = new BackgroundThread();
//            backgroundThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewRecipient = (TextView) view.findViewById(R.id.addreceiption);
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

        textViewRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddRecipient();
            }
        });


        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccountEdit();
            }
        });


        txtviewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_change_password);

                editTextOldPassword = (EditText) dialog.findViewById(R.id.editOldPassword);
                editTextPassword = (EditText) dialog.findViewById(R.id.editPassword);
                editTextConfirmPassword = (EditText) dialog.findViewById(R.id.editConfirmPassword);
                Button dialogButton = (Button) dialog.findViewById(R.id.btndialogOk);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editTextOldPassword.setError(null);
                        editTextPassword.setError(null);
                        editTextConfirmPassword.setError(null);

                        boolean cancel = false;
                        View focusView = null;

                        strOldPass = editTextOldPassword.getText().toString().trim();
                        strPass = editTextPassword.getText().toString().trim();
                        String strConfirmPass = editTextConfirmPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(strOldPass)) {
                            editTextOldPassword.setError(getString(R.string.error_field_required));
                            focusView = editTextOldPassword;
                            cancel = true;
                        }
                        if (TextUtils.isEmpty(strPass)) {
                            editTextPassword.setError(getString(R.string.error_field_required));
                            focusView = editTextOldPassword;
                            cancel = true;
                        }
                        if (TextUtils.isEmpty(strConfirmPass)) {
                            editTextConfirmPassword.setError(getString(R.string.error_field_required));
                            focusView = editTextOldPassword;
                            cancel = true;
                        }
                        if (!Utils.isEmpty(strOldPass) && !Utils.isEmpty(strPass) &&
                                !Utils.isEmpty(strConfirmPass)) {

                            if (!strPass.equalsIgnoreCase(strConfirmPass)) {
                                editTextConfirmPassword.setError(getString(R.string.error_confirm_password));
                                focusView = editTextConfirmPassword;
                                cancel = true;
                            }

                            if (strOldPass.equalsIgnoreCase(strPass)) {
                                editTextPassword.setError(getString(R.string.error_same_password));
                                focusView = editTextPassword;
                                cancel = true;
                            }
                        }

                        if (cancel) {
                            focusView.requestFocus();
                        } else {

                            if (utils.isConnectingToInternet()) {

                        /*progressDialog.setMessage(getActivity().getString(R.string.uploading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();*/

                                // DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

                                StorageService storageService = new StorageService(getActivity());

                                JSONObject jsonToUpdate = new JSONObject();


                                storageService.updateDocs(jsonToUpdate,
                                        Config.customerModel.getStrCustomerID(),
                                        Config.collectionCustomer, new App42CallBack() {
                                            @Override
                                            public void onSuccess(Object o) {

                                                if (strPass != null && !strPass.equalsIgnoreCase("")) {

                                                    if (utils.isConnectingToInternet()) {

                                                        // progressDialog.setMessage(getActivity().getString(R.string.verify_identity_password));

                                       /* try {
                                            strOldPass = AESCrypt.encrypt(Config.string, strOldPass);
                                            strPass = AESCrypt.encrypt(Config.string, strPass);
                                        } catch (GeneralSecurityException e) {
                                            e.printStackTrace();
                                        }*/

                                                        UserService userService = new UserService(getActivity());

                                                        userService.onChangePassword(Config.strUserName, strOldPass
                                                                , strPass, new App42CallBack() {
                                                                    @Override
                                                                    public void onSuccess(Object o) {
                                                                        // progressDialog.dismiss();
                                                                        // DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                                        sessionManager.createLoginSession(strPass, Config.strUserName);
                                                                        if (sessionManager.getOldPassword() != null && !(sessionManager.getOldPassword().equalsIgnoreCase("")) && sessionManager.getOldPassword().length() > 0) {

                                                                        } else {
                                                                            sessionManager.saveOldPassword(strOldPass);
                                                                        }
                                                                        utils.toast(1, 1, "Password Change Successfully");
                                                                        dialog.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onException(Exception e) {
                                                                        // progressDialog.dismiss();
                                                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                                        try {
                                                                            JSONObject jsonObject = new JSONObject(e.getMessage());
                                                                            JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                                                            String strMess = jsonObjectError.getString("details");

                                                                            utils.toast(2, 2, strMess);
                                                                        } catch (JSONException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                    }
                                                                });

                                                    } else
                                                        utils.toast(2, 2, getString(R.string.warning_internet));

                                                } else {
                                                    // progressDialog.dismiss();
                                                    // DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                    utils.toast(1, 1, "Enter Password ");

                                                }
                                            }

                                            @Override
                                            public void onException(Exception e) {
                                                //progressDialog.dismiss();
                                                // DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                utils.toast(2, 2, e.getMessage());
                                            }
                                        });

                            } else utils.toast(2, 2, getString(R.string.warning_internet));
                        }


                    }
                });


                dialog.show();
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



        /*progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        return view;
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            roundedImageView.setImageBitmap(bitmap);
            loadingPanel.setVisibility(View.GONE);
        }
    };

    private void loadImageSimpleTarget() {

        Glide.with(getActivity())
                .load(Config.customerModel.getStrImgUrl())
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.person_icon)
                .into(target);
    }

    public void goToAccountEdit() {
        MyAccountEditFragment myAccountEditFragment = MyAccountEditFragment.newInstance();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_dashboard, myAccountEditFragment);
        ft.commit();
    }

    public void goToAddRecipient() {
        AddCareRecipientsFragment addRecipientFragment = AddCareRecipientsFragment.newInstance();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_dashboard, addRecipientFragment);
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

//    public static class ThreadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
////            progressDialog.dismiss();
//            DashboardActivity.loadingPanel.setVisibility(View.GONE);
//
//            if (bitmap != null) {
//                roundedImageView.setImageBitmap(bitmap);
//            }
//
//            loadingPanel.setVisibility(View.GONE);
//        }
//    }


    //    public class BackgroundThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                //if(Config.customerModel!=null) {
//                File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
//                System.out.println("TOTAL SPACE : "+f.getTotalSpace()+" "+" USABLE SPACE : "+f.getUsableSpace());
//                bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
//                //}
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            threadHandler.sendEmptyMessage(0);
//        }
//    }
    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                //if(Config.customerModel!=null) {
                File f = utils.getInternalFileImages(Config.customerModel.getStrCustomerID());
                System.out.println("TOTAL SPACE : " + f.getTotalSpace() + " " + " USABLE SPACE : " + f.getUsableSpace());
                bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                //}

            } catch (Exception e) {
                e.printStackTrace();
            }
            //threadHandler.sendEmptyMessage(0);
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
