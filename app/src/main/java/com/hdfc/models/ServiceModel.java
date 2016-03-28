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
    private int iServiceId;
    private int iServiceHistoryId;
    private int iDependentID;

    public ServiceModel(String strServiceName, String strServiceDesc, String strUpdatedDate,
                        String[] strFeatures, String[] strFeaturesDone, double doubleCost,
                        int iUnit, int iUnitUsed, int iServiceId, int iServiceHistoryId,
                        int iDependentID) {
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strUpdatedDate = strUpdatedDate;
        this.strFeatures = strFeatures;
        this.strFeaturesDone = strFeaturesDone;
        this.doubleCost = doubleCost;
        this.iUnit = iUnit;
        this.iUnitUsed = iUnitUsed;
        this.iServiceId = iServiceId;
        this.iServiceHistoryId = iServiceHistoryId;
        this.iDependentID = iDependentID;
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

    public int getiServiceId() {
        return iServiceId;
    }

    public int getiDependentID() {
        return iDependentID;
    }

    public String[] getStrFeaturesDone() {
        return strFeaturesDone;
    }

    public int getiServiceHistoryId() {
        return iServiceHistoryId;
    }
}
