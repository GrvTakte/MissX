package com.ray.missreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaurav on 3/7/2018.
 */

public class IgnoreDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "blocknumberdb";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "blocknumbertable";
    public static final String CREATE_TABLE = "create table if not exists "+TABLE_NAME+"(id integer primary key autoincrement,"+DbContract.BLOCKED_NUMBER+" text);";
    public static final String DROP_TABLE = "drop table if exists "+TABLE_NAME;

    public IgnoreDbHelper(Context context){
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

    public boolean hasNumber(String number, SQLiteDatabase db){
        db = this.getWritableDatabase();
        String checkNumber = "SELECT * FROM "+TABLE_NAME+" WHERE "+DbContract.BLOCKED_NUMBER+"="+number;
        Cursor cursor = db.rawQuery(checkNumber,null);

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public void saveNumber(String number, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(DbContract.BLOCKED_NUMBER,number);
        db.insert(TABLE_NAME,null,values);
    }

    public Cursor readBlockedNumbers(SQLiteDatabase database){
        String[] projection = {"id",DbContract.BLOCKED_NUMBER};
        return (database.query(TABLE_NAME,projection,null,null,null,null,null));
    }

    public void deleteRow(SQLiteDatabase database, String number){
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME,DbContract.BLOCKED_NUMBER+"=?",new String[]{number});
    }

    public void deleteAllData(SQLiteDatabase database){
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME,null,null);
    }

}
