package com.example.remed;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

//this class creates the notification on the created channel below
public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";


    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {

        //this is an attempt to open a class when the user clicks on the notification
        //trying to open EoeIntent.class when the notification is clicked
        Intent EoeIntent = new Intent(this, EoeIntent.class);
        EoeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, EoeIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_android, "View", contentIntent).build();

        //this builds the notification, sets the title, the description, what logo you want to appear with the notification
        //addAction.(action) is an attempt of opening EoeIntent, not high priority
        //AutoCancel allows the alert to disappear by itself or when the user swipes it away
        //below in the comments are some other functions that could work
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("ReMed Notification")
                .setContentText("Take your medicine .")
                .setSmallIcon(R.drawable.ic_android)
                .addAction(action)
                .setAutoCancel(true);


                //.setTimeoutAfter(1000);
                //.setContentIntent(PendingIntent, EoeIntent);
                //.addAction(R.drawable.ic_one, "View", PendingIntent.getActivity(this, 100, EoeIntent,0) );
                //.addAction(R.drawable.ic_one, "Eoe", PendingIntent.getActivity(this,100, EoeIntent, 0));
        
    }
}