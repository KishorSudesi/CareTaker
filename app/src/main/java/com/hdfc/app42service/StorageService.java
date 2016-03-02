package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONObject;

public class StorageService {

    private AsyncApp42ServiceApi asyncService;

    public StorageService(Context context) {
        asyncService = AsyncApp42ServiceApi.instance(context);
    }

    public void insertDocs(JSONObject jsonToSave, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.insertJSONDoc(Config.dbName, Config.collectionName, jsonToSave, app42CallBack);
    }

    /*public void findDocsByName(String checkValue) {
        asyncService.findDocByDocId(Config.dbName, Config.collectionName, checkValue, this);
    }*/

    public void findDocsById(String strDocId, String strCollectionName, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.findDocByDocId(Config.dbName, strCollectionName, strDocId, app42CallBack);
    }

    public void findDocsByIdApp42CallBack(String strDocId, String strCollectionName, App42CallBack app42CallBack) {
        asyncService.findDocByDocIdApp42CallBack(Config.dbName, strCollectionName, strDocId, app42CallBack);
    }

    public void findDocsByKeyValue(String strKey, String strValue, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.findDocumentByKeyValue(Config.dbName, Config.collectionName, strKey, strValue, app42CallBack);
    }

    /*public void updateDocs(JSONObject jsonToUpdate, String fieldName, String checkValue) {
        asyncService.updateDocByKeyValue(Config.dbName, Config.collectionName, fieldName, checkValue, jsonToUpdate, this);
    }*/

    public void updateDocs(JSONObject jsonToUpdate, String strDocId, String strCollectionName, App42CallBack app42CallBack) {
        asyncService.updateDocPartByKeyValue(Config.dbName, strCollectionName, strDocId, jsonToUpdate, app42CallBack);
    }
}
