package com.admiral.taskreminder;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

public class MPlayer {
    public static  MediaPlayer mediaPlayer;

    public static void mediaPlayer( Context context){
        mediaPlayer = MediaPlayer.create(context,Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();

    }
    public static void stopMediaPlayer(){
        mediaPlayer.stop();
    }


}
