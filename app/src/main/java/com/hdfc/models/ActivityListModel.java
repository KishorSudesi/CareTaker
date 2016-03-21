package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityListModel implements Serializable {
    private String strDate = "";
    private String strDateTime = "";
    private String strDateNumber = "";
    private String strActualDate = "";
    private String strMessage = "";
    private String strPerson = "";
    private String strStatus = "";
    private String strDesc = "";
    private String strImageUrl = "";
    private String strDependentName = "";

    public String getStrDependentName() {
        return strDependentName;
    }

    public void setStrDependentName(String strDependentName) {
        this.strDependentName = strDependentName;
    }

    public String getStrActualDate() {
        return strActualDate;
    }

    public void setStrActualDate(String strActualDate) {
        this.strActualDate = strActualDate;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public void setStrImageUrl(String strImageUrl) {
        this.strImageUrl = strImageUrl;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    /* public ActivityListModel(String strDate, String strDateTime, String strDateNumber, String strMessage){
                    this.strDate = strDate;
                    this.strDateTime = strDateTime;
                    this.strDateNumber = strDateNumber;
                    this.strMessage = strMessage;
                }
            */
    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }

    public String getStrDateNumber() {
        return strDateNumber;
    }

    public void setStrDateNumber(String strDateNumber) {
        this.strDateNumber = strDateNumber;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }


    public String getStrPerson() {
        return strPerson;
    }

    public void setStrPerson(String strPerson) {
        this.strPerson = strPerson;
    }
}
