package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;

import org.json.JSONObject;

import java.util.HashMap;

public class StorageService {

    private AsyncApp42ServiceApi asyncService;

    public StorageService(Context context) {
        asyncService = AsyncApp42ServiceApi.instance(context);
    }

    public void setOtherMetaHeaders(HashMap<String, String> otherMetaHeaders) {
        asyncService.setOtherMetaHeaders(otherMetaHeaders);
    }

    public void insertDocs(JSONObject jsonToSave, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack, String collectionCustomer) {
        asyncService.insertJSONDoc(Config.dbName, collectionCustomer, jsonToSave, app42CallBack);
    }

    /*public void findDocsByName(String checkValue) {
        asyncService.findDocByDocId(Config.dbName, Config.collectionCustomer, checkValue, this);
    }*/

    public void findDocsById(String strDocId, String strCollectionName, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.findDocByDocId(Config.dbName, strCollectionName, strDocId, app42CallBack);
    }

    public void findDocsByIdApp42CallBack(String strDocId, String strCollectionName, App42CallBack app42CallBack) {
        asyncService.findDocByDocIdApp42CallBack(Config.dbName, strCollectionName, strDocId, app42CallBack);
    }

    public void findDocsByKeyValue(String strCollectionName, String strKey, String strValue, AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        asyncService.findDocumentByKeyValue(Config.dbName, strCollectionName, strKey, strValue, app42CallBack);
    }

    public void findDocsByQuery(String strCollectionName, Query query, int max, int offset, App42CallBack app42CallBack) {
        asyncService.findDocumentByQueryPaging(Config.dbName, strCollectionName, query, max, offset, app42CallBack);
    }

    public void findAllDocs(String strCollectionName, App42CallBack app42CallBack) {
        asyncService.findAllDocuments(Config.dbName, strCollectionName, app42CallBack);
    }

    /*public void updateDocs(JSONObject jsonToUpdate, String fieldName, String checkValue) {
        asyncService.updateDocByKeyValue(Config.dbName, Config.collectionCustomer, fieldName, checkValue, jsonToUpdate, this);
    }*/

    public void updateDocs(JSONObject jsonToUpdate, String strDocId, String strCollectionName, App42CallBack app42CallBack) {
        asyncService.updateDocPartByKeyValue(Config.dbName, strCollectionName, strDocId, jsonToUpdate, app42CallBack);
    }
}
