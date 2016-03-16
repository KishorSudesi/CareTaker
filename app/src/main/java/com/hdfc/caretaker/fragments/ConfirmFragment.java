package com.hdfc.caretaker.fragments;


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
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.models.ConfirmViewModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FileModel;
import com.hdfc.caretaker.AccountSuccessActivity;
import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONArray;
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
    public static int uploadSize, uploadingCount=0;
    private static ProgressDialog progressDialog, pDialog;
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

                    uploadSize = SignupActivity.dependentModels.size();

                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage(getResources().getString(R.string.uploading_profile_pic));
                    pDialog.setIndeterminate(false);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setProgress(0);
                    pDialog.setCancelable(true);
                    pDialog.setMax(uploadSize);
                    pDialog.show();

                    uploadingCount=0;

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

        if(!jsonDocId.equalsIgnoreCase(""))
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

            progressDialog.setMessage(getActivity().getResources().getString(R.string.process_login));

            UploadService uploadService = new UploadService(getActivity());

            uploadService.getAllFilesByUser(Config.strUserName, new App42CallBack() {
                public void onSuccess(Object response) {

                    if(response!=null) {
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
                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.warning_internet));
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

        try {

            if(uploadingCount < uploadSize) {

                if (libs.isConnectingToInternet()) {

                    DependentModel dependentModel = SignupActivity.dependentModels.get(uploadingCount);

                    final int progress = uploadingCount;

                    if (pDialog.isShowing())
                        pDialog.setProgress(uploadingCount);

                    UploadService uploadService = new UploadService(getActivity());

                    if (!dependentModel.getStrName().equalsIgnoreCase(
                            getActivity().getResources().getString(R.string.add_dependent))) {

                        uploadService.uploadImageCommon(dependentModel.getStrImg(),
                                libs.replaceSpace(dependentModel.getStrName()), "Profile Picture",
                                SignupActivity.strCustomerEmail,
                                UploadFileType.IMAGE, new App42CallBack() {

                                    public void onSuccess(Object response) {

                                        if (response != null) {

                                            Upload upload = (Upload) response;
                                            ArrayList<Upload.File> fileList = upload.getFileList();

                                            if (fileList.size() > 0) {
                                                String strImagePath = fileList.get(0).getUrl();
                                                SignupActivity.dependentModels.get(progress)
                                                        .setStrImgServer(strImagePath);
                                                uploadingCount++;
                                            }
                                            if (uploadingCount == uploadSize) {
                                                uploadImage();
                                            } else uploadDependentImages();

                                        } else {
                                            if (pDialog.isShowing())
                                                pDialog.dismiss();
                                            libs.toast(2, 2, getString(R.string.warning_internet));
                                        }
                                    }

                                    public void onException(Exception ex) {

                                        if (ex != null) {

                                            App42Exception exception = (App42Exception) ex;
                                            int appErrorCode = exception.getAppErrorCode();

                                            if (appErrorCode == 2100) {
                                                uploadingCount++;
                                            }

                                            if (uploadingCount == uploadSize) {
                                                uploadImage();
                                            } else uploadDependentImages();

                                        } else {
                                            if (pDialog.isShowing())
                                                pDialog.dismiss();
                                            libs.toast(2, 2, getString(R.string.warning_internet));
                                        }
                                    }
                                });

                    } else{
                        uploadingCount++;
                        uploadDependentImages();
                    }

                } else {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    uploadSize = uploadingCount;
                    uploadingCount=0;
                    libs.toast(2, 2, getString(R.string.warning_internet));
                }
            }

        } catch (Exception e) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error));
            e.printStackTrace();
        }
    }

    public void uploadImage() {

        try {

            if (libs.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(getActivity());

                if (pDialog.isShowing())
                    pDialog.setProgress(uploadSize+1);

                uploadService.uploadImageCommon(SignupActivity.strCustomerImg, Config.strCustomerImageName, "Profile Picture", SignupActivity.strCustomerEmail, UploadFileType.IMAGE, new App42CallBack() {
                    public void onSuccess(Object response) {

                        if(response!=null) {
                            Upload upload = (Upload) response;
                            ArrayList<Upload.File> fileList = upload.getFileList();

                            if (fileList.size() > 0) {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                strCustomerImageUrl = fileList.get(0).getUrl();
                                checkStorage();
                            } else {
                                libs.toast(2, 2, ((Upload) response).getStrResponse());
                                uploadImage();
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onException(Exception e) {

                        if(e!=null) {
                            App42Exception exception = (App42Exception) e;
                            int appErrorCode = exception.getAppErrorCode();

                            if (appErrorCode == 2100) {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                checkStorage();
                            } else {
                                libs.toast(2, 2, getString(R.string.error));
                                uploadImage();
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }
                });
            } else {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                libs.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (pDialog.isShowing())
                pDialog.dismiss();
            libs.toast(2, 2, getString(R.string.error));
        }
    }

    public void checkStorage() {

        try {

            if (libs.isConnectingToInternet()) {

                progressDialog.setMessage(getActivity().getResources().getString(R.string.uploading));
                progressDialog.setCancelable(true);
                progressDialog.show();

                StorageService storageService = new StorageService(getActivity());

                storageService.findDocsByKeyValue(Config.collectionName, "customer_email", SignupActivity.strCustomerEmail, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    @Override
                    public void onDocumentInserted(Storage response) {

                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {

                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {

                        if(response!=null) {

                            Libs.log(response.toString(), "");

                            if (response.getJsonDocList().size() <= 0)
                                uploadData();
                            else {
                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                createUser(jsonDocument.getDocId());
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }

                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {

                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {

                        if(ex!=null) {

                            int appErrorCode = ex.getAppErrorCode();

                            if (appErrorCode == 2601)
                                uploadData();
                            else{
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, ex.getMessage());
                            }
                        }else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }
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
            libs.toast(2, 2, getString(R.string.error));
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

                        if (response != null){
                            Libs.log(response.toString(), "");
                            if (response.isResponseSuccess()) {
                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                createUser(jsonDocument.getDocId());
                            }
                        }else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
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
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        if(ex!=null){
                            //int appErrorCode = ex.getAppErrorCode();
                            Libs.log(ex.getMessage(), "");
                            libs.toast(2, 2, getString(R.string.error_register));
                        }else{
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }
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

    public void createUser(final String jsonDocId) {

        if (libs.isConnectingToInternet()) {

            UserService userService = new UserService(getActivity());

            userService.onCreateUser(SignupActivity.strCustomerEmail, SignupActivity.strCustomerPass, SignupActivity.strCustomerEmail, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if(o!=null)
                        assignToDependent(jsonDocId);
                    else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {

                    if(e!=null){
                        Libs.log(e.getMessage(), "");

                        int appErrorCode = ((App42Exception)e).getAppErrorCode();

                        if (appErrorCode == 2005)
                            assignToDependent(jsonDocId);
                        else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.error));
                        }

                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            libs.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void assignToDependent(final String jsonDocId){

        if (libs.isConnectingToInternet()) {

            final StorageService storageService = new StorageService(getActivity());

            storageService.findDocsByKeyValue(Config.collectionNameProviders, "provider_email", "carla1@gmail.com", new AsyncApp42ServiceApi.App42StorageServiceListener() {
                @Override
                public void onDocumentInserted(Storage response) {
                }

                @Override
                public void onUpdateDocSuccess(Storage response) {
                }

                @Override
                public void onFindDocSuccess(Storage response) {

                    if(response!=null) {

                        if (response.getJsonDocList().size() > 0) {

                            Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                            final String strCarlaJsonId = response.getJsonDocList().get(0).getDocId();

                            String strDocument = jsonDocument.getJsonDoc();

                            try {
                                final JSONObject responseJSONDocCarla = new JSONObject(strDocument);

                                if (responseJSONDocCarla.has("dependents")) {
                                    JSONArray dependantsA = responseJSONDocCarla.getJSONArray("dependents");

                                    //
                                    int intCount = SignupActivity.dependentModels.size();

                                    for (int cursorIndex = 0; cursorIndex < intCount; cursorIndex++) {

                                        try {

                                            DependentModel dependentModel = SignupActivity.dependentModels.get(cursorIndex);

                                            if(!dependentModel.getStrName().equalsIgnoreCase(getActivity().getResources().getString(R.string.add_dependent))) {

                                                JSONObject jsonDependant = new JSONObject();
                                                jsonDependant.put("dependent_name", dependentModel.getStrName());
                                                jsonDependant.put("dependent_illness", dependentModel.getStrIllness());
                                                jsonDependant.put("dependent_address", dependentModel.getStrAddress());
                                                jsonDependant.put("dependent_email", dependentModel.getStrEmail());

                                                jsonDependant.put("dependent_notes", dependentModel.getStrDesc());
                                                jsonDependant.put("dependent_age", dependentModel.getIntAge());
                                                jsonDependant.put("dependent_contact_no", dependentModel.getStrContacts());

                                                jsonDependant.put("dependent_profile_url", dependentModel.getStrImgServer());
                                                jsonDependant.put("customer_email", SignupActivity.strCustomerEmail);

                                                dependantsA.put(jsonDependant);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                //
                                if (libs.isConnectingToInternet()) {
                                    storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionNameProviders, new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {

                                            if (o != null) {
                                               /* if(progressDialog.isShowing())
                                                    progressDialog.dismiss();*/

                                                callSuccess(jsonDocId);

                                            }else {
                                                if(progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                libs.toast(2, 2, getString(R.string.warning_internet));
                                            }
                                        }

                                        @Override
                                        public void onException(Exception e) {
                                            if(progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            if(e!=null) {
                                                libs.toast(2, 2, e.getMessage());
                                            }else{
                                                libs.toast(2, 2, getString(R.string.warning_internet));
                                            }
                                        }
                                    });

                                }else{
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (JSONException e) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, getString(R.string.error));
                                e.printStackTrace();
                            }
                        }

                    }else{
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

                    if(ex!=null) {
                        libs.toast(2, 2, ex.getMessage());
                    }else{
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onUpdateDocFailed(App42Exception ex) {
                }
            });

        }else {
            if(progressDialog.isShowing())
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
}
