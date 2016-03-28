package com.hdfc.models;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class ServiceModel {

    private String strServiceName;
    private String strServiceDesc;
    private String strUpdatedDate;

    private String[] strFeatures;
    private String[] strFeaturesDone;

    private double doubleCost;

    private int iUnit;
    private int iUnitUsed;

    private String strServiceId;
    private String strServiceHistoryId;
    private String strDependentId;

    public ServiceModel(String strServiceName, String strServiceDesc, String strUpdatedDate,
                        String[] strFeatures, String[] strFeaturesDone, double doubleCost,
                        int iUnit, int iUnitUsed, String strServiceId, String strServiceHistoryId,
                        String strDependentId) {
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strUpdatedDate = strUpdatedDate;
        this.strFeatures = strFeatures;
        this.strFeaturesDone = strFeaturesDone;
        this.doubleCost = doubleCost;
        this.iUnit = iUnit;
        this.iUnitUsed = iUnitUsed;
        this.strServiceId = strServiceId;
        this.strServiceHistoryId = strServiceHistoryId;
        this.strDependentId = strDependentId;
    }

    public String getStrServiceName() {
        return strServiceName;
    }

    public String getStrServiceDesc() {
        return strServiceDesc;
    }

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public String[] getStrFeatures() {
        return strFeatures;
    }

    public double getDoubleCost() {
        return doubleCost;
    }

    public int getiUnit() {
        return iUnit;
    }

    public int getiUnitUsed() {
        return iUnitUsed;
    }

    public String[] getStrFeaturesDone() {
        return strFeaturesDone;
    }

    public String getStrServiceId() {
        return strServiceId;
    }

    public String getStrServiceHistoryId() {
        return strServiceHistoryId;
    }

    public String getStrDependentId() {
        return strDependentId;
    }
}
