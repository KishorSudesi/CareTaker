package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hdfc.caretaker.AdditionalServicesActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.views.RoundedImageView;

import java.io.File;


public class MyAccountFragment extends Fragment {
    private static Bitmap bitmap;
    private static RoundedImageView roundedImageView;
    private static Handler threadHandler;
   // private RelativeLayout loadingPanel;
    private static ProgressDialog progressDialog;
    TextView txtviewBuyServices;
    TextView txtNumber, txtAddress, textViewName, textViewEmail, textViewLogout;
    private Libs libs;

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

        libs = new Libs(getActivity());

        //loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        progressDialog = new ProgressDialog(getActivity());

        roundedImageView = (RoundedImageView) view.findViewById(R.id.imageView5);

        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewLogout = (TextView) view.findViewById(R.id.logout);

        txtAddress = (TextView) view.findViewById(R.id.editText31);

        if (Config.customerModel != null && Config.customerModel.getStrAddress() != null)
            txtAddress.setText(Config.customerModel.getStrAddress());

        if (Config.customerModel != null) {
            txtNumber.setText(Config.customerModel.getStrContacts());
            textViewName.setText(Config.customerModel.getStrName());
            textViewEmail.setText(Config.customerModel.getStrEmail());
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
                MyAccountEditFragment myAccountEditFragment = MyAccountEditFragment.newInstance();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_dashboard, myAccountEditFragment);
                ft.commit();
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
                        Libs.logout();
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

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //loadingPanel.setVisibility(View.VISIBLE);

        return view;
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (bitmap != null)
                roundedImageView.setImageBitmap(bitmap);

            //loadingPanel.setVisibility(View.GONE);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                if(Config.customerModel!=null) {
                    File f = libs.getInternalFileImages(Config.strCustomerImageName);
                    bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                }

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
