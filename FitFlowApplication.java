package com.example.FitFlow;

import android.app.Application;
import android.content.Intent;

public class FitFlowApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Intent serviceIntent = new Intent(this, StepCountingService.class);
        startService(serviceIntent);
    }
}
