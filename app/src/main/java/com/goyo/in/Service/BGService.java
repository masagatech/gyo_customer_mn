package com.goyo.in.Service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.in.MainActivity;
import com.goyo.in.ModelClasses.model_notification;
import com.goyo.in.R;
import com.goyo.in.Utils.Global;
import com.goyo.in.Utils.Preferences;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.Date;

import io.hypertrack.smart_scheduler.SmartScheduler;

import static io.fabric.sdk.android.Fabric.TAG;

public class BGService extends Service implements SmartScheduler.JobScheduledCallback {
    private static final String CHANNEL_ID = "CHAN1";
    public Context context = BGService.this;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (jobScheduler.contains(JOB_ID)) {
            removePeriodicJob();
            // return;
        }

        io.hypertrack.smart_scheduler.Job job = createJob();
        if (job == null) {
//            Toast.makeText(context, "Invalid paramteres specified. " +
//                    "Please try again with correct job params.", Toast.LENGTH_SHORT).show();
            // return;
        }

        // Schedule current created job
        if (jobScheduler.addJob(job)) {
            // Toast.makeText(context, "Job successfully added!", Toast.LENGTH_SHORT).show();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             O.createNotification(BGService.this);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("GoYo VTS")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private static final String JOB_PERIODIC_TASK_TAG = "io.hypertrack.android_scheduler_demo.JobPeriodicTask";
    private static final Integer JOB_ID = 1;
    private long Interval = 20;
    private String intervalInMillisEditText;


    private void removePeriodicJob() {

        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (!jobScheduler.contains(JOB_ID)) {
            Toast.makeText(context, "No job exists with JobID: " + JOB_ID, Toast.LENGTH_SHORT).show();
            return;
        }

        if (jobScheduler.removeJob(JOB_ID)) {
            Toast.makeText(context, "Started!", Toast.LENGTH_SHORT).show();
        }
    }



    private io.hypertrack.smart_scheduler.Job createJob() {
        int jobType = 3;
        boolean isPeriodic = true;
        Long miliSecondsInterval = Interval * 1000;
        String intervalInMillisString = intervalInMillisEditText;
        io.hypertrack.smart_scheduler.Job.Builder
                builder = new io.hypertrack.smart_scheduler.Job.Builder(JOB_ID, this, jobType, JOB_PERIODIC_TASK_TAG)
                .setIntervalMillis(miliSecondsInterval);

        if (isPeriodic) {
            builder.setPeriodic(miliSecondsInterval);
        }

        return builder.build();
    }

    @Override
    public void onJobScheduled(final Context context, io.hypertrack.smart_scheduler.Job job) {
        if (job != null) {
            Toast.makeText(context, "Running", Toast.LENGTH_SHORT).show();
            //code to do the HTTP request
//                    SendDataToServer s = new SendDataToServer();
//                    s.DataSendingJob(context);
//            new Runnable() {
//                @Override
//                public void run() {
            Notify(context);
//                }
//            };
        }
    }


    boolean isNotifyInTransit = false;

    int attemps = 0;

    private void Notify(final Context c) {
    
        if (isNotifyInTransit) {
            if (attemps == 3) {
                attemps = 0;
                isNotifyInTransit = false;
            }
            attemps += 1;
            return;
        }
        //if (wakeLock != null) wakeLock.acquire();
        isNotifyInTransit = true;
        Ion.with(getApplicationContext())
                .load("GET", Global.urls.getNotify.value)
                .addQuery("uid", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID))
                .addQuery("flag", "neworder")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        isNotifyInTransit = false;
                        try {
                            if (result != null) android.util.Log.v("result", result.toString());
                            {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<model_notification>() {
                                }.getType();
                                model_notification m = gson.fromJson(result.get("data").getAsJsonObject()
                                        , listType);


                                try {
                                    if (!m.state) {
                                        return;
                                    }

                                    sendNotification(c, m.data.title, m.data.msg);
//                                    Intent intent = new Intent(c, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    PendingIntent pendingIntent = PendingIntent.getActivity(c, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//                                    /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
//                                    Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);
//
//
//                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, CHANNEL_ID)
//                                            .setSmallIcon(R.drawable.ic_taxi)
//                                            .setContentTitle(m.data.title)
//                                            .setContentText(m.data.msg)
//                                            .setAutoCancel(true)
//                                            .setSound(defaultSoundUri)
//                                            .setContentIntent(pendingIntent);
//                                    NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
//                                    notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }

                        } catch (Exception ea) {
                            ea.printStackTrace();
                        } finally {
                        }
                        isNotifyInTransit = false;
                    }
                });
    }

    private void sendNotification(Context c, String title, String message) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);


        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.newgoyologo : R.drawable.newgoyologo;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(message);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setSound(defaultSoundUri, null);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(c, channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(m, notification);
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setLights(Color.BLUE, 3000, 3000);
            notificationManager.notify(m, notificationBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "temp", Toast.LENGTH_SHORT).show();
        Intent in = new Intent();
        in.setAction("com.goyo.in.BroadCastListners.ServiceRestarter");
        sendBroadcast(in);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Toast.makeText(context, "Killed", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "TASK REMOVED");

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), BGService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }



    @TargetApi(26)
    public static class O {

        public static final String CHANNEL_ID = String.valueOf(5001);

        public static void createNotification(Service context) {
            String channelId = createChannel(context);
            Notification notification = buildNotification(context, channelId);
            context.startForeground(5001, notification);
        }

        private static Notification buildNotification(Service context, String channelId) {
            // Create Pending Intents.
            PendingIntent piLaunchMainActivity = getLaunchActivityPI(context);
            //    PendingIntent piStopService = getStopServicePI(context);

            // Action to stop the service.
//            Notification.Action stopAction =
//                    new Notification.Action.Builder(
//                            STOP_ACTION_ICON,
//                            getNotificationStopActionText(context),
//                            piStopService)
//                            .build();

            // Create a notification.
            return new Notification.Builder(context, channelId)
                    .setContentTitle("GoYo")
                    .setContentText("GoYo1")
                    .setSmallIcon(R.drawable.newgoyologo)
                    //.setActions(stopAction)
                    .setStyle(new Notification.BigTextStyle())
                    .build();
        }

        @NonNull
        private static String createChannel(Service context) {
            // Create a channel.
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence channelName = "Playback channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            return CHANNEL_ID;
        }

        /**
         * Get pending intent to launch the activity.
         */
        private static PendingIntent getLaunchActivityPI(Service context) {
            PendingIntent piLaunchMainActivity;
            {
                Intent iLaunchMainActivity = new Intent(context, MainActivity.class);
                piLaunchMainActivity =
                        PendingIntent.getActivity(context, 5001, iLaunchMainActivity, 0);
            }
            return piLaunchMainActivity;
        }



    }

}
