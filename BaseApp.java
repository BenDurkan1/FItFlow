package com.example.FitFlow;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent serviceIntent = new Intent(this, StepCountingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}
