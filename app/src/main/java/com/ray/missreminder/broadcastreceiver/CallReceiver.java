package com.ray.missreminder.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ray.missreminder.MyCustomDialog;


/**
 * Created by Gaurav on 2/23/2018.
 */

public class CallReceiver extends IncommingCallReceiver {
    Context context;

    @Override
    protected void onIncomingCallStarted(final Context ctx, String number)
    {
        context =   ctx;

        final Intent intent = new Intent(context, MyCustomDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("phone_no",number);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                context.startActivity(intent);
            }
        },2000);
    }
}
