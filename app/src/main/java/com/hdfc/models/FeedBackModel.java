package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FeedBackModel implements Serializable {

    private String strFeedBackMess;
    private String strFeedBackBy;
    private int intFeedBackRating;
    private boolean intFeedBackReport;
    private String strFeedBackTime;
    private String strFeedBackByUrl;

    public FeedBackModel(String strFeedBackMess, String strFeedBackBy, int intFeedBackRating, boolean intFeedBackReport, String strFeedBackTime, String strFeedBackByUrl) {
        this.strFeedBackMess = strFeedBackMess;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.intFeedBackReport = intFeedBackReport;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByUrl = strFeedBackByUrl;
    }

    public String getStrFeedBackMess() {
        return strFeedBackMess;
    }

    public String getStrFeedBackBy() {
        return strFeedBackBy;
    }

    public int getIntFeedBackRating() {
        return intFeedBackRating;
    }

    public boolean getIntFeedBackReport() {
        return intFeedBackReport;
    }

    public String getStrFeedBackByUrl() {
        return strFeedBackByUrl;
    }

    public String getStrFeedBackTime() {
        return strFeedBackTime;


    }
}
