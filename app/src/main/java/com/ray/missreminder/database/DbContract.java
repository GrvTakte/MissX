package com.ray.missreminder.database;

/**
 * Created by Gaurav on 2/23/2018.
 */

public class DbContract {

    // reminder database variable
    public static final String TABLE_NAME = "incomingInfo";
    public static final String INCOMING_NUMBER = "incomingNumber";
    public static final String INCOMING_NAME = "incoming_name";

    //reminder broadcast receiver variable
    public static final String UPDATE_UI_FILTER = "com.gaurav.missreminder.UPDATE_UI";
    public static final String REFRESH_LIST_UI = "com.gaurav.missreminder.UPDATE_LIST_UI";
    public static final String REFRESH_OUTGOING_LIST = "com.gaurav.missreminder.refresh_outgoing_list";

    //notes database variable
    public static final String NOTES_NUMBER = "notesnumber";
    public static final String NOTES_TEXT = "notestext";
    public static final String NOTES_NAME = "notesname";

    //notes broadcast receiver variable
    public static final String REFRESH_NOTES_UI = "com.gaurav.missreminder.refresh.notes.ui";
    public static final String REMOVE_REFRESH_NOTES_UI = "com.gaurav.missreminder.remove.refresh.notes.ui";

    //block number database variable
    public static final String BLOCKED_NUMBER = "block_number";
}
