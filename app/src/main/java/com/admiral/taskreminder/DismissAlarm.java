package com.admiral.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissAlarm extends BroadcastReceiver {
    MPlayer mPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("notes");
        int id = intent.getIntExtra("id",0);
        double lat = intent.getDoubleExtra("lat",0);
        double lon = intent.getDoubleExtra("lon",0);
        long start_date = intent.getLongExtra("start_date",0);
        long end_date = intent.getLongExtra("end_date",0);

        //mPlayer.stopMediaPlayer();

        // When notification is tapped, call CreateTaskActivity.
        Intent mainIntent = new Intent(context, CreateTaskActivity.class);

        Log.e("iddad111","::"+id);
        Log.e("iddadNotes","::"+message);
        mainIntent.putExtra("id", id);
        mainIntent.putExtra("notes", message);
        mainIntent.putExtra("lat", lat);
        mainIntent.putExtra("lon", lon);
        mainIntent.putExtra("date_in_ms", start_date);
        mainIntent.putExtra("end_date", end_date);
        mainIntent.putExtra("action", "view");

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }
}