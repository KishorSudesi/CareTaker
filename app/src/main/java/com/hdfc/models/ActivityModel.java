package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ActivityModel implements Serializable {

    private int iActivityID;
    private int iCustomerID;
    private int iDependentID;
    private int iProviderID;
    private int iServcieID;

    private String strActivityName;
    private String strActivityDesc;
    private String strActivityMessage;
    private String strActivityStatus;
    private String strServiceName;
    private String strServiceDesc;
    private String strActivityDate;
    private String strActivityDoneDate;
    private String strActivityProviderStatus;

    private boolean bActivityOverdue;

    private String[] strFeatures;
    private String[] strFeaturesDone;

    private ArrayList<ImageModel> imageModels = new ArrayList<>();
    private ArrayList<VideoModel> videoModels = new ArrayList<>();
    private ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();

    public ActivityModel() {
    }


    public ActivityModel(int iActivityID, int iCustomerID, int iDependentID, int iProviderID,
                         String strActivityName,
                         String strActivityDesc, String strActivityMessage,
                         String strActivityStatus, int iServcieID, String strServiceName,
                         String strServiceDesc, String strActivityDate, String strActivityDoneDate,
                         String strActivityProviderStatus, boolean bActivityOverdue,
                         String[] strFeatures, String[] strFeaturesDone,
                         ArrayList<ImageModel> imageModels, ArrayList<VideoModel> videoModels,
                         ArrayList<FeedBackModel> feedBackModels) {
        this.iActivityID = iActivityID;
        this.iCustomerID = iCustomerID;
        this.iDependentID = iDependentID;
        this.iProviderID = iProviderID;
        this.strActivityName = strActivityName;
        this.strActivityDesc = strActivityDesc;
        this.strActivityMessage = strActivityMessage;
        this.strActivityStatus = strActivityStatus;
        this.iServcieID = iServcieID;
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strActivityDate = strActivityDate;
        this.strActivityDoneDate = strActivityDoneDate;
        this.strActivityProviderStatus = strActivityProviderStatus;
        this.bActivityOverdue = bActivityOverdue;
        this.strFeatures = strFeatures;
        this.strFeaturesDone = strFeaturesDone;
        this.imageModels = imageModels;
        this.videoModels = videoModels;
        this.feedBackModels = feedBackModels;
    }

    public ActivityModel(int iActivityID, int iCustomerID, int iDependentID, int iProviderID,
                         String strActivityName,
                         String strActivityDesc, String strActivityMessage,
                         String strActivityStatus, int iServcieID, String strServiceName,
                         String strServiceDesc, String strActivityDate,
                         String strActivityProviderStatus, boolean bActivityOverdue,
                         String[] strFeatures) {
        this.iActivityID = iActivityID;
        this.iCustomerID = iCustomerID;
        this.iDependentID = iDependentID;
        this.iProviderID = iProviderID;
        this.strActivityName = strActivityName;
        this.strActivityDesc = strActivityDesc;
        this.strActivityMessage = strActivityMessage;
        this.strActivityStatus = strActivityStatus;
        this.iServcieID = iServcieID;
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strActivityDate = strActivityDate;
        this.strActivityProviderStatus = strActivityProviderStatus;
        this.bActivityOverdue = bActivityOverdue;
        this.strFeatures = strFeatures;
    }

    public int getiActivityID() {
        return iActivityID;
    }

    public int getiCustomerID() {
        return iCustomerID;
    }

    public int getiDependentID() {
        return iDependentID;
    }

    public int getiProviderID() {
        return iProviderID;
    }

    public int getiServcieID() {
        return iServcieID;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public String getStrActivityDesc() {
        return strActivityDesc;
    }

    public String getStrActivityMessage() {
        return strActivityMessage;
    }

    public String getStrActivityStatus() {
        return strActivityStatus;
    }

    public String getStrServiceName() {
        return strServiceName;
    }

    public String getStrServiceDesc() {
        return strServiceDesc;
    }

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public String getStrActivityDoneDate() {
        return strActivityDoneDate;
    }

    public String getStrActivityProviderStatus() {
        return strActivityProviderStatus;
    }

    public boolean isbActivityOverdue() {
        return bActivityOverdue;
    }

    public String[] getStrFeatures() {
        return strFeatures;
    }

    public String[] getStrFeaturesDone() {
        return strFeaturesDone;
    }

    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }

    public ArrayList<VideoModel> getVideoModels() {
        return videoModels;
    }

    public ArrayList<FeedBackModel> getFeedBackModels() {
        return feedBackModels;
    }
}
