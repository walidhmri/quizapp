package com.example.quizapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) || 
                state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // Phone is ringing or call is active
                // Send broadcast to MainActivity to load a new question
                Intent questionIntent = new Intent("com.example.quizapp.LOAD_NEW_QUESTION");
                context.sendBroadcast(questionIntent);
            }
        }
    }
}
