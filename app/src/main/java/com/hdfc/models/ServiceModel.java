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

    public ServiceModel() {
    }

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

    public ServiceModel(String strServiceName, String strServiceDesc, String strUpdatedDate,
                        String[] strFeatures, int iUnit, int iUnitUsed, String strServiceId,
                        String strServiceHistoryId) {
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strUpdatedDate = strUpdatedDate;
        this.strFeatures = strFeatures;
        this.iUnit = iUnit;
        this.iUnitUsed = iUnitUsed;
        this.strServiceId = strServiceId;
        this.strServiceHistoryId = strServiceHistoryId;
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

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public void setStrUpdatedDate(String strUpdatedDate) {
        this.strUpdatedDate = strUpdatedDate;
    }

    public String[] getStrFeatures() {
        return strFeatures;
    }

    public void setStrFeatures(String[] strFeatures) {
        this.strFeatures = strFeatures;
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

    public String[] getStrFeaturesDone() {
        return strFeaturesDone;
    }

    public void setStrFeaturesDone(String[] strFeaturesDone) {
        this.strFeaturesDone = strFeaturesDone;
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

    public String getStrDependentId() {
        return strDependentId;
    }

    public void setStrDependentId(String strDependentId) {
        this.strDependentId = strDependentId;
    }
}
