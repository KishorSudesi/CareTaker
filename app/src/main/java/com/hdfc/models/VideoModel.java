package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Admin on 2/22/2016.
 */
public class VideoModel implements Serializable {
    private int iVideoId;
    private String strDateTime;
    private String strDescription;

    public int getiVideoId() {
        return iVideoId;
    }

    public void setiVideoId(int iVideoId) {
        this.iVideoId = iVideoId;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }
}
