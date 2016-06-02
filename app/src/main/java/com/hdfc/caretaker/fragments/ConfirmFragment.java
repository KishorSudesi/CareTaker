package com.hdfc.caretaker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.hdfc.adapters.ConfirmListAdapter;
import com.hdfc.adapters.ConfirmListViewAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.AccountSuccessActivity;
import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ClientModel;
import com.hdfc.models.ConfirmCustomerModel;
import com.hdfc.models.ConfirmDependentModel;
import com.hdfc.models.ConfirmViewModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfirmFragment extends Fragment {

    public static ExpandableListView expListView;
    //private static List<ConfirmCustomerModel> listDataHeader = new ArrayList<>();
    private static List<CustomerModel> listDataHeader = new ArrayList<>();
    //private static HashMap<ConfirmCustomerModel, List<ConfirmDependentModel>> listDataChild = new HashMap<>();
    private static HashMap<CustomerModel, List<DependentModel>> listDataChild = new HashMap<>();

    public static ArrayList<ConfirmViewModel> CustomListViewValuesArr = new ArrayList<>();
    //public static ConfirmListViewAdapter adapter;
    public static ConfirmListAdapter adapter;
    public static int uploadSize, uploadingCount=0;
    private static ProgressDialog progressDialog, pDialog;
    private static String jsonDocId;
    public Button buttonContinue;
    private Utils utils;
    private String strCustomerImageUrl = "";

    private int iDependentCount = 0;

    public ConfirmFragment() {
    }

    public static ConfirmFragment newInstance() {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addFragment = inflater.inflate(R.layout.fragment_confirm, container, false);

        buttonContinue = (Button) addFragment.findViewById(R.id.buttonContinue);

        utils = new Utils(getActivity());

        expListView = (ExpandableListView)addFragment.findViewById(R.id.expList);

        progressDialog = new ProgressDialog(getActivity());

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utils.isConnectingToInternet()) {

                    uploadSize = SignupActivity.dependentModels.size();

                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage(getResources().getString(R.string.uploading_profile_pic));
                    pDialog.setIndeterminate(false);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setProgress(0);
                    pDialog.setCancelable(false);
                    pDialog.setMax(uploadSize);
                    pDialog.show();

                    uploadingCount = 0;

                    uploadDependentImages();

                } else utils.toast(2, 2, getString(R.string.warning_internet));
            }
        });

        //adapter = new ConfirmListAdapter();

        prepareListData();

        setListView();

        return addFragment;
    }

    public void callSuccess() {

        //remove "Add Dependent"

        for (int i = 0; i < SignupActivity.dependentModels.size(); i++) {
            if (SignupActivity.dependentModels.get(i).getStrName().
                    equalsIgnoreCase(getActivity().
                            getResources().
                            getString(R.string.add_dependent))) {
                SignupActivity.dependentModels.remove(i);
                break;
            }
        }

        if (progressDialog.isShowing())
            progressDialog.dismiss();
        utils.toast(2, 2, getString(R.string.register_success));

        Intent intent = new Intent(getActivity(), AccountSuccessActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

//        if (expListView != null) {

         /*   listDataHeader.clear();
            listDataChild.clear();

            for (ClientModel clientModel : Config.clientModels) {*/
        listDataHeader.add(Config.customerModel);

        ArrayList<DependentModel> dependentModels = new ArrayList<>();

        for(DependentModel dependentModel : SignupActivity.dependentModels){

            if(!dependentModel.getStrName().equalsIgnoreCase(getString(R.string.add_dependent))){
                dependentModels.add(dependentModel);
            }
        }

        listDataChild.put(Config.customerModel, dependentModels);
    }

    public void uploadDependentImages() {

        try {

            if(uploadingCount < uploadSize) {

                if (utils.isConnectingToInternet()) {

                    DependentModel dependentModel = SignupActivity.dependentModels.
                            get(uploadingCount);

                    final int progress = uploadingCount;

                    if (pDialog.isShowing())
                        pDialog.setProgress(uploadingCount);

                    UploadService uploadService = new UploadService(getActivity());

                    if (!dependentModel.getStrName().equalsIgnoreCase(
                            getActivity().getResources().getString(R.string.add_dependent))) {

                        if (dependentModel.getStrImagePath() != null &&
                                !dependentModel.getStrImagePath().equalsIgnoreCase("")) {

                            uploadService.uploadImageCommon(dependentModel.getStrImagePath(),
                                    utils.replaceSpace(dependentModel.getStrName()), "Profile Picture",
                                    dependentModel.getStrEmail(),
                                    UploadFileType.IMAGE, new App42CallBack() {

                                        public void onSuccess(Object response) {

                                            if (response != null) {

                                                Upload upload = (Upload) response;
                                                ArrayList<Upload.File> fileList = upload.getFileList();

                                                if (fileList.size() > 0) {
                                                    String strImagePath = fileList.get(0).getUrl();
                                                    SignupActivity.dependentModels.get(progress)
                                                            .setStrImageUrl(strImagePath);
                                                    uploadingCount++;
                                                }
                                                if (uploadingCount == uploadSize) {
                                                    uploadImage();
                                                } else uploadDependentImages();

                                            } else {
                                                if (pDialog.isShowing())
                                                    pDialog.dismiss();
                                                utils.toast(2, 2, getString(R.string.warning_internet));
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
                                                utils.toast(2, 2, getString(R.string.warning_internet));
                                            }
                                        }
                                    });

                        } else {
                            uploadingCount++;
                            uploadDependentImages();
                        }
                    } else {
                        uploadingCount++;
                        uploadDependentImages();
                    }

                } else {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    uploadSize = uploadingCount;
                    uploadingCount=0;
                    utils.toast(2, 2, getString(R.string.warning_internet));
                }
            } else {
                if (uploadingCount == uploadSize)
                    uploadImage();
            }

        } catch (Exception e) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
            e.printStackTrace();
        }
    }

    public void uploadImage() {

        try {

            if (utils.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(getActivity());

                if (pDialog.isShowing())
                    pDialog.setProgress(uploadSize+1);

                uploadService.uploadImageCommon(Config.customerModel.getStrImgPath(),
                        Config.strCustomerImageName, "Profile Picture",
                        Config.customerModel.getStrEmail(),
                        UploadFileType.IMAGE, new App42CallBack() {
                    public void onSuccess(Object response) {

                        if(response!=null) {
                            Upload upload = (Upload) response;
                            ArrayList<Upload.File> fileList = upload.getFileList();

                            if (fileList.size() > 0) {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                strCustomerImageUrl = fileList.get(0).getUrl();
                                Config.customerModel.setStrImgUrl(strCustomerImageUrl);
                                checkStorage();
                            } else {
                                utils.toast(2, 2, ((Upload) response).getStrResponse());
                                uploadImage();
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
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
                                utils.toast(2, 2, getString(R.string.error));
                                uploadImage();
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }
                });
            } else {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (pDialog.isShowing())
                pDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void checkStorage() {

        try {

            if (utils.isConnectingToInternet()) {

                progressDialog.setMessage(getActivity().getResources().getString(R.string.uploading));
                progressDialog.setCancelable(false);
                progressDialog.show();

                StorageService storageService = new StorageService(getActivity());

                storageService.findDocsByKeyValue(Config.collectionCustomer, "customer_email",
                        Config.customerModel.getStrEmail(),
                        new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    @Override
                    public void onDocumentInserted(Storage response) {
                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {
                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {

                        if(response!=null) {

                            Utils.log(response.toString(), "");

                            if (response.getJsonDocList().size() <= 0)
                                uploadData();
                            else {
                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                jsonDocId = jsonDocument.getDocId();
                                createUser();
                            }
                        }else{
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
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
                                utils.toast(2, 2, ex.getMessage());
                            }
                        }else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                });

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public boolean prepareData(String strCustomerImageUrl) {

        boolean isFormed;

        try {

            Config.jsonCustomer = new JSONObject();

            Config.jsonCustomer.put("customer_name", Config.customerModel.getStrName());
            Config.jsonCustomer.put("customer_address", Config.customerModel.getStrCountryCode());//
            Config.jsonCustomer.put("customer_city", "");
            Config.jsonCustomer.put("customer_state", "");
            Config.jsonCustomer.put("customer_contact_no", Config.customerModel.getStrContacts());
            Config.jsonCustomer.put("customer_email", Config.customerModel.getStrEmail());
            Config.jsonCustomer.put("customer_profile_url", strCustomerImageUrl);
            Config.jsonCustomer.put("paytm_account", "paytm_account");

            Config.jsonCustomer.put("customer_dob", Config.customerModel.getStrDob());
            Config.jsonCustomer.put("customer_country", Config.customerModel.getStrCountryCode());
            Config.jsonCustomer.put("customer_country_code", Config.customerModel.getStrCountryIsdCode());
            Config.jsonCustomer.put("customer_area_code", Config.customerModel.getStrCountryAreaCode());


            isFormed = true;

        } catch (JSONException e) {
            e.printStackTrace();
            isFormed = false;
        }

        return isFormed;
    }


    public void uploadData() {

        boolean isRegistered = prepareData(strCustomerImageUrl);

        if (utils.isConnectingToInternet()) {

            if (isRegistered) {

                StorageService storageService = new StorageService(getActivity());

                storageService.insertDocs(Config.jsonCustomer,
                        new AsyncApp42ServiceApi.App42StorageServiceListener() {

                    @Override
                    public void onDocumentInserted(Storage response) {

                        if (response != null){
                            Utils.log(response.toString(), "");
                            if (response.isResponseSuccess()) {
                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);
                                jsonDocId = jsonDocument.getDocId();
                                createUser();
                            }
                        }else{
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
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
                            Utils.log(ex.getMessage(), "");
                            utils.toast(2, 2, getString(R.string.error_register));
                        }else{
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                        }, Config.collectionCustomer);
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.error_register));
            }
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void createUser() {

        if (utils.isConnectingToInternet()) {

            UserService userService = new UserService(getActivity());

            ArrayList<String> roleList = new ArrayList<>();
            roleList.add("customer");

            userService.onCreateUser(Config.customerModel.getStrEmail(),
                    SignupActivity.strCustomerPass, Config.customerModel.getStrEmail(),
                    new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if (o != null) {
                        createDependent();
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {

                    if(e!=null){
                        Utils.log(e.getMessage(), "");

                        int appErrorCode = ((App42Exception)e).getAppErrorCode();

                        if (appErrorCode == 2005 || appErrorCode == 2001) {
                            createDependent();
                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.error));
                        }

                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
                    }, roleList);

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    public void createDependent() {

        int intCount = SignupActivity.dependentModels.size();

        Config.customerModel.setStrCustomerID(jsonDocId);

        if (iDependentCount < intCount) {

            try {

                DependentModel dependentModel = SignupActivity.
                        dependentModels.get(iDependentCount);

                if (!dependentModel.getStrName().
                        equalsIgnoreCase(getActivity().
                                getResources().
                                getString(R.string.add_dependent))) {

                    final int iSelectedDependent = iDependentCount;

                    JSONObject jsonDependant = new JSONObject();
                    jsonDependant.put("dependent_name", dependentModel.getStrName());

                    if (dependentModel.getStrIllness() == null || dependentModel.getStrIllness().equalsIgnoreCase(""))
                        dependentModel.setStrIllness("NA");

                    if (dependentModel.getStrNotes() == null || dependentModel.getStrNotes().equalsIgnoreCase(""))
                        dependentModel.setStrNotes("NA");

                    jsonDependant.put("dependent_illness", dependentModel.getStrIllness());

                    jsonDependant.put("dependent_address", dependentModel.getStrAddress());
                    jsonDependant.put("dependent_email", dependentModel.getStrEmail());

                    jsonDependant.put("dependent_notes", dependentModel.getStrNotes());
                    jsonDependant.put("dependent_age", dependentModel.getIntAge());
                    jsonDependant.put("dependent_dob", dependentModel.getStrDob());
                    jsonDependant.put("dependent_contact_no", dependentModel.getStrContacts());

                    jsonDependant.put("dependent_profile_url", dependentModel.getStrImageUrl());
                    jsonDependant.put("dependent_relation", dependentModel.getStrRelation());
                    jsonDependant.put("customer_id", jsonDocId);

                    jsonDependant.put("health_bp", 70 + iDependentCount);

                    Config.dependentNames.add(dependentModel.getStrName());

                    jsonDependant.put("health_heart_rate", 80 + iDependentCount);

                    SignupActivity.dependentModels.get(iDependentCount).setIntHealthBp(70 +
                            iDependentCount);
                    SignupActivity.dependentModels.get(iDependentCount).
                            setIntHealthHeartRate(80 + iDependentCount);
                    SignupActivity.dependentModels.get(iDependentCount).setStrCustomerID(jsonDocId);

                    //jsonDependant.put("services", new JSONArray());

                    if (utils.isConnectingToInternet()) {

                        StorageService storageService = new StorageService(getActivity());

                        final String strDependentEmail = dependentModel.getStrEmail();
                        final JSONObject object = jsonDependant;

                        storageService.findDocsByKeyValue(Config.collectionDependent,
                                "dependent_email",
                                strDependentEmail,
                                new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                    @Override
                                    public void onDocumentInserted(Storage response) {
                                    }

                                    @Override
                                    public void onUpdateDocSuccess(Storage response) {
                                    }

                                    @Override
                                    public void onFindDocSuccess(Storage response) {

                                        if (response != null) {

                                            if (response.getJsonDocList().size() <= 0) {

                                                insertDependent(strDependentEmail, object, iSelectedDependent);
                                            } else {

                                                //
                                                Storage.JSONDocument jsonDocument = response.
                                                        getJsonDocList().
                                                        get(0);

                                                String strDependentDocId = jsonDocument.getDocId();

                                                SignupActivity.dependentModels.
                                                        get(iSelectedDependent).
                                                        setStrDependentID(strDependentDocId);

                                                if (!Config.strDependentIds.contains(strDependentDocId))
                                                    Config.strDependentIds.add(strDependentDocId);
                                                //

                                                createDependentUser(strDependentEmail);
                                            }
                                        } else {
                                            if (pDialog.isShowing())
                                                pDialog.dismiss();
                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                    }

                                    }

                                    @Override
                                    public void onInsertionFailed(App42Exception ex) {
                                    }

                                    @Override
                                    public void onFindDocFailed(App42Exception ex) {

                                        if (ex != null) {
                                            int appErrorCode = ex.getAppErrorCode();

                                            if (appErrorCode == 2601) {
                                                insertDependent(strDependentEmail, object,
                                                        iSelectedDependent);
                                            } else {
                                                createDependentUser(strDependentEmail);
                                            }
                                        } else {
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            utils.toast(2, 2, getString(R.string.warning_internet));
                                        }
                                    }

                                    @Override
                                    public void onUpdateDocFailed(App42Exception ex) {
                                    }
                                });

                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                } else {
                    iDependentCount++;
                    createDependent();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.error));
            }
        }
    }

    public void insertDependent(final String strDependentEmail, JSONObject jsonObject,
                                final int iSelectedDependent) {
        try {

            if (utils.isConnectingToInternet()) {

                StorageService storageService = new StorageService(getActivity());

                storageService.insertDocs(jsonObject,
                        new AsyncApp42ServiceApi.App42StorageServiceListener() {

                            @Override
                            public void onDocumentInserted(Storage response) {

                                if (response != null) {
                                    if (response.isResponseSuccess()) {
                                        //
                                        Storage.JSONDocument jsonDocument = response.
                                                getJsonDocList().
                                                get(0);

                                        String strDependentDocId = jsonDocument.getDocId();

                                        SignupActivity.dependentModels.
                                                get(iSelectedDependent).
                                                setStrDependentID(strDependentDocId);

                                        if (!Config.strDependentIds.contains(strDependentDocId))
                                            Config.strDependentIds.add(strDependentDocId);
                                        //

                                        createDependentUser(strDependentEmail);
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
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

                                if (ex != null) {
                                    Utils.log(ex.getMessage(), "");
                                    utils.toast(2, 2, getString(R.string.error_register));
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                            }

                            @Override
                            public void onFindDocFailed(App42Exception ex) {
                            }

                            @Override
                            public void onUpdateDocFailed(App42Exception ex) {
                            }
                        }, Config.collectionDependent);
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void createDependentUser(final String strDependentEmail) {

        try {

            if (utils.isConnectingToInternet()) {

                UserService userService = new UserService(getActivity());

                ArrayList<String> roleList = new ArrayList<>();
                roleList.add("dependent");

                Utils.log(" 2 ", " IN 0");

                userService.onCreateUser(strDependentEmail,
                        //todo generate random password and send mail
                        SignupActivity.strCustomerPass, strDependentEmail,
                        new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {

                                    iDependentCount++;

                                    if (SignupActivity.dependentModels.size() == iDependentCount)
                                        callSuccess();
                                    else
                                        createDependent();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                            }

                            @Override
                            public void onException(Exception e) {

                                if (e != null) {
                                    Utils.log(e.getMessage(), "");

                                    int appErrorCode = ((App42Exception) e).getAppErrorCode();

                                    if (appErrorCode == 2005 || appErrorCode == 2001) {

                                        iDependentCount++;

                                        if (SignupActivity.dependentModels.size() == iDependentCount)
                                            callSuccess();
                                        else
                                            createDependent();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                            }
                        }, roleList);

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void setListData() {

        int intCount = 0;

        if (Config.customerModel != null && Config.customerModel.getStrName() != null
                && !Config.customerModel.getStrName().equalsIgnoreCase(""))
            intCount = utils.retrieveConfirmDependants();

        if (intCount > 1)
            buttonContinue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setListView() {
        try {
            /*adapter = new ConfirmListAdapter(getContext(),
                    ConfirmFragment.CustomListViewValuesArr);*/
            setListData();

            adapter = new ConfirmListAdapter(getContext(),listDataHeader,listDataChild);
            expListView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }}
}
