package com.hdfc.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class DependentModel {

    private String strName;
    private String strRelation;
    private String strNotes;
    private String strAddress;
    private String strContacts;
    private String strEmail;
    private String strIllness;
    private String strImagePath;
    private String strImageUrl;
    private String strDob;
    private String strAge;

    //private int intAge;
    private int intHealthBp;
    private int intHealthHeartRate;

    private String strDependentID;
    private String strCustomerID;

    private List<ServiceModel> serviceModels = new ArrayList<>();
    private List<ActivityModel> activityModels = new ArrayList<>();
    private List<ActivityModel> monthActivityModel = new ArrayList<>();
    private List<NotificationModel> notificationModels = new ArrayList<>();

    public DependentModel() {
    }

    public DependentModel(String strName, String strRelation, String strNotes, String strAddress,
                          String strContacts, String strEmail, String strIllness,
                          String strImagePath, String strImageUrl, String strAge, int intHealthBp,
                          int intHealthHeartRate, String strDependentID, String strCustomerID,
                          ArrayList<ServiceModel> serviceModels) {
        this.strName = strName;
        this.strRelation = strRelation;
        this.strNotes = strNotes;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strIllness = strIllness;
        this.strImagePath = strImagePath;
        this.strImageUrl = strImageUrl;
        this.strAge = strAge;
        this.intHealthBp = intHealthBp;
        this.intHealthHeartRate = intHealthHeartRate;
        this.strDependentID = strDependentID;
        this.strCustomerID = strCustomerID;
        this.serviceModels = serviceModels;
    }

    public String getStrDob() {
        return strDob;
    }

    public void setStrDob(String strDob) {
        this.strDob = strDob;
    }

    public void setActivityModels(List<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }

    public void setNotificationModels(List<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
    }

    public List<ActivityModel> getActivityModels() {
        return activityModels;
    }

    /*public void setActivityModels(ArrayList<ActivityModel> activityModels) {
        this.activityModels = activityModels;
    }*/

    public void setActivityModels(ActivityModel activityModel) {
        this.activityModels.add(activityModel);
    }

    public List<ActivityModel> getMonthActivityModel() {
        return monthActivityModel;
    }

    public void setMonthActivityModel(List<ActivityModel> monthActivityModel) {
        this.monthActivityModel.addAll(monthActivityModel);
    }

    public void setMonthActivityModel(ActivityModel monthActivityModel) {
        this.monthActivityModel.add(monthActivityModel);
    }

    public void clearMonthActivityModel() {
        this.monthActivityModel.clear();
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrRelation() {
        return strRelation;
    }

    public void setStrRelation(String strRelation) {
        this.strRelation = strRelation;
    }

    public String getStrNotes() {
        return strNotes;
    }

    public void setStrNotes(String strNotes) {
        this.strNotes = strNotes;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getStrContacts() {
        return strContacts;
    }

    public void setStrContacts(String strContacts) {
        this.strContacts = strContacts;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrIllness() {
        return strIllness;
    }

    public void setStrIllness(String strIllness) {
        this.strIllness = strIllness;
    }

    public String getStrImagePath() {
        return strImagePath;
    }

    public void setStrImagePath(String strImagePath) {
        this.strImagePath = strImagePath;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public void setStrImageUrl(String strImageUrl) {
        this.strImageUrl = strImageUrl;
    }

    public String getStrAge() {
        return strAge;
    }

    public void setStrAge(String strAge) {
        this.strAge = strAge;
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

    public String getStrDependentID() {
        return strDependentID;
    }

    public void setStrDependentID(String strDependentID) {
        this.strDependentID = strDependentID;
    }

    public String getStrCustomerID() {
        return strCustomerID;
    }

    public void setStrCustomerID(String strCustomerID) {
        this.strCustomerID = strCustomerID;
    }

    public List<ServiceModel> getServiceModels() {
        return serviceModels;
    }

    public void setServiceModels(List<ServiceModel> serviceModels) {
        this.serviceModels.addAll(serviceModels);
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModels.add(serviceModel);
    }

    public List<NotificationModel> getNotificationModels() {
        return notificationModels;
    }

    public void setNotificationModels(NotificationModel notificationModels) {
        this.notificationModels.add(notificationModels);
    }
}
