package com.hdfc.model;

import java.io.Serializable;

/**
 * Created by Admin on 2/22/2016.
 */
public class VideoCompletedModel implements Serializable {
    int imgVid;
    String dateTime;
    String information;

    public int getImgVid() {
        return imgVid;
    }

    public void setImgVid(int imgVid) {
        this.imgVid = imgVid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
