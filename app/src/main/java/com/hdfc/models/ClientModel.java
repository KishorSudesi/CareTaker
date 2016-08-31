package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 5/21/2016.
 */
public class ClientModel implements Serializable {


    private CustomerModel customerModel;

    private ArrayList<DependentModel> dependentModels = new ArrayList<>();

    public CustomerModel getCustomerModel() {
        return customerModel;
    }

    public void setCustomerModel(CustomerModel customerModel) {
        this.customerModel = customerModel;
    }

    public ArrayList<DependentModel> getDependentModels() {
        return dependentModels;
    }

    public void setDependentModels(ArrayList<DependentModel> dependentModels) {
        this.dependentModels = dependentModels;
    }
}
