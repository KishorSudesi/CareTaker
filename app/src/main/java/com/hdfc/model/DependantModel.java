package com.hdfc.model;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class DependantModel {

    private String strName = "";
    private String strRelation = "";
    private String strImg = "";
    private String strDesc = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";
    private String strIllness = "";
    private String strAge = "";
    private int intHealthBp = 0;
    private int intHealthHeartRate = 0;
    private ArrayList<ActivityModel> activityModels = new ArrayList<>();
    private ArrayList<DependantNotificationModel> dependantNotificationModels = new ArrayList<>();
    private ArrayList<DependantHealthModel> dependantHealthModels = new ArrayList<>();
    private ArrayList<DependantServiceModel> dependantServiceModels = new ArrayList<>();

    public DependantModel() {
    }

    public DependantModel(String strName, String strRelation, String strImg, String strDesc,
                          String strAddress, String strContacts, String strEmail, String strIllness,
                          String strAge, int intHealthBp, int intHealthHeartRate,
                          ArrayList<ActivityModel> activityModels,
                          ArrayList<DependantNotificationModel> dependantNotificationModels,
                          ArrayList<DependantHealthModel> dependantHealthModels,
                          ArrayList<DependantServiceModel> dependantServiceModels) {
        this.strName = strName;
        this.strRelation = strRelation;
        this.strImg = strImg;
        this.strDesc = strDesc;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strIllness = strIllness;
        this.strAge = strAge;
        this.intHealthBp = intHealthBp;
        this.intHealthHeartRate = intHealthHeartRate;
        this.activityModels = activityModels;
        this.dependantNotificationModels = dependantNotificationModels;
        this.dependantHealthModels = dependantHealthModels;
        this.dependantServiceModels = dependantServiceModels;
    }

    public int getIntHealthBp() {
        return intHealthBp;
    }

    public int getIntHealthHeartRate() {
        return intHealthHeartRate;
    }

    public ArrayList<ActivityModel> getActivityModels() {
        return activityModels;
    }

    public ArrayList<DependantNotificationModel> getDependantNotificationModels() {
        return dependantNotificationModels;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrImg() {
        return strImg;
    }

    public void setStrImg(String strImg) {
        this.strImg = strImg;
    }

    public String getStrRelation() {

        return strRelation;
    }

    public void setStrRelation(String strRelation) {
        this.strRelation = strRelation;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrContacts() {
        return strContacts;
    }

    public void setStrContacts(String strContacts) {
        this.strContacts = strContacts;
    }

    public String getStrDesc() {

        return strDesc;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }


    public String getStrIllness() {
        return strIllness;
    }

    public String getStrAge() {
        return strAge;
    }

    public ArrayList<DependantServiceModel> getDependantServiceModels() {
        return dependantServiceModels;
    }

    public ArrayList<DependantHealthModel> getDependantHealthModels() {
        return dependantHealthModels;
    }
}
