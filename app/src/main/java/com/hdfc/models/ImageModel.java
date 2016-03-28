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

    private int iImageID;

    public ImageModel(String strImageName, String strImageUrl, String strImageDesc,
                      String strImageTime, int iImageID) {
        this.strImageName = strImageName;
        this.strImageUrl = strImageUrl;
        this.strImageDesc = strImageDesc;
        this.strImageTime = strImageTime;
        this.iImageID = iImageID;
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

    public int getiImageID() {
        return iImageID;
    }
}
