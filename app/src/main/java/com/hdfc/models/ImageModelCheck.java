package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Admin on 7/18/2016.
 */
public class ImageModelCheck implements Serializable {

    private String strImageUrl="";
    private String strImageDesc;
    private String strImageTime;

    public ImageModelCheck(String strImageUrl, String strImageDesc,
                           String strImageTime) {

        this.strImageUrl = strImageUrl;
        this.strImageDesc = strImageDesc;
        this.strImageTime = strImageTime;
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
}
