package com.ray.missreminder.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbHelper;
import com.ray.missreminder.database.IgnoreDbHelper;

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

    SharedPreferences startNamePref;
    SharedPreferences.Editor startNameEditor;


    String startTime;
    String endTime;

    long callTime;
    //multi calls
    static boolean isTalking = false;

    @Override
    public void onReceive(final Context mContext, Intent intent) {

        try {
            //Shared prefernce setting.
            reminderPref = mContext.getSharedPreferences("reminderPreference", 0);
            reminderEditor = reminderPref.edit();
            showReminder = reminderPref.getBoolean("showReminder", true);

            startTimePref = mContext.getSharedPreferences("startTime", 0);
            startTimeEditor = startTimePref.edit();

            startNamePref = mContext.getSharedPreferences("StartName", 0);
            startNameEditor = startNamePref.edit();

            reminderOutgoingPref = mContext.getSharedPreferences("reminderOutgoinfPref", 0);
            reminderOutgoing = reminderOutgoingPref.edit();

            showReminderOutgoing = reminderOutgoingPref.getBoolean("showOutgoingReminder", true);


            Log.d("missX:", "onReceive ============================================================");

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            // If phone state "Ringing"
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                Log.d("missX: ", " if:EXTRA_STATE_RINGING ");

                ring = true;
                // Get the Caller's Phone Number
                Bundle bundle = intent.getExtras();
                callerPhoneNumber = bundle.getString("incoming_number");

            } else {
                Log.d("missX: ", " else:EXTRA_STATE_RINGING ");
                callerPhoneNumber = intent.getExtras().getString("incoming_number");
            }


            // If incoming call is received
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // received

                startTime = "" + System.currentTimeMillis();
                startTimeEditor.clear();
                startTimeEditor.putString("callStartTime", startTime);
                startTimeEditor.commit();


                String oldSaved = startNamePref.getString("callStartNumber", null);
                Log.d("missX", "oldSaved " + oldSaved);

                try {
                    if (oldSaved != null) {
                        Log.d("missX", "Number NOT saved");
                    } else {
                        startNameEditor.clear();
                        startNameEditor.putString("callStartNumber", intent.getExtras().getString("incoming_number"));
                        startNameEditor.commit();
                        Log.d("missX", "Number saved");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Toast.makeText(mContext, ""+startTime , Toast.LENGTH_SHORT).show();

                Log.d("missX: ", " if:EXTRA_STATE_OFFHOOK ");
                callReceived = true;

                //TODO:
                // Multi Call
                // An onGoing call and more calls coming
                if (isTalking) {
                    Log.d("missX: ", "onReceive: " + callerPhoneNumber);

                    // method call when user gets missed call while talking
                    //getMissedCalls(mContext);

                    Log.d("missX", "onReceived");

                    isTalking = false;
                } else {
                /*
                */


                    Log.d("missX", "else: isTalking");
                    isTalking = true;
                }
                //

            } else {
                // not received
                Log.d("missX: ", " else:EXTRA_STATE_OFFHOOK ");

                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    String incomingNumber = startNamePref.getString("callStartNumber", null);

                    if (incomingNumber !=null) {
                        Log.d("missX", incomingNumber + "Same" + callerPhoneNumber);
                        getMissedCalls(mContext);
                    } else {
                        Log.d("missX", incomingNumber + "Not same" + callerPhoneNumber);
                    }

                    //getMissedCalls(mContext);
                    Log.d("missX: ", "if:EXTRA_STATE_IDLE");
                    //
                    if (callReceived == true) {
                        //
                        Log.d("missX:", "EXTRA_STATE_IDLE:if: callReceived==true");
                        //getMissedCalls(mContext);
                        if (ring == true) {
                            Log.d("missX:", "EXTRA_STATE_IDLE:if: callReceived==true :if: ring == true");
                            if (showReminder) {
                                if (!showDialog(mContext,callerPhoneNumber)) {
                                    onIncomingCallStarted(mContext, callerPhoneNumber);
                                }
                            }
                        } else {
                            Log.d("missX:", "EXTRA_STATE_IDLE:if: callReceived==true :else: ring == true");

                            if (showReminderOutgoing) {
                                // Display UI for remind/Ignore
                                if (!showDialog(mContext,callerPhoneNumber)) {
                                    onIncomingCallStarted(mContext, callerPhoneNumber);
                                    Log.d("missX", "ShowReminderOutgoing == true");
                                }
                            } else {
                                Log.d("missX", "ShowReminderOutgoing == false");
                            }
                        }
                        ring = false;
                    } else {
                        Log.d("missX:", "EXTRA_STATE_IDLE:else: callReceived==true");

                        if (ring == true) {

                            // THis part of code gets called when user got a missed call, In this part programmer checks whether missed call number
                            // present in ignore/block list or not if present then it won't add the number to the database otherwise it will add
                            // missed number to the database and display notification to the user.

                            IgnoreDbHelper helper = new IgnoreDbHelper(mContext);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            boolean isPresentInIgnoreList = helper.hasNumber(intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER),db);
                            Log.d("isPresent",""+isPresentInIgnoreList+intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                            helper.close();
                            //
                            if (!isPresentInIgnoreList) {
                                callTime = System.currentTimeMillis();
                                Date date = new Date(callTime);
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                String time = format.format(date);

                                boolean alarmUp = (PendingIntent.getBroadcast(mContext,1001
                                        ,new Intent(mContext, ReceiverAlarm.class),PendingIntent.FLAG_NO_CREATE)!=null);

                                if (!alarmUp){
                                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                    Intent intent1 = new Intent(mContext,ReceiverAlarm.class);
                                    PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext,1001,intent1,0);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(System.currentTimeMillis());

                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*5,alarmIntent);
                                    Log.d("MissX AlarmManager","New Alarm Manager registered");
                                }else {
                                    Log.d("MissX AlarmManager","Alarm manager already registered");
                                }

                                Log.d("missX:", "if: ring==true");
                                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                                String name = getName(number, mContext);
                                //Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
                                DbHelper dbHelper = new DbHelper(mContext);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                dbHelper.saveNumber(number, name, time,database);
                                dbHelper.close();
                                //Log.d("Inside If: ", ""+callReceived);

                                Intent intent1 = new Intent(DbContract.UPDATE_UI_FILTER);
                                mContext.sendBroadcast(intent1);
                            }else {
                                Log.d("missX","number present in ignore list");
                            }
                            //
                            //onIncomingCallStarted(mContext, callerPhoneNumber);
                        } else {
                            Log.d("missX:", "else: ring==true");
                        }
                        //callReceived = false;
                        ring = false;
                    }
                    //
                } else {
                    Log.d("missX: ", "" + "else:EXTRA_STATE_IDLE");

                }
                //
                callReceived = false;
                //
                //isTalking = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // this method fetch name of the caller from content provider and store it in db if there is new number calling then it display name as
    // unknown number.
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
                return "Unknown Number";
            }
        }else {
            return "Unknown Number";
        }
    }


    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat();
        String currentTime = timeFormat.format(calendar.getTime());
        return currentTime;
    }

    //This method return string with current date in MM/dd/yyyy format.
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = timeFormat.format(calendar.getTime());
        return currentDate;
    }

    private void getMissedCalls(Context context) throws SecurityException{
        /*
        This method gets called when user is on calll and there is missed call to user so this method dictate this kind of missed calls and
        store it in reminder list
         */

        String PATH = "content://call_log/calls";
        // Took start time and date when call received from shared pref
        String callStartTime = startTimePref.getString("callStartTime",null);
        long longValue = Long.parseLong(callStartTime);

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
                                callTime = System.currentTimeMillis();
                                Date dat = new Date(callTime);
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                String time = format.format(dat);

                                Log.d("missX","While Calling "+ cursor.getString(0));
                                String name = cursor.getString(0);
                                String number = cursor.getString(1);
                                DbHelper dbHelper = new DbHelper(context);
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                dbHelper.saveNumber(number, name, time,database);
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

        startNameEditor.clear();
        startNameEditor.commit();
    }

    private boolean showDialog(Context context,String number){
        IgnoreDbHelper helper = new IgnoreDbHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        if (helper.hasNumber(number,database)){
            helper.close();
            return true;
        }else {
            helper.close();
            return false;
        }
    }

    private boolean checkDatabaseRecord(Context context){
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        return helper.dbStatus(db);
    }

    protected void onIncomingCallStarted(Context ctx, String number){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
}
