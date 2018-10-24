package com.goyo.in.BroadCastListners;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.goyo.in.MainActivity;
import com.goyo.in.Service.BGService;

import static com.koushikdutta.async.AsyncServer.LOGTAG;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(LOGTAG, "ServeiceDestroy onReceive...");
            Log.d(LOGTAG, "action:" + intent.getAction());
            Log.d(LOGTAG, "ServeiceDestroy auto start service...");
            Toast.makeText(context, "killed", Toast.LENGTH_SHORT).show();
            if (!isMyServiceRunning(BGService.class, context)) {

                Intent myService = new Intent(context, BGService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
        } catch (Exception ex) {

        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}