package com.example.riyasharma.sms;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by RiyaSharma on 08-02-2017.
 */
public class SendSMS extends AppCompatActivity {
    public static final String ACTION_SMS_SENT = "com.techblogon.android.apis.os.SMS_SENT_ACTION";
    EditText recipientTextEdit = null;
    EditText contentTextEdit = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        recipientTextEdit = (EditText) this
                .findViewById(R.id.editTextEnterReceipents);
        contentTextEdit = (EditText) this
                .findViewById(R.id.editTextCompose);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_sms, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void onClickSend(View v) {
        //sms body coming from user input
        String strSMSBody = contentTextEdit.getText().toString().trim();
        //sms recipient added by user from the activity screen
        String strReceipentsList = recipientTextEdit.getText().toString().trim();
        if (strSMSBody.length()> 0 && strReceipentsList.length()>0) {
            SmsManager sms = SmsManager.getDefault();
            List<String> messages = sms.divideMessage(strSMSBody);
            for (String message : messages) {
                sms.sendTextMessage(strReceipentsList, null, message, PendingIntent.getBroadcast(
                        this, 0, new Intent(ACTION_SMS_SENT), 0), null);
            }
        } else {
            Toast.makeText(SendSMS.this, "Invalid Parameters", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}







