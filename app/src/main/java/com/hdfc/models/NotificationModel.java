package com.hdfc.models;

/**
 * Created by Admin on 19-02-2016.
 */
public class NotificationModel {

    private String strMessage = "";
    private String strDateTime = "";
    private String strUserType;
    private String strCreatedByType;

    private int iUserID;
    private int iCreatedByID;
    private int iNotificationID;

    public NotificationModel(String strMessage, String strDateTime, String strUserType,
                             String strCreatedByType, int iUserID, int iCreatedByID,
                             int iNotificationID) {
        this.strMessage = strMessage;
        this.strDateTime = strDateTime;
        this.strUserType = strUserType;
        this.strCreatedByType = strCreatedByType;
        this.iUserID = iUserID;
        this.iCreatedByID = iCreatedByID;
        this.iNotificationID = iNotificationID;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public String getStrUserType() {
        return strUserType;
    }

    public String getStrCreatedByType() {
        return strCreatedByType;
    }

    public int getiUserID() {
        return iUserID;
    }

    public int getiCreatedByID() {
        return iCreatedByID;
    }

    public int getiNotificationID() {
        return iNotificationID;
    }
}
