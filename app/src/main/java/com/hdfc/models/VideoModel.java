package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class VideoModel implements Serializable {

    private String strVideoName;
    private String strVideoUrl;
    private String strVideoDesc;
    private String strVideoTime;

    private int iVideoID;

    public VideoModel(String strVideoName, String strVideoUrl, String strVideoDesc,
                      String strVideoTime, int iVideoID) {
        this.strVideoName = strVideoName;
        this.strVideoUrl = strVideoUrl;
        this.strVideoDesc = strVideoDesc;
        this.strVideoTime = strVideoTime;
        this.iVideoID = iVideoID;
    }

    public String getStrVideoName() {
        return strVideoName;
    }

    public String getStrVideoUrl() {
        return strVideoUrl;
    }

    public String getStrVideoDesc() {
        return strVideoDesc;
    }

    public String getStrVideoTime() {
        return strVideoTime;
    }
}
