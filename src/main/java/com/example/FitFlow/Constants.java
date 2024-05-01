package com.example.FitFlow.Other;

import android.graphics.Color;

public class Constants {
// Running and Tracking
public static final long TIMER_UPDATE_INTERVAL = 50L;



    public static final String ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE";
    public static final String ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    public static final String ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT";

public static final String SHARED_PREFERENCES_NAME = "sharedPref";

    public static final String NOTIFICATION_CHANNEL_ID = "tracking_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Tracking";
    public static final int NOTIFICATION_ID = 1;


    public static final long LOCATION_UPDATE_INTERVAL = 5000L;
    //2 seconds
    public static final long FASTEST_LOCATION_INTERVAL = 2000L;


    public static final int POLYLINE_COLOR = Color.RED;
    public static final float POLYLINE_WIDTH = 8f;
    public static final float MAP_ZOOM = 15f;

}


