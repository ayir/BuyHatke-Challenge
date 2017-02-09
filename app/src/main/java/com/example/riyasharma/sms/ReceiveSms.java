package com.example.riyasharma.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.os.Bundle;

import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by RiyaSharma on 06-02-2017.
 */
public class ReceiveSms extends BroadcastReceiver {
    private static final int NOTIFY_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                            notificationIntent, 0);
                    Notification notification = new NotificationCompat.Builder(context)
                            .setContentTitle(senderNum)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setSmallIcon(R.drawable.message)
                            .build();

                    NotificationManager manager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    manager.notify(NOTIFY_ID, notification);

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }

    }


    }
