package com.equinox.mariohernandez.portalviaje;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by mariohernandez on 8/07/15.
 */
public class GcmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmMessageHandler.class.getName());

        startWakefulService(context,(intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
