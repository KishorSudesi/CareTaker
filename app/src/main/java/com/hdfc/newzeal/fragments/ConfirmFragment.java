package com.hdfc.newzeal.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hdfc.adapters.ConfirmListViewAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.db.DbCon;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.FileModel;
import com.hdfc.newzeal.AccountSuccessActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<ConfirmViewModel>();
    public static ListView list;
    public static ConfirmListViewAdapter adapter;
    public static Button buttonContinue;
    private static boolean isRegistered;
    private Libs libs;
    private ProgressDialog progressDialog;
    private String strCustomerImageUrl = "";

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

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (libs.isConnectingToInternet()) {
                    insertAllData();
                } else libs.toast(2, 2, getString(R.string.warning_internet));
            }
        });

        setListView();

        return addFragment;
    }

    public void callSuccess(boolean isRegistered) {

        if (isRegistered) {

            UploadService uploadService = new UploadService(getActivity());

            uploadService.getAllFilesByUser(Config.strUserName, new App42CallBack() {
                public void onSuccess(Object response) {

                    Libs.log(response.toString(), " ERROR ");

                    Upload upload = (Upload) response;
                    ArrayList<Upload.File> fileList = upload.getFileList();

                    Libs.log(String.valueOf(fileList.size()), " ERROR1 ");

                    if (fileList.size() > 0) {

                        for (int i = 0; i < fileList.size(); i++) {
                            Config.fileModels.add(
                                    new FileModel(fileList.get(i).getName()
                                            , fileList.get(i).getUrl(),
                                            fileList.get(i).getType()));

                        }

                    }
                    //else libs.toast(2, 2, getString(R.string.error)); check this

                    Config.boolIsLoggedIn = true;

                    Intent dashboardIntent = new Intent(getActivity(), AccountSuccessActivity.class);
                    //
                    SignupActivity.strCustomerName = "";
                    SignupActivity.strCustomerEmail = "";
                    SignupActivity.strCustomerContactNo = "";
                    SignupActivity.strCustomerAddress = "";
                    SignupActivity.strCustomerImg = "";
                    SignupActivity.longUserId = 0;
                    //

                    libs.parseData();

                    progressDialog.dismiss();
                    startActivity(dashboardIntent);

                    getActivity().finish();

                }

                public void onException(Exception ex) {
                    progressDialog.dismiss();
                    libs.toast(2, 2, getString(R.string.error));
                    Libs.log(ex.getMessage(), " 123 ");
                    //ex.printStackTrace();
                }
            });

        } else {
            progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error_register));
        }

    }

    public void insertAllData() {

        try {

            UploadService uploadService = new UploadService(getActivity());

            progressDialog.setMessage(getString(R.string.uploading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            uploadService.uploadImageCommon(SignupActivity.strCustomerImg, libs.replaceSpace(SignupActivity.strCustomerName), "Profile Picture", SignupActivity.strCustomerEmail, UploadFileType.IMAGE, new App42CallBack() {
                public void onSuccess(Object response) {

                    Upload upload = (Upload) response;
                    ArrayList<Upload.File> fileList = upload.getFileList();

                    if (fileList.size() > 0) {
                        strCustomerImageUrl = fileList.get(0).getUrl();

                        isRegistered = NewZeal.dbCon.prepareData(strCustomerImageUrl);

                        if (isRegistered) {

                            StorageService storageService = new StorageService(getActivity());

                            storageService.insertDocs(Config.jsonServer, new AsyncApp42ServiceApi.App42StorageServiceListener() {

                                @Override
                                public void onDocumentInserted(Storage response) {

                                    UserService userService = new UserService(getActivity());

                                    //userService.addJSONObject(collectionName, jsonDoc);

                                    userService.onCreateUser(SignupActivity.strCustomerEmail, DbCon.strPass, SignupActivity.strCustomerEmail, new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            DbCon.strPass = "";
                                            Config.jsonObject = Config.jsonServer;
                                            Config.jsonServer = null;
                                            isRegistered = true;
                                            Config.strUserName = SignupActivity.strCustomerEmail;
                                            callSuccess(isRegistered);
                                        }

                                        @Override
                                        public void onException(Exception e) {
                                            Libs.log(e.getMessage(), "");
                                            DbCon.strPass = "";
                                            isRegistered = false;
                                            callSuccess(isRegistered);
                                        }
                                    });

                                }

                                @Override
                                public void onUpdateDocSuccess(Storage response) {
                                    //do nothing
                                }

                                @Override
                                public void onFindDocSuccess(Storage response) {
                                    //do nothing
                                }

                                @Override
                                public void onInsertionFailed(App42Exception ex) {
                                    isRegistered = false;
                                    //progressDialog.dismiss();
                                    Libs.log(ex.getMessage(), "onInsertionFailed");
                                    callSuccess(isRegistered);
                                }

                                @Override
                                public void onFindDocFailed(App42Exception ex) {
                                    //do nothing
                                }

                                @Override
                                public void onUpdateDocFailed(App42Exception ex) {
                                    //do nothing
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.error_register));
                        }

                    } else {
                        progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.error_register_image));
                    }
                }

                public void onException(Exception ex) {
                    progressDialog.dismiss();
                    Libs.log(ex.getMessage(), "onException");
                    libs.toast(2, 2, getString(R.string.error_register_image));
                }

            });

        } catch (Exception e) {
            if (progressDialog != null)
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error_register));
        }

        /*if (NewZeal.dbCon.sendToServer()) {

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
        }*/
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
