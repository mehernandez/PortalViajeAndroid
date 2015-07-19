package com.equinox.mariohernandez.portalviaje;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by mariohernandez on 8/07/15.
 */
public class GcmMessageHandler extends IntentService {

    String mes ;
    private Handler handler;

    public GcmMessageHandler(){
        super("GcmMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();


        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title");

        String mes2 = extras.getString("message");

        showNotification(mes,mes2);

     //   showToast();

        Log.v("prueba", "Received : ("+messageType+") "+mes);

        GcmReceiver.completeWakefulIntent(intent);
    }

    public void showToast(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),mes,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    public void showNotification(String title , String text){

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.car_icon) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(text) // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }
}
