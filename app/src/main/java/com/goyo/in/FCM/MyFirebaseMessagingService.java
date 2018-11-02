package com.goyo.in.FCM;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.goyo.in.CompleteRide;
import com.goyo.in.MainActivity;
import com.goyo.in.R;
import com.goyo.in.StartRideActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by brittany on 4/3/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static String notifData;
    private String deduct_amount, paid_amount;
    String ride_id, mType = null, mTitle = null, mBody = null;
    public static final String MESSAGE_SUCCESS = "MessageSuccess";
    public static final String RIDE_CANCEL_BY_DRIVER = "RideCancelByDriver";
    public static final String COMPLETE_RIDE = "CompleteRide";
    public static final String MESAGE_ERROR = "MessageError";
    public static final String MESSAGE_NOTIFICATION = "MessageNotification";


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        //notifData= String.valueOf(remoteMessage);

        //if(mType!=null & !mType.isEmpty()){
        ride_id = remoteMessage.getData().get("i_ride_id");
        mType = remoteMessage.getData().get("type");
        mTitle = remoteMessage.getData().get("title");
        mBody = remoteMessage.getData().get("body");

        Log.e("Remote Message", "onMessageReceived: " + remoteMessage.toString());
        Log.e(TAG, "NOTIF TAG : mRide Id= " + ride_id);
        Log.e(TAG, "NOTIF TAG : mType = " + mType);
        Log.e(TAG, "NOTIF TAG : mTitle = " + mTitle);
        Log.e(TAG, "NOTIF TAG : mBody = " + mBody);
        Log.e(TAG, "NOTIF TAG : data = " + remoteMessage.getData());

        //this is added by pratik
        if (mType != null) {
            if (mType.equals("driver_tracking")) {
                String mSubType = remoteMessage.getData().get("subtype");
                if (mSubType.equals("start_trip")) {
                    java.util.Map<java.lang.String, java.lang.String> m = remoteMessage.getData();
                    sendNotificationTrackStartTrip(m);
                }
                return;
            }
        }
        //end

        if (mType != null) {
            if (mType.equalsIgnoreCase("user_ride_start")) {
                SendMessageToDeitician(ride_id);
                return;
            }
            if (mType.equalsIgnoreCase("user_ride_cancel")) {
                SendMessageToMainActivity(mTitle, mBody);
                return;
            }
            if (mType.equalsIgnoreCase("user_ride_complete")) {
                SendMessageToCompleteRide(ride_id);
                return;
            }
        }
        if (mType != null) {
            if (mType.equals("user_ride_complete")) {
                sendNotificationComplete();
            }
            if (mType.equals("user_ride_start")) {
                sendNotification();
            }
            if (mType.equals("user_ride_wallet_payment")) {
                paid_amount = remoteMessage.getData().get("paid_wallet_amount");
                sendNotificationPayment();
            }
            if (mType.equals("user_ride_cancel_charge")) {
                deduct_amount = remoteMessage.getData().get("deduct_amount");
                sendNotificationCancelCharge();
            }
            if (mType.equals("user_ride_cancel")) {
                Log.d(TAG, "data: " + "app close notif");
                sendNotificationRideCancel();
            }

        /*hector*/
            if (mType.equalsIgnoreCase("user_driver_arrived")) {
                sendNotification();
            }
            if (mType.equalsIgnoreCase("user_manual_update")) {
                sendNotificationUserManualUpdate();
            }
            if (mType.equalsIgnoreCase("user_add_money")) {
                sendNotificationUserAddMoney();
            }

        } else {
            Log.e(TAG, "Notification Type = Null");
        }

        // Edited by shine infosoft (add parameters)
        SendMessageNotification(mTitle, mBody);
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "data: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotificationUserAddMoney() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from", "notifyUser_Add_Money");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
      /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(8/* ID of notification */, notificationBuilder.build());
    }


    private void sendNotificationPayment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from", "notifServicePayment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
       /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(01 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotificationCancelCharge() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("i_ride_id", ride_id);
        intent.putExtra("from", "notifServiceRideCancelCharge");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
      /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(02 /* ID of notification */, notificationBuilder.build());
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotificationComplete() {
        Intent intent = new Intent(this, CompleteRide.class);
        intent.putExtra("i_ride_id", ride_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
       /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(03 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification() {
        Intent intent = new Intent(this, StartRideActivity.class);
        intent.putExtra("i_ride_id", ride_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(04 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotificationRideCancel() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
       /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(05, notificationBuilder.build());
         /* 05 is the ID of notification */
    }

    private void SendMessageToDeitician(String rideId) {
        Intent registrationComplete = null;
        try {
            registrationComplete = new Intent(MESSAGE_SUCCESS);
            registrationComplete.putExtra("i_ride_id", rideId);
        } catch (Exception e) {
            registrationComplete = new Intent(MESAGE_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void SendMessageToMainActivity(String mTitle, String mBody) {
        Intent registrationComplete = null;
        try {
            registrationComplete = new Intent(RIDE_CANCEL_BY_DRIVER);
            registrationComplete.putExtra("mTitle", mTitle);
            registrationComplete.putExtra("mBody", mBody);
        } catch (Exception e) {
//            registrationComplete = new Intent(MESAGE_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void SendMessageToCompleteRide(String ride_id) {
        Intent registrationComplete = null;
        try {
            registrationComplete = new Intent(COMPLETE_RIDE);
            Log.e("GCMRegIntentService", "Ride complete");
            registrationComplete.putExtra("i_ride_id", ride_id);
        } catch (Exception e) {
//            registrationComplete = new Intent(MESAGE_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void SendMessageNotification(String title, String body) {
        Intent registrationComplete = null;
        try {
            registrationComplete = new Intent(MESSAGE_NOTIFICATION)
                    .putExtra("i_ride_id", ride_id);

            // edited by Shine Infosoft
            // start
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);

            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            }

            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_taxi)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());

            // End
            //edited by Shine Infosoft

        } catch (Exception e) {
            Log.e("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(MESAGE_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    /*####################################Code By pratik (Don't touch)#######################################*/
    private void sendNotificationTrackStartTrip(java.util.Map<java.lang.String, java.lang.String> getData) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tripid", getData.get("tripid"));
            intent.putExtra("status", getData.get("status"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
           /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
            Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_taxi)
                    .setContentTitle(getData.get("title"))
                    .setContentText(getData.get("body"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*hector*/
    private void sendNotificationUserManualUpdate() {
        Intent intent = new Intent(this, StartRideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
      /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(mTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(07 /* ID of notification */, notificationBuilder.build());
    }
}



