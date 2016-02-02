package com.hdfc.newzeal.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.ConfirmListViewAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.newzeal.AccountSuccessActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<ConfirmViewModel>();
    public static ListView list;
    public static ConfirmListViewAdapter adapter;
    public static Button buttonContinue;
    private StorageService storageService;
    private Libs libs;
    private ProgressDialog progressDialog;

    public ConfirmFragment() {
    }

    public static ConfirmFragment newInstance() {
        ConfirmFragment fragment = new ConfirmFragment();
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
        // Inflate the layout for this fragment
        View addFragment = inflater.inflate(R.layout.fragment_confirm, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewConfirm);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        libs = new Libs(getActivity());

        progressDialog = new ProgressDialog(getActivity());

        storageService = new StorageService(getActivity());

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertAllData();
            }
        });

        setListView();
        Log.e("ConfirmFragment", "onCreateView");

        return addFragment;
    }

    public void insertAllData() {

        try {
            progressDialog.setMessage(getString(R.string.uploading));
            progressDialog.show();
        } catch (Exception e) {
        }

        if (NewZeal.dbCon.sendToServer()) {

            storageService.insertDocs(Config.jsonServer, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                @Override
                public void onDocumentInserted(Storage response) {
                    //callSuccessDismiss();

                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                    }

                    Intent dashboardIntent = new Intent(getActivity(), AccountSuccessActivity.class);
                    startActivity(dashboardIntent);

                    //
                    SignupActivity.strCustomerName = "";
                    SignupActivity.strCustomerEmail = "";
                    SignupActivity.strCustomerContactNo = "";
                    SignupActivity.strCustomerAddress = "";
                    SignupActivity.strCustomerImg = "";
                    SignupActivity.longUserId = 0;
                    //

                    getActivity().finish();
                }

                @Override
                public void onUpdateDocSuccess(Storage response) {
                    dismissProgress(response.toString());
                }

                @Override
                public void onFindDocSuccess(Storage response) {
                    dismissProgress(response.toString());
                }

                @Override
                public void onInsertionFailed(App42Exception ex) {
                    dismissProgress(ex.getMessage());
                }

                @Override
                public void onFindDocFailed(App42Exception ex) {
                    dismissProgress(ex.getMessage());
                }

                @Override
                public void onUpdateDocFailed(App42Exception ex) {
                    dismissProgress(ex.getMessage());
                }
            });
        }
    }

    public void callSuccessDismiss() {
        progressDialog.dismiss();
        //libs.toast(1, 1, "Registered Successfully");
    }

    public void dismissProgress(String mess) {
        progressDialog.dismiss();
        Libs.toast(2, 2, mess);
    }

    public void setListData() {

        int intCount = 0;

        if (SignupActivity.longUserId > 0)
            intCount = NewZeal.dbCon.retrieveConfirmDependants(SignupActivity.longUserId);

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setListView() {
        try {
            setListData();
            adapter = new ConfirmListViewAdapter(getContext(), ConfirmFragment.CustomListViewValuesArr);
            list.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
