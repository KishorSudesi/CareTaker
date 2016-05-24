package com.hdfc.models;

/**
 * Created by Admin on 5/20/2016.
 */
public class ConfirmCustomerModel {
    private String textName = "";
    private String textAddress = "";
    private String textNumber = "";
    private String strImagePath = "";
    private String customerId = "";

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public String getTextAddress() {
        return textAddress;
    }

    public void setTextAddress(String textAddress) {
        this.textAddress = textAddress;
    }

    public String getTextNumber() {
        return textNumber;
    }

    public void setTextNumber(String textNumber) {
        this.textNumber = textNumber;
    }

    public String getStrImagePath() {
        return strImagePath;
    }

    public void setStrImagePath(String strImagePath) {
        this.strImagePath = strImagePath;
    }
}
