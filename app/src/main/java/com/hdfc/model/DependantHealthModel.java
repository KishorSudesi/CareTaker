package com.hdfc.model;

/**
 * Created by Suhail on 2/19/2016.
 */
public class DependantHealthModel {

    private int intDependantHealthBp;
    private int intDependantHealthHeartRate;
    private String intDependantHealthTimeTaken;

    public DependantHealthModel(int intDependantHealthBp, int intDependantHealthHeartRate, String intDependantHealthTimeTaken) {
        this.intDependantHealthBp = intDependantHealthBp;
        this.intDependantHealthHeartRate = intDependantHealthHeartRate;
        this.intDependantHealthTimeTaken = intDependantHealthTimeTaken;
    }

    public int getIntDependantHealthBp() {
        return intDependantHealthBp;
    }

    public int getIntDependantHealthHeartRate() {
        return intDependantHealthHeartRate;
    }

    public String getIntDependantHealthTimeTaken() {
        return intDependantHealthTimeTaken;
    }
}
