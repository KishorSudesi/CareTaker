package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ActivityModel implements Serializable {

    private String strActivityName;
    private String strActivityMessage;
    private String strAtivityProvider;
    private String strActivityDate;
    private String strActivityStatus;

    private String strActivityProviderEmail;
    private String strActivityProviderContactNo;
    private String strActivityProviderDesc;
    private String strActivityProviderName;

    private String strProviderImageUrl;

    private ArrayList<ImageModel> imageModels = new ArrayList<>();
    private ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();
    private ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();

    public ActivityModel() {
    }

    public ActivityModel(String strActivityName, String strActivityMessage,
                         String strAtivityProvider, String strActivityDate, String strActivityStatus,
                         String strActivityProviderEmail, String strActivityProviderContactNo,
                         String strActivityProviderDesc, String strActivityProviderName,
                         ArrayList<ImageModel> imageModels,
                         ArrayList<ActivityVideoModel> activityVideoModels,
                         ArrayList<FeedBackModel> feedBackModels,
                         String strProviderImageUrl) {
        this.strActivityName = strActivityName;
        this.strActivityMessage = strActivityMessage;
        this.strAtivityProvider = strAtivityProvider;
        this.strActivityDate = strActivityDate;
        this.strActivityStatus = strActivityStatus;
        this.strActivityProviderEmail = strActivityProviderEmail;
        this.strActivityProviderContactNo = strActivityProviderContactNo;
        this.strActivityProviderDesc = strActivityProviderDesc;
        this.strActivityProviderName = strActivityProviderName;
        this.imageModels = imageModels;
        this.activityVideoModels = activityVideoModels;
        this.feedBackModels = feedBackModels;
        this.strProviderImageUrl = strProviderImageUrl;
    }

    public ActivityModel(String strActivityName, String strActivityMessage,
                         String strAtivityProvider, String strActivityDate, String strActivityStatus,
                         String strActivityProviderEmail, String strActivityProviderContactNo,
                         String strActivityProviderDesc, String strActivityProviderName,
                         ArrayList<ActivityVideoModel> activityVideoModels,
                         ArrayList<FeedBackModel> feedBackModels,
                         ArrayList<ImageModel> imageModels) {
        this.strActivityName = strActivityName;
        this.strActivityMessage = strActivityMessage;
        this.strAtivityProvider = strAtivityProvider;
        this.strActivityDate = strActivityDate;
        this.strActivityStatus = strActivityStatus;
        this.strActivityProviderEmail = strActivityProviderEmail;
        this.strActivityProviderContactNo = strActivityProviderContactNo;
        this.strActivityProviderDesc = strActivityProviderDesc;
        this.strActivityProviderName = strActivityProviderName;
        this.activityVideoModels = activityVideoModels;
        this.feedBackModels = feedBackModels;
        this.imageModels = imageModels;
        //this.strProviderImageUrl=strProviderImageUrl;
    }

    public String getStrProviderImageUrl() {
        return strProviderImageUrl;
    }

    public void setStrProviderImageUrl(String strProviderImageUrl) {
        this.strProviderImageUrl = strProviderImageUrl;
    }

    public String getStrActivityProviderEmail() {
        return strActivityProviderEmail;
    }

    public String getStrActivityProviderContactNo() {
        return strActivityProviderContactNo;
    }

    public String getStrActivityProviderDesc() {
        return strActivityProviderDesc;
    }

    public String getStrActivityProviderName() {
        return strActivityProviderName;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public String getStrActivityMessage() {
        return strActivityMessage;
    }

    public String getStrAtivityProvider() {
        return strAtivityProvider;
    }

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public String getStrActivityStatus() {
        return strActivityStatus;
    }

    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }

    public ArrayList<ActivityVideoModel> getActivityVideoModels() {
        return activityVideoModels;
    }

    public ArrayList<FeedBackModel> getFeedBackModels() {
        return feedBackModels;
    }
}
