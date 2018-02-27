package com.gaurav.missreminder.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.gaurav.missreminder.database.DbContract;
import com.gaurav.missreminder.database.DbHelper;

import java.util.Date;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class IncommingCallReceiver extends BroadcastReceiver {

    static boolean ring = false;
    static boolean callReceived = false;
    String callerPhoneNumber;

    SharedPreferences reminderPref;
    SharedPreferences.Editor reminderEditor;
    boolean showReminder;

    @Override
    public void onReceive(final Context mContext, Intent intent) {

        //Shared prefernce setting.
        reminderPref = mContext.getSharedPreferences("reminderPreference",0);
        reminderEditor = reminderPref.edit();
        showReminder = reminderPref.getBoolean("showReminder",true);
        Log.d("missX:","onReceive ============================================================ ");

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);


        // If phone state "Rininging"
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

            Log.d("missX: ", " if:EXTRA_STATE_RINGING ");

            ring = true;
            // Get the Caller's Phone Number
            Bundle bundle = intent.getExtras();
            callerPhoneNumber = bundle.getString("incoming_number");

        }else{
            Log.d("missX: ", " else:EXTRA_STATE_RINGING ");
            callerPhoneNumber = intent.getExtras().getString("incoming_number");
        }


        // If incoming call is received
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // received
            Log.d("missX: ", " if:EXTRA_STATE_OFFHOOK ");
            callReceived = true;
        }else{
            // not received
            Log.d("missX: ", " else:EXTRA_STATE_OFFHOOK ");

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                Log.d("missX: ", "if:EXTRA_STATE_IDLE");
                //
                if(callReceived==true){
                    //
                    Log.d("missX:","if: callReceived==true");

                    if(ring == true) {
                        if (showReminder) {
                            onIncomingCallStarted(mContext, callerPhoneNumber);
                        }
                    }
                    ring = false;
                }else{
                    Log.d("missX:","else: callReceived==true");

                    if(ring == true){
                     //
                        Log.d("missX:","if: ring==true");
                        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        String name = getName(number,mContext);
                        //Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
                        DbHelper dbHelper = new DbHelper(mContext);
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        dbHelper.saveNumber(number, name, database);
                        dbHelper.close();
                        //Log.d("Inside If: ", ""+callReceived);

                        Intent intent1 = new Intent(DbContract.UPDATE_UI_FILTER);
                        mContext.sendBroadcast(intent1);
                     //
                        //onIncomingCallStarted(mContext, callerPhoneNumber);
                    }else{
                        Log.d("missX:","else: ring==true");
                    }
                    //callReceived = false;
                    ring = false;
                }
                //
            }else{
                Log.d("missX: ", ""+"else:EXTRA_STATE_IDLE");

            }
            //
            callReceived = false;
        }
        /*
        // If phone is Idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
            //if (ring == true && callReceived == false) {
            Log.d("Before If: ", ""+callReceived);
            if (callReceived == false) {
                Log.d("Inside1 If: ", ""+callReceived);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                DbHelper dbHelper = new DbHelper(mContext);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.saveNumber(number,database);
                dbHelper.close();
                Log.d("Inside If: ", ""+callReceived);

                Intent intent1 = new Intent(DbContract.UPDATE_UI_FILTER);
                mContext.sendBroadcast(intent1);
            }else{
                Log.d("Else: ", ""+"callReceived==false");
            }
        }else{
            Log.d("Else: ", ""+"EXTRA_STATE_IDLE");
        }
        */
    }

    private String getName(String number, Context context){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName ="";
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor.getCount()>0){
            if (cursor.moveToNext()){
                contactName = cursor.getString(0);
                return contactName;
            }else {
                return "New Contact";
            }
        }else {
            return "New Contact";
        }
    }

    protected void onIncomingCallStarted(Context ctx, String number){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
}
