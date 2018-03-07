package com.ray.missreminder.model;

/**
 * Created by Gaurav on 3/7/2018.
 */

public class IgnoreModel {

    private int id;
    private String number;

    public IgnoreModel(){}

    public IgnoreModel(String number){
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
