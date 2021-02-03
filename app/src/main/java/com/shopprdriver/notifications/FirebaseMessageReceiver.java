package com.shopprdriver.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shopprdriver.Activity.ChatActivity;
import com.shopprdriver.R;
import com.shopprdriver.Session.SessonManager;

import org.json.JSONException;
import org.json.JSONObject;


public class FirebaseMessageReceiver extends FirebaseMessagingService {
    SessonManager sessonManager;
    Uri notification;
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        sessonManager=new SessonManager(this);



        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            JSONObject jsonObject=new JSONObject(remoteMessage.getData());
            //Log.d("ress",""+jsonObject);
            try {
                String caller=jsonObject.getString("caller");
                String token=jsonObject.getString("token");
                String id=jsonObject.getString("id");
                //String user_id=jsonObject.getString("user_id");
                String image=jsonObject.getString("image");
                String channel=jsonObject.getString("channel");
                //sessonManager.setAgoraToken(token);
                sessonManager.setAgoraChanelName(channel);


                int NOTIFICATION_ID = 1;


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(R.drawable.pin_logo);
                builder.setColor(ContextCompat.getColor(this, R.color.colorDarkBlue));
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pin_logo));
                builder.setContentTitle("Notification Actions");
                builder.setContentText("Tap View to launch our website");
                builder.setAutoCancel(true);
                //PendingIntent launchIntent = getLaunchIntent(NOTIFICATION_ID, getBaseContext());

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.journaldev.com"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                Intent buttonIntent = new Intent(getBaseContext(), NotificationReceiver.class);
                buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
                PendingIntent dismissIntent = PendingIntent.getBroadcast(getBaseContext(), 0, buttonIntent, 0);

                //builder.setContentIntent(launchIntent);
                builder.addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent);
                builder.addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // Will display the notification in the notification bar
                notificationManager.notify(NOTIFICATION_ID, builder.build());


                //sessonManager.setAgoraUserid(id);
                //Log.d("ressss",token+channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());


           /* String title=remoteMessage.getNotification().getTitle();
            String body=remoteMessage.getNotification().getBody();
            Log.d("bodyResponse",body+title);*/


            Intent intent = new Intent("message_subject1_intent");
            intent.putExtra("title", remoteMessage.getNotification().getTitle() );
            intent.putExtra("body", remoteMessage.getNotification().getBody());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }
    }

    /*private PendingIntent getLaunchIntent(int notification_id, Context baseContext) {

    }*/

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.mipmap.ic_launcher);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message) {
        //Log.d("title",title);
        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, ChatActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon( R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setSound(notification)
                .setContentIntent(pendingIntent);

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
                getCustomDesign(title, message));
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

        notificationManager.notify(0, builder.build());



    }
}
