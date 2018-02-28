package com.gaurav.missreminder.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.gaurav.missreminder.database.DbContract;
import com.gaurav.missreminder.database.DbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class IncommingCallReceiver extends BroadcastReceiver {

    static boolean ring = false;
    static boolean callReceived = false;
    String callerPhoneNumber;

    SharedPreferences reminderPref, reminderOutgoingPref;
    SharedPreferences.Editor reminderEditor, reminderOutgoing;
    boolean showReminder, showReminderOutgoing;

    SharedPreferences startTimePref;
    SharedPreferences.Editor startTimeEditor;


    String startTime;
    String endTime;

    //multi calls
    static boolean isTalking = false;

    @Override
    public void onReceive(final Context mContext, Intent intent) {

        //Shared prefernce setting.
        reminderPref = mContext.getSharedPreferences("reminderPreference",0);
        reminderEditor = reminderPref.edit();
        showReminder = reminderPref.getBoolean("showReminder",true);

        startTimePref = mContext.getSharedPreferences("startTime",0);
        startTimeEditor = startTimePref.edit();

        reminderOutgoingPref = mContext.getSharedPreferences("reminderOutgoinfPref",0);
        reminderOutgoing = reminderOutgoingPref.edit();
        showReminderOutgoing = reminderOutgoingPref.getBoolean("showOutgoingReminder",true);

        Log.d("missX:","onReceive ============================================================");

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

            startTime = ""+System.currentTimeMillis();
            startTimeEditor.clear();
            startTimeEditor.putString("callStartTime",startTime);
            startTimeEditor.commit();
            //Toast.makeText(mContext, ""+startTime , Toast.LENGTH_SHORT).show();

            Log.d("missX: ", " if:EXTRA_STATE_OFFHOOK ");
            callReceived = true;

            //TODO:
            // Multi Call
            // An onGoing call and more calls coming
            if(isTalking){
                Log.d("missX: ", "onReceive: "+callerPhoneNumber);

                // method call when user gets missed call while talking
                getMissedCalls(mContext);

                /*
                String name = getName(callerPhoneNumber,mContext);
                String number = callerPhoneNumber;
                DbHelper dbHelper = new DbHelper(mContext);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.saveNumber(number, name, database);
                dbHelper.close();
                Intent intent1 = new Intent(DbContract.UPDATE_UI_FILTER);
                mContext.sendBroadcast(intent1);
                */
                isTalking = false;
            }else{
                isTalking = true;
            }
            //

        }else{
            // not received
            Log.d("missX: ", " else:EXTRA_STATE_OFFHOOK ");

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                Toast.makeText(mContext, ""+endTime, Toast.LENGTH_SHORT).show();

                getMissedCalls(mContext);

                Log.d("missX: ", "if:EXTRA_STATE_IDLE");
                //
                if(callReceived==true){
                    //
                    Log.d("missX:","EXTRA_STATE_IDLE:if: callReceived==true");

                    if(ring == true) {
                        Log.d("missX:","EXTRA_STATE_IDLE:if: callReceived==true :if: ring == true");
                        if (showReminder) {
                            onIncomingCallStarted(mContext, callerPhoneNumber);
                        }
                    }else {
                        Log.d("missX:","EXTRA_STATE_IDLE:if: callReceived==true :else: ring == true");

                        if (showReminderOutgoing){
                            // Display UI for remind/Ignore
                            onIncomingCallStarted(mContext,callerPhoneNumber);
                            Log.d("missX","ShowReminderOutgoing == true");
                        }else {
                            Log.d("missX","ShowReminderOutgoing == false");
                        }

                    }
                    ring = false;
                }else{
                    Log.d("missX:","EXTRA_STATE_IDLE:else: callReceived==true");

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
            //
            //isTalking = false;
        }
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


    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat();
        String currentTime = timeFormat.format(calendar.getTime());
        return currentTime;
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = timeFormat.format(calendar.getTime());
        return currentDate;
    }

    private void getMissedCalls(Context context) throws SecurityException{
        String PATH = "content://call_log/calls";
        // Took start time and date when call received from shared pref
        String callStartTime = startTimePref.getString("callStartTime",null);
        long longValue = Long.parseLong(callStartTime);

        //convert long to date
        //Date date5 = new Date(longValue);
       // Log.d("missX", "LongValue"+longValue);
       // Log.d("missX", "startTime"+date5);

        //declare which field we want to fetch
        String[] projection = new String[] { CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE };

        // declare sorting order
        String sortOrder = CallLog.Calls.DATE + " DESC";

        // where clause for query or conditions to filter query
        StringBuffer sb = new StringBuffer();
        sb.append(CallLog.Calls.TYPE).append("=?");

        //Cursor which query the data from content provider i.e Uri and give us result
        Cursor cursor = context.getContentResolver().query(
                Uri.parse(PATH),
                projection,
                sb.toString(),
                new String[] { String.valueOf(CallLog.Calls.MISSED_TYPE)},sortOrder);

        //Check whether cursor empty or not
        if (cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    long date = cursor.getLong(2);
                    String dateFormat = android.text.format.DateFormat.format("MM/dd/yyyy", new Date(date)).toString();
                    String currentDate = getCurrentDate();

                    String timeCompare = android.text.format.DateFormat.format("HH:mm",new Date(date)).toString();
                    String startTimeCompare = android.text.format.DateFormat.format("HH:mm", new Date(longValue)).toString();

                    try {
                        Date date10 = new SimpleDateFormat("HH:mm").parse(timeCompare);
                        Date date11 = new SimpleDateFormat("HH:mm").parse(startTimeCompare);

                        // display only todays missed calls
                        if (currentDate.matches(dateFormat)){

                            // compare time to show missed call while caling.
                            if (date11.after(date10)){

                                Log.d("missX","Before Calling "+ cursor.getString(0));
                            }else {
                                Log.d("missX","While Calling "+ cursor.getString(0));
                                String name = cursor.getString(0);
                                String number = cursor.getString(1);
                                DbHelper dbHelper = new DbHelper(context);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                dbHelper.saveNumber(number, name, database);
                                dbHelper.close();
                                Intent intent1 = new Intent(DbContract.UPDATE_UI_FILTER);
                                context.sendBroadcast(intent1);
                            }
                        }else {

                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
        }else {
            Log.d("missX","No missed call during call recieved");
        }
    }

    protected void onIncomingCallStarted(Context ctx, String number){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
}
