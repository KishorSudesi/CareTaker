package com.hdfc.models;

/**
 * Created by Suhail on 2/17/2016.
 */
public class ActivitiesModel {

    private String strCarlaImagePath = "";
    private String strActivityName = "";
    private String strDateTime = "";

    private String strActivityFeedback = "";
    private String strAuthor = "";

    public ActivitiesModel(String strCarlaImagePath, String strActivityName, String strDateTime, String strActivityFeedback, String strAuthor) {
        this.strCarlaImagePath = strCarlaImagePath;
        this.strActivityName = strActivityName;
        this.strDateTime = strDateTime;
        this.strActivityFeedback = strActivityFeedback;
        this.strAuthor = strAuthor;
    }

    public String getStrActivityFeedback() {
        return strActivityFeedback;
    }

    public void setStrActivityFeedback(String strActivityFeedback) {
        this.strActivityFeedback = strActivityFeedback;
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

    public String getStrActivityName() {
        return strActivityName;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }
}
