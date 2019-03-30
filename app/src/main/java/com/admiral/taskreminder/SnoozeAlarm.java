package com.admiral.taskreminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Collections;

public class SnoozeAlarm extends BroadcastReceiver {
    MPlayer mPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("notes");
        int id = intent.getIntExtra("id",0);
        double lat = intent.getDoubleExtra("lat",0);
        double lon = intent.getDoubleExtra("lon",0);
        long start_date = intent.getLongExtra("start_date",0);
        long end_date = intent.getLongExtra("end_date",0);

        mPlayer.stopMediaPlayer();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // When notification is tapped, call Alarm.
        Intent mainIntent = new Intent(context, Alarm.class);

        Log.e("iddad111","::"+id);
        Log.e("iddadNotes","::"+message);
        mainIntent.putExtra("notificationId", id);
        mainIntent.putExtra("id", id);
        mainIntent.putExtra("todo", message);
        mainIntent.putExtra("lat", lat);
        mainIntent.putExtra("lon", lon);
        mainIntent.putExtra("start_date", start_date);
        mainIntent.putExtra("end_date", end_date);
        mainIntent.putExtra("action", "view");

        Globals.notificationIdSnoozeList.add(id);
        int occurences = Collections.frequency(Globals.notificationIdSnoozeList,id);
        int snoozeTime = 300000 * occurences;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,start_date + snoozeTime,pendingIntent);

    }
}
