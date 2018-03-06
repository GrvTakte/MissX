package com.ray.missreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaurav on 3/1/2018.
 */

public class DbNotesHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notesdatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "notestable";

    public static final String CREATE_TABLE = "create table if not exists "+TABLE_NAME+" (id integer primary key autoincrement,"+DbContract.NOTES_NUMBER+" text,"+
            DbContract.NOTES_TEXT+" text,"+DbContract.NOTES_NAME+" text);";

    public static final String DROP_TABLE = "drop table if exists "+TABLE_NAME;

    public DbNotesHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveNotes(String number, String notes, String name, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(DbContract.NOTES_NUMBER,number);
        values.put(DbContract.NOTES_TEXT,notes);
        values.put(DbContract.NOTES_NAME,name);
        db.insert(TABLE_NAME,null,values);
    }

    public Cursor readNotes(SQLiteDatabase database){
        String[] projection = {"id",DbContract.NOTES_NUMBER,DbContract.NOTES_TEXT,DbContract.NOTES_NAME};
        return (database.query(TABLE_NAME,projection,null,null,null,null,null));
    }

    public boolean dbStatus(SQLiteDatabase database){
        Cursor cursor = database.query(TABLE_NAME,null,null,null,null,null,null);

        if (cursor.getCount()>0){
            return true;
        }else {
            return false;
        }
    }

    public void deleteRow(SQLiteDatabase database, String number){
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME,DbContract.NOTES_NUMBER+"=?",new String[]{number});
    }

    public void clearDatabase(SQLiteDatabase database){
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME,null,null);
    }


}
