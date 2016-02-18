package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

public class StorageService implements
        AsyncApp42ServiceApi.App42StorageServiceListener {

    private AsyncApp42ServiceApi asyncService;
    private Libs libs;

    public StorageService(Context context) {
        asyncService = AsyncApp42ServiceApi.instance(context);
        libs = new Libs(context);
    }

    public void insertDocs(JSONObject jsonToSave, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.insertJSONDoc(Config.dbName, Config.collectionName, jsonToSave, app42CallBack);
    }

    public void findDocsByName(String checkValue) {
        asyncService.findDocByDocId(Config.dbName, Config.collectionName, checkValue, this);
    }

    public void findDocsByKeyValue(String strKey, String strValue, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.findDocumentByKeyValue(Config.dbName, Config.collectionName, strKey, strValue, app42CallBack);
    }

    /*public void updateDocs(JSONObject jsonToUpdate, String fieldName, String checkValue) {
        asyncService.updateDocByKeyValue(Config.dbName, Config.collectionName, fieldName, checkValue, jsonToUpdate, this);
    }*/

    @Override
    public void onDocumentInserted(Storage response) {
        String getJson = response.getJsonDocList().get(0).getJsonDoc();
        try {
            JSONObject json = new JSONObject(getJson);
            //libs.createAlertDialog("Document Saved "+ json.get("Name"));
            // docId = response.getJsonDocList().get(0).getDocId();
        } catch (JSONException ex) {
            //Libs.toast(2, 2, "Error : " + ex.getMessage());
        }
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        //Libs.toast(2, 2, "Error : " + ex.getMessage());
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        libs.createAlertDialog("Error : " + ex.getMessage());
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        libs.createAlertDialog("Document Found : " + response.getJsonDocList().get(0).getJsonDoc());
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        libs.createAlertDialog("Document Updated : " + response.getJsonDocList().get(0).getJsonDoc());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        libs.createAlertDialog("Error : " + ex.getMessage());
    }
}
