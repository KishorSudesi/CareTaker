package com.hdfc.models;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class CustomerModel {

    private String strName = "";
    private String strPaytm = "";
    private String strImgUrl = "";
    private String strImgPath = "";
    private String strAddress = "";
    private String strCity = "";
    private String strState = "";
    private String strContacts = "";
    private String strEmail = "";
    private String strDob = "";
    private String strCountryCode = "";
    private String strCountryIsdCode = "";
    private String strCountryAreaCode = "";
    private String strLandLine = "";

    private String strCustomerID="";

    public CustomerModel() {
    }

    public CustomerModel(String strName, String strPaytm, String strImgUrl, String strAddress,
                         String strContacts, String strEmail, String strCustomerID,
                         String strImgPath) {
        this.strName = strName;
        this.strPaytm = strPaytm;
        this.strImgUrl = strImgUrl;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strCustomerID = strCustomerID;
        this.strImgPath = strImgPath;
    }

    public String getStrCity() {
        return strCity;
    }

    public void setStrCity(String strCity) {
        this.strCity = strCity;
    }

    public String getStrState() {
        return strState;
    }

    public void setStrState(String strState) {
        this.strState = strState;
    }

    public String getStrDob() {
        return strDob;
    }

    public void setStrDob(String strDob) {
        this.strDob = strDob;
    }

    public String getStrCountryCode() {
        return strCountryCode;
    }

    public void setStrCountryCode(String strCountryCode) {
        this.strCountryCode = strCountryCode;
    }

    public String getStrCountryIsdCode() {
        return strCountryIsdCode;
    }

    public void setStrCountryIsdCode(String strCountryIsdCode) {
        this.strCountryIsdCode = strCountryIsdCode;
    }

    public String getStrCountryAreaCode() {
        return strCountryAreaCode;
    }

    public void setStrCountryAreaCode(String strCountryAreaCode) {
        this.strCountryAreaCode = strCountryAreaCode;
    }

    public String getStrLandLine() {
        return strLandLine;
    }

    public void setStrLandLine(String strLandLine) {
        this.strLandLine = strLandLine;
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

    public String getStrCustomerID() {
        return strCustomerID;
    }

    public void setStrCustomerID(String strCustomerID) {
        this.strCustomerID = strCustomerID;
    }

    public String getStrImgPath() {
        return strImgPath;
    }

    public void setStrImgPath(String strImgPath) {
        this.strImgPath = strImgPath;
    }
}
