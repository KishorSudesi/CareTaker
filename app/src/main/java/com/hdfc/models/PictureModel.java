package com.hdfc.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 07-07-2016.
 */
public class PictureModel {
    private String strRoomName = "";
    private String strStatus = "";

    private List<ImageModelCheck> imageModels = new ArrayList<>();

    public PictureModel() {

    }

    public PictureModel(String strRoomName, String strStatus, List<ImageModelCheck> imageModels) {
        this.strStatus = strStatus;
        this.strRoomName = strRoomName;
        this.imageModels = imageModels;

    }

    public String getStrRoomName() {
        return strRoomName;
    }

    public void setStrRoomName(String strRoomName) {
        this.strRoomName = strRoomName;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }


    public List<ImageModelCheck> getImageModels() {
        return imageModels;
    }

    public void setImageModels(List<ImageModelCheck> imageModels) {
        this.imageModels = imageModels;
    }

    public void clearImageModel() {
        this.imageModels.clear();
    }
}
