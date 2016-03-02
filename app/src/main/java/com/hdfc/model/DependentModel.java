package com.hdfc.model;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class DependentModel {

    private String strName = "";
    private String strRelation = "";
    private String strImg = "";
    private String strImgServer = "";
    private String strDesc = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";
    private String strIllness = "";
    private int intAge = 0;
    private int intHealthBp = 0;
    private int intHealthHeartRate = 0;
    private ArrayList<ActivityModel> activityModels = new ArrayList<>();
    private ArrayList<DependentNotificationModel> dependentNotificationModels = new ArrayList<>();
    private ArrayList<DependentHealthModel> dependentHealthModels = new ArrayList<>();
    private ArrayList<DependentServiceModel> dependentServiceModels = new ArrayList<>();


    public DependentModel() {
    }

    public DependentModel(String strName, String strRelation, String strImg, String strImgServer,
                          String strDesc, String strAddress, String strContacts, String strEmail,
                          String strIllness, int intAge) {
        this.strName = strName;
        this.strRelation = strRelation;
        this.strImg = strImg;
        this.strImgServer = strImgServer;
        this.strDesc = strDesc;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strIllness = strIllness;
        this.intAge = intAge;
    }

    public DependentModel(String strName, String strRelation, String strImg, String strImgServer,
                          String strDesc, String strAddress, String strContacts, String strEmail,
                          String strIllness, int intAge, int intHealthBp, int intHealthHeartRate,
                          ArrayList<ActivityModel> activityModels,
                          ArrayList<DependentNotificationModel> dependentNotificationModels,
                          ArrayList<DependentHealthModel> dependentHealthModels,
                          ArrayList<DependentServiceModel> dependentServiceModels) {
        this.strName = strName;
        this.strRelation = strRelation;
        this.strImg = strImg;
        this.strImgServer = strImgServer;
        this.strDesc = strDesc;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strIllness = strIllness;
        this.intAge = intAge;
        this.intHealthBp = intHealthBp;
        this.intHealthHeartRate = intHealthHeartRate;
        this.activityModels = activityModels;
        this.dependentNotificationModels = dependentNotificationModels;
        this.dependentHealthModels = dependentHealthModels;
        this.dependentServiceModels = dependentServiceModels;
    }

    public String getStrImgServer() {
        return strImgServer;
    }

    public void setStrImgServer(String strImgServer) {
        this.strImgServer = strImgServer;
    }

    public int getIntHealthBp() {
        return intHealthBp;
    }

    public void setIntHealthBp(int intHealthBp) {
        this.intHealthBp = intHealthBp;
    }

    public int getIntHealthHeartRate() {
        return intHealthHeartRate;
    }

    public void setIntHealthHeartRate(int intHealthHeartRate) {
        this.intHealthHeartRate = intHealthHeartRate;
    }

    public ArrayList<ActivityModel> getActivityModels() {
        return activityModels;
    }

    public void setActivityModels(ArrayList<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }

    public ArrayList<DependentNotificationModel> getDependentNotificationModels() {
        return dependentNotificationModels;
    }

    public void setDependentNotificationModels(ArrayList<DependentNotificationModel> dependentNotificationModels) {
        this.dependentNotificationModels = dependentNotificationModels;
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

    public void setStrIllness(String strIllness) {
        this.strIllness = strIllness;
    }

    public ArrayList<DependentServiceModel> getDependentServiceModels() {
        return dependentServiceModels;
    }

    public void setDependentServiceModels(ArrayList<DependentServiceModel> dependentServiceModels) {
        this.dependentServiceModels = dependentServiceModels;
    }

    public ArrayList<DependentHealthModel> getDependentHealthModels() {
        return dependentHealthModels;
    }

    public void setDependentHealthModels(ArrayList<DependentHealthModel> dependentHealthModels) {
        this.dependentHealthModels = dependentHealthModels;
    }

    public int getIntAge() {
        return intAge;
    }

    public void setIntAge(int intAge) {
        this.intAge = intAge;
    }
}
