package com.hdfc.models;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class ServiceModel {

    private String strServiceName;
    private String strUpdatedDate;
    private String strCategoryName;

    private double doubleCost;

    private int iUnit;
    private int iUnitUsed;
    private int iServiceNo;

    private String strServiceId;
    private String strServiceHistoryId;
    private String strCustomerId;

    public ServiceModel() {
    }



    public String getStrServiceName() {
        return strServiceName;
    }

    public void setStrServiceName(String strServiceName) {
        this.strServiceName = strServiceName;
    }

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public void setStrUpdatedDate(String strUpdatedDate) {
        this.strUpdatedDate = strUpdatedDate;
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

    public String getStrServiceId() {
        return strServiceId;
    }

    public void setStrServiceId(String strServiceId) {
        this.strServiceId = strServiceId;
    }

    public String getStrServiceHistoryId() {
        return strServiceHistoryId;
    }

    public void setStrServiceHistoryId(String strServiceHistoryId) {
        this.strServiceHistoryId = strServiceHistoryId;
    }
}
