package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FileModel implements Serializable {

    private String strFileName;
    private String strFileUrl;
    private String strFileType;

    private String strFileDesc;
    private String strFilePath;
    private String strFileTime;

    public FileModel(String strFileName, String strFileUrl, String strFileType) {
        this.strFileName = strFileName;
        this.strFileUrl = strFileUrl;
        this.strFileType = strFileType;
    }

    public FileModel(String strFileName, String strFileUrl, String strFileType, String strFileDesc, String strFilePath, String strFileTime) {
        this.strFileName = strFileName;
        this.strFileUrl = strFileUrl;
        this.strFileType = strFileType;
        this.strFileDesc = strFileDesc;
        this.strFilePath = strFilePath;
        this.strFileTime = strFileTime;
    }

    public String getStrFileDesc() {
        return strFileDesc;
    }

    public void setStrFileDesc(String strFileDesc) {
        this.strFileDesc = strFileDesc;
    }

    public String getStrFilePath() {
        return strFilePath;
    }

    public void setStrFilePath(String strFilePath) {
        this.strFilePath = strFilePath;
    }

    public String getStrFileTime() {
        return strFileTime;
    }

    public void setStrFileTime(String strFileTime) {
        this.strFileTime = strFileTime;
    }

    public String getStrFileName() {
        return strFileName;
    }

    public void setStrFileName(String strFileName) {
        this.strFileName = strFileName;
    }

    public String getStrFileUrl() {
        return strFileUrl;
    }

    public void setStrFileUrl(String strFileUrl) {
        this.strFileUrl = strFileUrl;
    }

    public String getStrFileType() {
        return strFileType;
    }

    public void setStrFileType(String strFileType) {
        this.strFileType = strFileType;
    }
}
