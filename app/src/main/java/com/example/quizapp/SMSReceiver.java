package com.example.quizapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            // SMS received
            // Send broadcast to MainActivity to load a new question
            Intent questionIntent = new Intent("com.example.quizapp.LOAD_NEW_QUESTION");
            context.sendBroadcast(questionIntent);
        }
    }
}
