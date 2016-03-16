package com.hdfc.models;

import org.json.JSONArray;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class ServiceModel {

    private String strServiceName;
    private String strServiceDesc;
    private JSONArray jsonArrayFeatures;
    private double doubleCost;
    private int iUnit;
    private int iUnitUsed;
    private int iServiceId;
    private String strUpdatedDate;

    public ServiceModel(String strServiceName, String strServiceDesc, JSONArray jsonArrayFeatures, double doubleCost, int iUnit, int iServiceId) {
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.jsonArrayFeatures = jsonArrayFeatures;
        this.doubleCost = doubleCost;
        this.iUnit = iUnit;
        this.iServiceId = iServiceId;
    }

    public ServiceModel(String strServiceName, int iServiceId ,int iUnit, int iUnitUsed, JSONArray jsonArrayFeatures, String strServiceDesc) {
        this.strServiceName = strServiceName;
        this.iServiceId = iServiceId;
        this.iUnit = iUnit;
        this.iUnitUsed = iUnitUsed;
        this.jsonArrayFeatures = jsonArrayFeatures;
        this.strServiceDesc = strServiceDesc;
    }

    public ServiceModel(String strServiceName, String strUpdatedDate, int iServiceId, int iUnitUsed, int iUnit, double doubleCost, JSONArray jsonArrayFeatures, String strServiceDesc) {
        this.strServiceName = strServiceName;
        this.strUpdatedDate = strUpdatedDate;
        this.iServiceId = iServiceId;
        this.iUnitUsed = iUnitUsed;
        this.iUnit = iUnit;
        this.doubleCost = doubleCost;
        this.jsonArrayFeatures = jsonArrayFeatures;
        this.strServiceDesc = strServiceDesc;
    }

    public String getStrServiceName() {
        return strServiceName;
    }

    public void setStrServiceName(String strServiceName) {
        this.strServiceName = strServiceName;
    }

    public String getStrServiceDesc() {
        return strServiceDesc;
    }

    public void setStrServiceDesc(String strServiceDesc) {
        this.strServiceDesc = strServiceDesc;
    }

    public JSONArray getJsonArrayFeatures() {
        return jsonArrayFeatures;
    }

    public void setJsonArrayFeatures(JSONArray jsonArrayFeatures) {
        this.jsonArrayFeatures = jsonArrayFeatures;
    }

    public double getDoubleCost() {
        return doubleCost;
    }

    public void setDoubleCost(double doubleCost) {
        this.doubleCost = doubleCost;
    }

    public int getiUnit() {
        return iUnit;
    }

    public void setiUnit(int iUnit) {
        this.iUnit = iUnit;
    }

    public int getiUnitUsed() {
        return iUnitUsed;
    }

    public void setiUnitUsed(int iUnitUsed) {
        this.iUnitUsed = iUnitUsed;
    }

    public int getiServiceId() {
        return iServiceId;
    }

    public void setiServiceId(int iServiceId) {
        this.iServiceId = iServiceId;
    }

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public void setStrUpdatedDate(String strUpdatedDate) {
        this.strUpdatedDate = strUpdatedDate;
    }
}
