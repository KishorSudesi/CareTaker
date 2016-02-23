package com.hdfc.model;

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


    private ArrayList<ActivityImageModel> activityImageModels = new ArrayList<>();
    private ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();
    private ArrayList<ActivityFeedBackModel> activityFeedBackModels = new ArrayList<>();

    public ActivityModel() {
    }

    public ActivityModel(String strActivityName, String strActivityMessage,
                         String strAtivityProvider, String strActivityDate, String strActivityStatus,
                         String strActivityProviderEmail, String strActivityProviderContactNo,
                         String strActivityProviderDesc, String strActivityProviderName,
                         ArrayList<ActivityImageModel> activityImageModels,
                         ArrayList<ActivityVideoModel> activityVideoModels,
                         ArrayList<ActivityFeedBackModel> activityFeedBackModels) {
        this.strActivityName = strActivityName;
        this.strActivityMessage = strActivityMessage;
        this.strAtivityProvider = strAtivityProvider;
        this.strActivityDate = strActivityDate;
        this.strActivityStatus = strActivityStatus;
        this.strActivityProviderEmail = strActivityProviderEmail;
        this.strActivityProviderContactNo = strActivityProviderContactNo;
        this.strActivityProviderDesc = strActivityProviderDesc;
        this.strActivityProviderName = strActivityProviderName;
        this.activityImageModels = activityImageModels;
        this.activityVideoModels = activityVideoModels;
        this.activityFeedBackModels = activityFeedBackModels;
    }

    public ActivityModel(String strActivityName, String strActivityMessage,
                         String strAtivityProvider, String strActivityDate, String strActivityStatus,
                         String strActivityProviderEmail, String strActivityProviderContactNo,
                         String strActivityProviderDesc, String strActivityProviderName,
                         ArrayList<ActivityVideoModel> activityVideoModels,
                         ArrayList<ActivityFeedBackModel> activityFeedBackModels) {
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
        this.activityFeedBackModels = activityFeedBackModels;
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

    public ArrayList<ActivityImageModel> getActivityImageModels() {
        return activityImageModels;
    }

    public ArrayList<ActivityVideoModel> getActivityVideoModels() {
        return activityVideoModels;
    }

    public ArrayList<ActivityFeedBackModel> getActivityFeedBackModels() {
        return activityFeedBackModels;
    }
}
