package com.ray.missreminder.model;

/**
 * Created by Gaurav on 3/1/2018.
 */

public class NotesModel {

    private int id;
    private String number;
    private String notesText;
    private String name;

    public NotesModel(){}

    public NotesModel(int id,String number, String notesText, String name){
        this.id = id;
        this.number = number;
        this.notesText = notesText;
        this.name = name;
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

    public String getNotesText() {
        return notesText;
    }

    public void setNotesText(String notesText) {
        this.notesText = notesText;
    }
}
