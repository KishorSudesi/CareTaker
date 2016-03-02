package com.hdfc.model;

/**
 * Created by Suhail on 2/19/2016.
 */
public class DependentNotificationModel {

    private String strNotificationMessage;
    private String strNotificationAuthor;
    private String strNotificationTime;
    private String strNotificationUrl;

    public DependentNotificationModel(String strNotificationMessage, String strNotificationAuthor, String strNotificationTime, String strNotificationUrl) {
        this.strNotificationMessage = strNotificationMessage;
        this.strNotificationAuthor = strNotificationAuthor;
        this.strNotificationTime = strNotificationTime;
        this.strNotificationUrl = strNotificationUrl;
    }

    public String getStrNotificationMessage() {
        return strNotificationMessage;
    }

    public String getStrNotificationAuthor() {
        return strNotificationAuthor;
    }

    public String getStrNotificationTime() {
        return strNotificationTime;
    }

    public String getStrNotificationUrl() {
        return strNotificationUrl;
    }
}
