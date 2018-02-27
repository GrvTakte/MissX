package com.gaurav.missreminder;

import com.gaurav.missreminder.fragment.MissedCall;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class MissedCallModel {
    private int id;
    private String number;

    public MissedCallModel(int id, String number){
        this.setId(id);
        this.setNumber(number);
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
