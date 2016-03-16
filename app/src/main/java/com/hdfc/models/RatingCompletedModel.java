package com.hdfc.models;

/**
 * Created by Admin on 2/22/2016.
 */
public class RatingCompletedModel {
    int img;
    String feedback;
    String personName;
    String timeDate;
    int ratingSmiley;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int getRatingSmiley() {
        return ratingSmiley;
    }

    public void setRatingSmiley(int ratingSmiley) {
        this.ratingSmiley = ratingSmiley;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }
}
