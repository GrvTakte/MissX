package com.ray.missreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaurav on 2/23/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "numberDb";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE = "create table "+DbContract.TABLE_NAME+
            "(id integer primary key autoincrement,"+DbContract.INCOMING_NUMBER+" text,"+DbContract.INCOMING_NAME+" text);";

    private static final String DROP_TABLE = "drop table if exists "+DbContract.TABLE_NAME;

    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveNumber(String number, String name, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(DbContract.INCOMING_NUMBER,number);
        values.put(DbContract.INCOMING_NAME,name);
        db.insert(DbContract.TABLE_NAME,null,values);
    }

    public Cursor readNumber(SQLiteDatabase database){
        String[] projection = {"id",DbContract.INCOMING_NUMBER,DbContract.INCOMING_NAME};
        return (database.query(DbContract.TABLE_NAME,projection,null,null,null,null,null));
    }

    public boolean dbStatus(SQLiteDatabase database){
        Cursor cursor = database.query(DbContract.TABLE_NAME,null,null,null,null,null,null);

        if (cursor.getCount()>0){
            return true;
        }else {
            return false;
        }
    }

    public void deleteRow(SQLiteDatabase database, String number){
        database = this.getWritableDatabase();
        database.delete(DbContract.TABLE_NAME,DbContract.INCOMING_NUMBER+"=?",new String[]{number});
    }

    public void clearDatabase(SQLiteDatabase database){
        database = this.getWritableDatabase();
        database.delete(DbContract.TABLE_NAME,null,null);
    }
}

