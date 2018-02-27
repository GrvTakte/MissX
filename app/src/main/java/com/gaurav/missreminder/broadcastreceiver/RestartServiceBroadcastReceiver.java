package com.gaurav.missreminder.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gaurav.missreminder.NotifyUser;

/**
 * Created by Gaurav on 2/23/2018.
 */

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartServiceBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, NotifyUser.class));;
    }
}
