package com.ray.missreminder.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.ray.missreminder.MainActivity;
import com.ray.missreminder.R;

public class ServiceAlarm extends Service {

    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification(){
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this, MainActivity.class),0);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("MissX");
        notificationBuilder.setSmallIcon(R.mipmap.logo);
        notificationBuilder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        notificationBuilder.setSound(uri);
        notificationBuilder.setLights(Color.YELLOW,3000,3000);
        notificationBuilder.setContentText("You have missed calls please tap to view list.");

        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0,notificationBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}