package com.hdfc.newzeal.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.DependentModel;
import com.hdfc.model.FileModel;
import com.hdfc.newzeal.AccountSuccessActivity;
import com.hdfc.newzeal.DependentDetailPersonalActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<>();
    public static ListView list;
    public static ConfirmListViewAdapter adapter;
    public static int uploadedCount = 0;
    private static ProgressDialog progressDialog, pDialog;
    private static Handler threadHandler;
    public Button buttonContinue;
    private Libs libs;
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

        View addFragment = inflater.inflate(R.layout.fragment_confirm, container, false);

        list = (ListView) addFragment.findViewById(R.id.listViewConfirm);
        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        libs = new Libs(getActivity());

        progressDialog = new ProgressDialog(getActivity());

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (libs.isConnectingToInternet()) {
                    uploadDependentImages();
                } else libs.toast(2, 2, getString(R.string.warning_internet));
            }
        });

        setListView();

        return addFragment;
    }

    public void callSuccess(String jsonDocId) {

        SignupActivity.strCustomerPass = "";

        Config.jsonObject = Config.jsonServer;
        Config.jsonDocId = jsonDocId;
        Config.strUserName = SignupActivity.strCustomerEmail;

        Config.jsonServer = null;

        SignupActivity.strCustomerName = "";
        SignupActivity.strCustomerEmail = "";
        SignupActivity.strCustomerContactNo = "";
        SignupActivity.strCustomerAddress = "";
        SignupActivity.strCustomerImg = "";
        SignupActivity.strUserId = "";
        SignupActivity.dependentModels = null;
        SignupActivity.dependentNames = null;

        DependentDetailPersonalActivity.dependentModel = null;

        if (libs.isConnectingToInternet()) {

            UploadService uploadService = new UploadService(getActivity());

            uploadService.getAllFilesByUser(Config.strUserName, new App42CallBack() {
                public void onSuccess(Object response) {

                    Upload upload = (Upload) response;
                    ArrayList<Upload.File> fileList = upload.getFileList();

                    if (fileList.size() > 0) {

                        for (int i = 0; i < fileList.size(); i++) {
                            Config.fileModels.add(
                                    new FileModel(fileList.get(i).getName()
                                            , fileList.get(i).getUrl(),
                                            fileList.get(i).getType()));
                        }

                        Config.boolIsLoggedIn = true;
                        Intent dashboardIntent = new Intent(getActivity(), AccountSuccessActivity.class);
                        libs.parseData();

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        startActivity(dashboardIntent);
                        getActivity().finish();

                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.error_load_images));
                        logout();
                    }

                }

                public void onException(Exception ex) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    libs.toast(2, 2, getString(R.string.error));
                    Libs.log(ex.getMessage(), " 123 ");
                    logout();
                }
            });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.warning_internet));
            logout();
        }

    }

    public void logout() {
        Config.jsonObject = null;
        Config.jsonDocId = "";
        Config.strUserName = "";
        Libs.logout();
    }

    public void uploadDependentImages() {

        //int size = SignupActivity.dependentModels.size();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.uploading_profile_pic));
        /*pDialog.setIndeterminate(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setProgress(0);*/
        pDialog.setCancelable(false);
        //pDialog.setMax(size);
        pDialog.show();

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    public void uploadImage() {

        if (libs.isConnectingToInternet()) {

            UploadService uploadService = new UploadService(getActivity());

            uploadService.uploadImageCommon(SignupActivity.strCustomerImg, libs.replaceSpace(SignupActivity.strCustomerName), "Profile Picture", SignupActivity.strCustomerEmail, UploadFileType.IMAGE, new App42CallBack() {
                public void onSuccess(Object response) {

                    Upload upload = (Upload) response;
                    ArrayList<Upload.File> fileList = upload.getFileList();

                    if (fileList.size() > 0) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        strCustomerImageUrl = fileList.get(0).getUrl();
                        checkStorage();
                    } else {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        libs.toast(2, 2, ((Upload) response).getStrResponse());
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                        String strMess = jsonObjectError.getString("details");

                        libs.toast(2, 2, strMess);

                        //TODO call to checkstorage
                        //checkStorage();

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } else {
            if (pDialog.isShowing())
                pDialog.dismiss();
            libs.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void checkStorage() {

        try {

            if (libs.isConnectingToInternet()) {

                progressDialog.setMessage(getActivity().getResources().getString(R.string.uploading));
                progressDialog.setCancelable(false);
                progressDialog.show();

                StorageService storageService = new StorageService(getActivity());

                storageService.findDocsByKeyValue("customer_email", SignupActivity.strCustomerEmail, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    @Override
                    public void onDocumentInserted(Storage response) {

                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {

                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {

                        Libs.log(response.toString(), "");

                        if (response.getJsonDocList().size() <= 0)
                            uploadData();
                        else {
                            Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                            createUser(jsonDocument);
                        }

                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {

                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {

                        /*try {
                            if(ex!=null) {
                                JSONObject jsonObject = new JSONObject(ex.getMessage());
                                if(jsonObject.has("app42Fault")) {
                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");
                                    libs.toast(2, 2, strMess);
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }*/

                        Libs.log(ex.getMessage(), "");
                        uploadData();
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {

                    }
                });

            } else {
                libs.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error_register));
        }

    }

    public void uploadData() {

        boolean isRegistered = libs.prepareData(strCustomerImageUrl);

        if (libs.isConnectingToInternet()) {

            if (isRegistered) {

                StorageService storageService = new StorageService(getActivity());

                storageService.insertDocs(Config.jsonServer, new AsyncApp42ServiceApi.App42StorageServiceListener() {

                    @Override
                    public void onDocumentInserted(Storage response) {

                        if (response.isResponseSuccess()) {
                            Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                            createUser(jsonDocument);
                        }

                        Libs.log(response.toString(), "");
                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {

                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {

                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Libs.log(ex.getMessage(), "");
                        libs.toast(2, 2, getString(R.string.error_register));
                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {

                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {

                    }
                });
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                libs.toast(2, 2, getString(R.string.error_register));
            }
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void createUser(final Storage.JSONDocument jsonDocument) {

        if (libs.isConnectingToInternet()) {

            UserService userService = new UserService(getActivity());

            userService.onCreateUser(SignupActivity.strCustomerEmail, SignupActivity.strCustomerPass, SignupActivity.strCustomerEmail, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    callSuccess(jsonDocument.getDocId());
                }

                @Override
                public void onException(Exception e) {
                    Libs.log(e.getMessage(), "");
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    libs.toast(2, 2, getString(R.string.error_register));
                }
            });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void setListData() {

        int intCount = 0;

        if (SignupActivity.strUserId != null && !SignupActivity.strUserId.equalsIgnoreCase(""))
            intCount = libs.retrieveConfirmDependants();

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

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                int size = SignupActivity.dependentModels.size();

                for (int i = 0; i < size; i++) {

                    if (libs.isConnectingToInternet()) {

                        DependentModel dependentModel = SignupActivity.dependentModels.get(i);

                        final int progress = i;

                        UploadService uploadService = new UploadService(getActivity());

                        Libs.log(dependentModel.getStrImg() + String.valueOf(i), " ghy ");

                        uploadService.uploadImageCommon(dependentModel.getStrImg(),
                                libs.replaceSpace(dependentModel.getStrName()), "Profile Picture",
                                SignupActivity.strCustomerEmail,
                                UploadFileType.IMAGE, new App42CallBack() {

                                    public void onSuccess(Object response) {
                                        Upload upload = (Upload) response;

                                        Libs.log(response.toString(), " ghyl");
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {
                                            String strImagePath = fileList.get(0).getUrl();
                                            SignupActivity.dependentModels.get(progress)
                                                    .setStrImgServer(strImagePath);
                                            uploadedCount++;
                                        }
                                    }

                                    public void onException(Exception ex) {
                                        Libs.log(ex.getMessage() + " ~ ", " 1 ");
                                    }
                                });
                    }
                }

                Libs.log("Thread ", " Completed");

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Libs.log("ThreadHandler ", " Called");

            if (uploadedCount == SignupActivity.dependentModels.size()) {
                uploadImage();
            } else {
                pDialog.dismiss();
            }
        }
    }

}
