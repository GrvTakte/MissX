package com.ray.missreminder.broadcastreceiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.ray.missreminder.service.ServiceAlarm;

public class ReceiverAlarm extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // This will send notification messageg
        ComponentName componentName = new ComponentName(context.getPackageName(),ServiceAlarm.class.getName());
        startWakefulService(context,(intent.setComponent(componentName)));
        setResultCode(Activity.RESULT_OK);
    }
}
