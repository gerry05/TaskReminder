package com.admiral.taskreminder;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

class Globals {
    public static Location myLocation;
    public static int lastNotifId = 0;
    public static List notificationIdList = new ArrayList();
    public static List notificationIdSnoozeList = new ArrayList();
}
