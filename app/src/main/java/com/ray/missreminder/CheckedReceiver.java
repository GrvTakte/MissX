package com.ray.missreminder;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CheckedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isServiceRunning(NotifyUser.class, context)) {
            Intent intent1 = new Intent(context, NotifyUser.class);
            context.startService(intent1);
            Log.d("Checked Receiver", "Check service started or not");
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
