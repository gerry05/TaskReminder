package com.admiral.taskreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.admiral.taskreminder.database.DatabaseHelper;

import java.util.Date;

public class Alarm extends BroadcastReceiver {
    DatabaseHelper myDb;
    // MediaPlayer mediaPlayer;
    MPlayer mPlayer;



    @Override
    public void onReceive(Context context, Intent intent) {
        myDb = new DatabaseHelper(context);
        System.out.println("ALARMMMMM!!!");

        // Get id & message from intent.
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("todo");
        int id = intent.getIntExtra("id", 0);
        double lat = intent.getDoubleExtra("lat", 0);
        double lon = intent.getDoubleExtra("lon", 0);
        long start_date = intent.getLongExtra("start_date", 0);
        long end_date = intent.getLongExtra("end_date", 0);
        String action = intent.getStringExtra("action");

        // When notification is tapped, call DismissAlarm.
        Intent dismissAlarmIntent = new Intent(context, DismissAlarm.class);


        Log.e("iddad", "::" + id);
        Log.e("iddadNotes", "::" + message);
        dismissAlarmIntent.putExtra("id", id);
        dismissAlarmIntent.putExtra("notes", message);
        dismissAlarmIntent.putExtra("lat", lat);
        dismissAlarmIntent.putExtra("lon", lon);
        dismissAlarmIntent.putExtra("start_date", start_date);
        dismissAlarmIntent.putExtra("end_date", end_date);

        // When notification is tapped, call SnoozeAlarm.
        Intent snoozeAlarmIntent = new Intent(context, SnoozeAlarm.class);
        snoozeAlarmIntent.putExtra("id", id);
        snoozeAlarmIntent.putExtra("notes", message);
        snoozeAlarmIntent.putExtra("lat", lat);
        snoozeAlarmIntent.putExtra("lon", lon);
        snoozeAlarmIntent.putExtra("start_date", start_date);
        snoozeAlarmIntent.putExtra("end_date", end_date);

        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, dismissAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent snoozeIntent = PendingIntent.getBroadcast(context, 0, snoozeAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent dismissNotificationIntent = new Intent(context,DismissNotification.class);
        PendingIntent dismissNotification = PendingIntent.getBroadcast(context, 0, dismissNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(action != null){
            myNotificationManager.cancel(id);

        }

        // Prepare notification.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notificationId), "Reminder", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription(message);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setVibrationPattern(new long[]{100, 250});
            notificationChannel.enableVibration(true);
            if (notificationChannel != null) {
                myNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, String.valueOf(notificationId));

        if(new Date().getTime() >= start_date){
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Reminders")
                    .setContentText(message)
                    .addAction(R.drawable.ic_alarm_black,"Snooze in 5 minutes",snoozeIntent)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(dismissNotification)
                    .setFullScreenIntent(contentIntent, true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
        }else{
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Reminders")
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(dismissNotification)
                    .setFullScreenIntent(contentIntent, true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
        }


            if(Globals.lastNotifId != id){
                Globals.lastNotifId = id;
                mPlayer.mediaPlayer(context);
                // Notify
                if (myNotificationManager != null) {
                    myNotificationManager.notify(notificationId, builder.build());
                }
            }else{
                Log.e("datteeee","::"+start_date);
                Log.e("datteeee1","::"+new Date().getTime());
                if(new Date().getTime() >= start_date){
                    Globals.lastNotifId = id;
                    mPlayer.mediaPlayer(context);
                    // Notify
                    if (myNotificationManager != null) {
                        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
                        myNotificationManager.notify(notificationId, builder.build());
                    }
                }
            }
    }



}
