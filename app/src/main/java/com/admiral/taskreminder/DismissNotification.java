package com.admiral.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DismissNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
       MPlayer.stopMediaPlayer();
    }
}
