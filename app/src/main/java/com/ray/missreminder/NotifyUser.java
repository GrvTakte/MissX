package com.ray.missreminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ray.missreminder.database.DbHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class NotifyUser extends Service {

    public int counter=0;

    public static final long INTERVAL=1000*60;//variable to execute services every 60 second
    //public static final long INTERVAL=10000; //five minutes
    private Handler mHandler=new Handler(); // run on another Thread to avoid crash
    private Timer mTimer=null; // timer handling
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String lastCall;

    public NotifyUser(Context context){
        super();
    }

    public NotifyUser(){
    }

    @Override
    public void onCreate() {
        super.onCreate();

            preferences = this.getSharedPreferences("notificationSetting",0);
            int addinterval = preferences.getInt("interval",5);

            if (addinterval <=0 ){
                addinterval = 5;
            }

            Log.d("Service","onCreate Called");
            TimeDisplayTimerTask timerTask = new TimeDisplayTimerTask();

            //build interval
        if (mTimer != null) {
            mTimer.cancel();
        }else {
            mTimer = new Timer(); // recreate new timer
            mTimer.scheduleAtFixedRate(timerTask, 1000, INTERVAL*addinterval);// schedule task
            Log.d("Service","New timer object created");
        }            //Testing interval
            //mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL);// schedule task
    }

    public boolean dbStatus(){
        DbHelper helper = new DbHelper(getApplicationContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        //Log.d("dbStatus",""+helper.dbStatus(database));
        Log.d("Service","Database status: "+helper.dbStatus(database));
        boolean status = helper.dbStatus(database);
        helper.close();
        return status;
    }

    private void loadNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);
        builder.setSound(uri);
        builder.setContentTitle("Missed");
        builder.setContentText(lastCall);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setSubText("Tap to view complete list");
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());

        Log.d("Service","loadNotification() Called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        //startTimer();
        Log.d("Service","onStartCommand() Called");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("com.gaurav.missreminder.RestartService");
        sendBroadcast(broadcastIntent);
        Log.d("Service","onDestroy() Called");
        mTimer.cancel();
        //stopTimerTask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;


    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,1000,1000);
    }


    public void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("in timer", "in timer ++"+(counter++));
            }
        };
    }

    public void stopTimerTask(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Service","TimeDisplayTimerTask() Called");
                    // display toast at every 10 second
                    if (dbStatus()) {
                        loadNotification();
                    }else {
                    }

                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service","IBinder() Called");
        return null;
    }
}
