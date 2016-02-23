package com.hdfc.model;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class CustomerModel {

    private String strName = "";
    private String strPaytm = "";
    private String strImgUrl = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";

    private ArrayList<DependantModel> dependantModels = new ArrayList<>();

    public CustomerModel() {
    }

    public CustomerModel(String strName, String strPaytm, String strImgUrl, String strAddress,
                         String strContacts,
                         String strEmail, ArrayList<DependantModel> dependantModels) {
        this.strName = strName;
        this.strPaytm = strPaytm;
        this.strImgUrl = strImgUrl;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.dependantModels = dependantModels;
    }

    public CustomerModel(String strName, String strPaytm, String strImgUrl, String strAddress,
                         String strContacts,
                         String strEmail) {
        this.strName = strName;
        this.strPaytm = strPaytm;
        this.strImgUrl = strImgUrl;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrPaytm() {
        return strPaytm;
    }

    public void setStrPaytm(String strPaytm) {
        this.strPaytm = strPaytm;
    }

    public String getStrImgUrl() {
        return strImgUrl;
    }

    public void setStrImgUrl(String strImgUrl) {
        this.strImgUrl = strImgUrl;
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

    public ArrayList<DependantModel> getDependantModels() {
        return dependantModels;
    }

    public void setDependantModels(ArrayList<DependantModel> dependantModels) {
        this.dependantModels = dependantModels;
    }
}
