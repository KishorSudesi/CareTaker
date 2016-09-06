package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

public class PushNotificationService {

    private AsyncApp42ServiceApi asyncService;

    public PushNotificationService(Context context) {
        try {
            asyncService = AsyncApp42ServiceApi.instance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPushToUser(String strUserName, String strMessage, App42CallBack app42CallBack) {
        try {
            asyncService.sendPushToUser(strUserName, strMessage, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUserDevice(String strUserName, String strToken, App42CallBack app42CallBack) {
        try {
            asyncService.removeDevice(strUserName, strToken, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUserDevice(String strUserName, String strToken, App42CallBack app42CallBack) {
        try {
            asyncService.deleteDeviceToken(strUserName, strToken, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUserDevice(String strUserName, App42CallBack app42CallBack) {
        try {
            asyncService.deleteDevice(strUserName, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
