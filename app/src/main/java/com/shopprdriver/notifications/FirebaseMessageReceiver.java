package com.shopprdriver.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;
import com.shopprdriver.Activity.ChatActivity;
import com.shopprdriver.MainActivity;
import com.shopprdriver.R;
import com.shopprdriver.SendBird.BaseApplication;
import com.shopprdriver.Session.SessonManager;

import org.json.JSONException;
import org.json.JSONObject;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FirebaseMessageReceiver extends FirebaseMessagingService {
    SessonManager sessonManager;
    Uri notification;
    String chat_id;
    Intent intent;
    String type;
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        sessonManager=new SessonManager(this);
        Log.d("Lakshmikant", String.valueOf(remoteMessage));

        Log.e("pankaj", remoteMessage.getData().toString());
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());
        }else {
            Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());

           // Log.d( "notificationdata " + remoteMessage.getData().toString());
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage);

            Intent intent = new Intent("message_subject_intent");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        //}
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title, String message, RemoteMessage remoteMessage) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_layout);
        JSONObject jsonObject=new JSONObject(remoteMessage.getData());
        try {
            title=jsonObject.getString("title");
            message=jsonObject.getString("message");
            remoteViews.setTextViewText(R.id.title, title);
            remoteViews.setTextViewText(R.id.messages, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.splash);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title, String message, RemoteMessage remoteMessage) {
        Log.d("ChatNotification",""+remoteMessage.getNotification());
        Log.d("ChatNotificationData",""+remoteMessage.getData());
        //Log.d("title",title);
        // Pass the intent to switch to the MainActivity

        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        Log.d("Chatjson",""+jsonObject);
        try {
             type=jsonObject.getString("type");

            chat_id = jsonObject.getString("chat_id");
            // sessonManager.setChatId(chat_id);
            Log.d("ChatId+",chat_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type!=null&&type.equalsIgnoreCase("pending_order")){
            intent = new Intent(FirebaseMessageReceiver.this, MainActivity.class);
            /*intent.putExtra("chat_id",chat_id);
            intent.putExtra("chat_status","2");*/
            intent.setAction(Intent.ACTION_MAIN);
        }else if (type!=null&&type.equalsIgnoreCase("chat")){
            intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
            intent.putExtra("chat_id",chat_id);
            intent.putExtra("chat_status","2");
            intent.setAction(Intent.ACTION_MAIN);
        }else if (type!=null&&type.equalsIgnoreCase("chat-assigned")){
            intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
            intent.putExtra("chat_id",chat_id);
            intent.putExtra("chat_status","2");
            intent.setAction(Intent.ACTION_MAIN);
        }
        else if (type!=null&&type.equalsIgnoreCase("recharge")){
            intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
            intent.putExtra("chat_id",chat_id);
            intent.putExtra("chat_status","2");
            intent.setAction(Intent.ACTION_MAIN);
        }
        else if (type!=null&&type.equalsIgnoreCase("order")){
            intent = new Intent(FirebaseMessageReceiver.this, ChatActivity.class);
            intent.putExtra("chat_id",chat_id);
            intent.putExtra("chat_status","2");
            intent.setAction(Intent.ACTION_MAIN);
        }


        // Assign channel ID
        String channel_id = "notification_channel";
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags

        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            //r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        @SuppressLint("WrongConstant") NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.splash)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setSound(notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);



        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
                getCustomDesign(title, message,remoteMessage));
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }
        builder.setOngoing(true);
        notificationManager.notify(0, builder.build());



    }
}
