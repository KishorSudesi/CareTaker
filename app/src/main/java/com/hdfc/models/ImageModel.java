package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ImageModel implements Serializable {

    private String strImageName;
    private String strImageUrl;
    private String strImageDesc;
    private String strImageTime;

    private String strImageID;

    public ImageModel(String strImageName, String strImageUrl, String strImageDesc,
                      String strImageTime, String strImageID) {
        this.strImageName = strImageName;
        this.strImageUrl = strImageUrl;
        this.strImageDesc = strImageDesc;
        this.strImageTime = strImageTime;
        this.strImageID = strImageID;
    }

    public String getStrImageName() {
        return strImageName;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public String getStrImageDesc() {
        return strImageDesc;
    }

    public String getStrImageTime() {
        return strImageTime;
    }

    public String getStrImageID() {
        return strImageID;
    }
}
