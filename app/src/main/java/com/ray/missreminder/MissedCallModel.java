package com.ray.missreminder;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class MissedCallModel {
    private int id;
    private String number;
    private String name;
    private String time;

    public MissedCallModel(){}

    public MissedCallModel(int id, String number, String name){
        this.setId(id);
        this.setNumber(number);
        this.setName(name);
    }

    public MissedCallModel(int id, String number, String name, String time){
        this.setId(id);
        this.setName(name);
        this.setNumber(number);
        this.setTime(time);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
