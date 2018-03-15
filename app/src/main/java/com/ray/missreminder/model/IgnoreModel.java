package com.ray.missreminder.model;

/**
 * Created by Gaurav on 3/7/2018.
 */

public class IgnoreModel {

    private int id;
    private String number;
    private String name;

    public IgnoreModel(){}

    public IgnoreModel(String number, String name){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

/*
    Future task:-
    1) Remove number from reminder list if user call to the number which is already present in reminder list(on Successful received call only).
    2) and also display reminder/ignore dialog box to the user when call disconnected.
*/