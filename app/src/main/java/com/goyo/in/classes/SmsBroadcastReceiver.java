package com.goyo.in.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                if(address.toLowerCase().contains("goyo")){
                    smsMessageStr = smsBody;
                }
            }
            if(!smsMessageStr.equals("")){
                if(smsMessageStr.indexOf("OTP") > -1){
                    int index = smsMessageStr.indexOf(":");
                    String strOtp = smsMessageStr.substring(index+2,index+1+5);
                    Intent intents = new Intent("com.goyo.in.smsveryfy");
                    // You can also include some extra data.
                    intents.putExtra("otp", strOtp);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intents);
                }
            }
            //this will update the UI with message
        }
    }
}
