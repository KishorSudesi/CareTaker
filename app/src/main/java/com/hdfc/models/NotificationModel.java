package com.hdfc.models;

/**
 * Created by Admin on 19-02-2016.
 */
public class NotificationModel {

    private String strCarlaImagePath = "";
    private String strMessage = "";
    private String strDateTime = "";
    private String strAuthor = "";

    public NotificationModel(String strCarlaImagePath, String strMessage, String strDateTime, String strAuthor) {
        this.strCarlaImagePath = strCarlaImagePath;
        this.strMessage = strMessage;
        this.strDateTime = strDateTime;
        this.strAuthor = strAuthor;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public String getStrAuthor() {
        return strAuthor;
    }

    public void setStrAuthor(String strAuthor) {
        this.strAuthor = strAuthor;
    }

    public String getStrCarlaImagePath() {
        return strCarlaImagePath;
    }

    public void setStrCarlaImagePath(String strCarlaImagePath) {
        this.strCarlaImagePath = strCarlaImagePath;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }

}
