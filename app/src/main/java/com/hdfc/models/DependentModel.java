package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class DependentModel {

    private String strName = "";
    private String strRelation = "";
    private String strNotes = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";
    private String strIllness = "";
    private String strImagePath = "";
    private String strImageUrl = "";

    private int intAge = 0;
    private int intHealthBp = 0;
    private int intHealthHeartRate = 0;

    private int iDependentID;
    private int iCustomerID;

    private ArrayList<ServiceModel> serviceModels = new ArrayList<>();

    public DependentModel() {
    }

    public DependentModel(String strName, String strRelation, String strNotes, String strAddress,
                          String strContacts, String strEmail, String strIllness,
                          String strImagePath, String strImageUrl, int intAge, int intHealthBp,
                          int intHealthHeartRate, int iDependentID, int iCustomerID,
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
        this.intAge = intAge;
        this.intHealthBp = intHealthBp;
        this.intHealthHeartRate = intHealthHeartRate;
        this.iDependentID = iDependentID;
        this.iCustomerID = iCustomerID;
        this.serviceModels = serviceModels;
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

    public int getIntAge() {
        return intAge;
    }

    public void setIntAge(int intAge) {
        this.intAge = intAge;
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

    public int getiDependentID() {
        return iDependentID;
    }

    public void setiDependentID(int iDependentID) {
        this.iDependentID = iDependentID;
    }

    public int getiCustomerID() {
        return iCustomerID;
    }

    public void setiCustomerID(int iCustomerID) {
        this.iCustomerID = iCustomerID;
    }

    public ArrayList<ServiceModel> getServiceModels() {
        return serviceModels;
    }

    public void setServiceModels(ArrayList<ServiceModel> serviceModels) {
        this.serviceModels = serviceModels;
    }
}
