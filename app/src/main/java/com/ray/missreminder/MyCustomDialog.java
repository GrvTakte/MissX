package com.ray.missreminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ray.missreminder.broadcastreceiver.ReceiverAlarm;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbHelper;
import com.ray.missreminder.database.DbNotesHelper;
import com.ray.missreminder.service.ServiceAlarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gaurav on 2/23/2018.
 */

public class MyCustomDialog extends Activity {

    TextView number;
    String phone_no;
    Button remind,ignore;

    EditText notesEditText;

    SharedPreferences notesPref;
    boolean isNotesShow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount=0.0f;
            getWindow().setAttributes(lp);
            setContentView(R.layout.incoming_call_layout);
            initializeContent();

            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            phone_no    =   getIntent().getExtras().getString("phone_no");
            number.setText(""+phone_no +" ?!");

            remind.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    long callTime = System.currentTimeMillis();
                    Date dat = new Date(callTime);
                    SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

                    String time = format.format(dat);
                    String name = getName(phone_no, getApplicationContext());
                    DbHelper helper = new DbHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    helper.saveNumber(phone_no, name, time,database);
                    helper.close();

                    startNotification();
                    Log.d("MissX","startNotification() method called");
                    Intent intent1 = new Intent(DbContract.REFRESH_OUTGOING_LIST);
                    getApplicationContext().sendBroadcast(intent1);


                    if (isNotesShow){
                        String notes = notesEditText.getText().toString();
                        if (!notes.matches("")){
                            DbNotesHelper helper1 = new DbNotesHelper(getApplicationContext());
                            SQLiteDatabase db = helper1.getWritableDatabase();
                            helper1.saveNotes(phone_no,notes,name,db);
                            helper.close();

                            Intent intent2 = new Intent(DbContract.REFRESH_NOTES_UI);
                            getApplicationContext().sendBroadcast(intent2);
                            Log.d("Notes","Notes added");
                        }else {
                            Log.d("Notes","Notes not added because, Notes edit field is empty");
                        }
                    }else {
                        Log.d("Notes","Notes not added");
                    }
                    MyCustomDialog.this.finish();
                }
            });

            if (isNotesShow){
                notesEditText.setVisibility(View.VISIBLE);
            }else {
                notesEditText.setVisibility(View.GONE);
            }


            ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCustomDialog.this.finish();
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
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

    private void startNotification(){
        SharedPreferences preferences = this.getSharedPreferences("notificationSetting",0);

        int minute = preferences.getInt("interval",5);

        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(),1001
                ,new Intent(getApplicationContext(), ReceiverAlarm.class),PendingIntent.FLAG_NO_CREATE)!=null);

        Calendar calendar = Calendar.getInstance();

        if (alarmUp) {
            //Alarm active
            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(getApplicationContext(), ReceiverAlarm.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1001, intent1, 0);
            manager.cancel(alarmIntent);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*minute, alarmIntent);

            Log.d("MissX","Alarm already registered");
        } else {
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(this,ReceiverAlarm.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this,1001,intent1,0);

            calendar.setTimeInMillis(System.currentTimeMillis());

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*minute,alarmIntent);
            Log.d("MissX AlarmManager","New Alarm Manager registered");
        }
    }

    private void initializeContent()
    {
        number   = (TextView) findViewById(R.id.incoming_number);
        remind   = (Button) findViewById(R.id.remind);
        ignore = (Button) findViewById(R.id.ignore);
        notesEditText = (EditText) findViewById(R.id.notes_Text);

        notesPref = this.getSharedPreferences("notes",0);
        isNotesShow = notesPref.getBoolean("showNotes",false);
    }

}
