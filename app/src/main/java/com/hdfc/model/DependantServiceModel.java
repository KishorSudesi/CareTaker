package com.hdfc.model;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class DependantServiceModel implements Serializable {

    private String strDependantServiceName;
    private String strDependantServiceFeatures;
    private int intDependantServiceUnit;
    private int intDependantServiceUnitUsed;

    public DependantServiceModel(String strDependantServiceName, String strDependantServiceFeatures,
                                 int intDependantServiceUnit, int intDependantServiceUnitUsed) {
        this.strDependantServiceName = strDependantServiceName;
        this.strDependantServiceFeatures = strDependantServiceFeatures;
        this.intDependantServiceUnit = intDependantServiceUnit;
        this.intDependantServiceUnitUsed = intDependantServiceUnitUsed;
    }

    public String getStrDependantServiceName() {
        return strDependantServiceName;
    }

    public String getStrDependantServiceFeatures() {
        return strDependantServiceFeatures;
    }

    public int getIntDependantServiceUnit() {
        return intDependantServiceUnit;
    }

    public int getIntDependantServiceUnitUsed() {
        return intDependantServiceUnitUsed;
    }
}
