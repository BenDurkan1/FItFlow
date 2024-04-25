package com.example.FitFlow.Services;


import static com.example.FitFlow.Other.Constants.ACTION_PAUSE_SERVICE;
import static com.example.FitFlow.Other.Constants.ACTION_START_OR_RESUME_SERVICE;
import static com.example.FitFlow.Other.Constants.FASTEST_LOCATION_INTERVAL;
import static com.example.FitFlow.Other.Constants.LOCATION_UPDATE_INTERVAL;
import static com.example.FitFlow.Other.Constants.NOTIFICATION_CHANNEL_ID;
import static com.example.FitFlow.Other.Constants.NOTIFICATION_CHANNEL_NAME;
import static com.example.FitFlow.Other.Constants.NOTIFICATION_ID;
import static com.example.FitFlow.Other.Constants.TIMER_UPDATE_INTERVAL;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.FitFlow.Other.Constants;
import com.example.FitFlow.Other.TrackingUtil;
import com.example.FitFlow.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;
@AndroidEntryPoint
public class TrackingService extends LifecycleService {
    private boolean isFirstRun = true;
private boolean serviceKilled = false;
    @Inject
     FusedLocationProviderClient fusedLocationProviderClient;

     MutableLiveData<Long> timeRunInSeconds = new MutableLiveData<>();

    @Inject
    public NotificationCompat.Builder baseNotificationBuilder;

    public NotificationCompat.Builder curNotificationBuilder;
    public static MutableLiveData<Long> timeRunInMillis = new MutableLiveData<>();

    public static MutableLiveData<Boolean> isTracking = new MutableLiveData<>();


    public static MutableLiveData<List<List<LatLng>>> pathPoints = new MutableLiveData<>();

    private void postInitialValues() {
        isTracking.postValue(false);
        pathPoints.postValue(null);
        timeRunInSeconds.postValue(0L);
        timeRunInMillis.postValue(0L);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        curNotificationBuilder = baseNotificationBuilder;
        postInitialValues();

        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        isTracking.observe(this, new Observer<Boolean>() {


            @Override
            public void onChanged(Boolean tracking) {
                updateLocationTracking(tracking);
                updateNotificationTrackingState(tracking);
            }
        });
    }
    private void killService() {
        serviceKilled = true;
        isFirstRun = true;
        pauseService();
        postInitialValues();
        stopForeground(true);
        stopSelf();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_START_OR_RESUME_SERVICE:
                        if (isFirstRun) {
                            startForegroundService();
                            isFirstRun = false;
                        } else {
                            Timber.d("Resuming service...");
                           startTimer();
                        }
                        break;
                    case ACTION_PAUSE_SERVICE:
                        Timber.d("Paused service");
                        pauseService();
                        break;
                    case Constants.ACTION_STOP_SERVICE:
                        Timber.d("Stopped service");
                        killService();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private boolean isTimerEnabled = false;
    private long lapTime = 0L;
    private long timeRun = 0L;
    private long timeStarted = 0L;
    private long lastSecondTimestamp = 0L;

    private void startTimer() {
        addEmptyPolyline();
        isTracking.postValue(true);
        timeStarted = System.currentTimeMillis();
        isTimerEnabled = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTracking.getValue() != null && isTracking.getValue()) {
                    lapTime = System.currentTimeMillis() - timeStarted;
                    timeRunInMillis.postValue(timeRun + lapTime);
                    if (timeRunInMillis.getValue() != null && timeRunInMillis.getValue() >= lastSecondTimestamp + 1000L) {
                        timeRunInSeconds.postValue(timeRunInSeconds.getValue() != null ? timeRunInSeconds.getValue() + 1 : 1);
                        lastSecondTimestamp += 1000L;
                    }

                    try {
                        Thread.sleep(TIMER_UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timeRun += lapTime;
            }
        }).start();
    }

    private void persistPathPoints() {
        SharedPreferences prefs = getSharedPreferences("TrackingServicePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pathPoints.getValue());
        editor.putString("pathPoints", json);
        editor.apply();
    }

    private void pauseService() {
        isTracking.postValue(false);
        isTimerEnabled = false;
    }
    private void updateNotificationTrackingState(boolean isTracking) {
        String notificationActionText = isTracking ? "Pause" : "Resume";

        Intent actionIntent;
        int requestCode;
        if (isTracking) {
            actionIntent = new Intent(this, TrackingService.class);
            actionIntent.setAction(ACTION_PAUSE_SERVICE);
            requestCode = 1;
        } else {
            actionIntent = new Intent(this, TrackingService.class);
            actionIntent.setAction(ACTION_START_OR_RESUME_SERVICE);
            requestCode = 2;
        }
        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                requestCode,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            Field mActionsField = curNotificationBuilder.getClass().getDeclaredField("mActions");
            mActionsField.setAccessible(true);

            mActionsField.set(curNotificationBuilder, new ArrayList<NotificationCompat.Action>());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!serviceKilled) {

            curNotificationBuilder = baseNotificationBuilder
                    .addAction(R.drawable.icons8_pause_60, notificationActionText, pendingIntent);
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build());
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationTracking(boolean isTracking) {
        if (isTracking) {
            if (TrackingUtil.hasLocationPermissions(this)) {
                LocationRequest request = new LocationRequest();
                request.setInterval(LOCATION_UPDATE_INTERVAL);
                request.setFastestInterval(FASTEST_LOCATION_INTERVAL);
                request.setPriority(PRIORITY_HIGH_ACCURACY);
                fusedLocationProviderClient.requestLocationUpdates(
                        request,
                        locationCallback,
                        Looper.getMainLooper()
                );
            }

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
private final LocationCallback locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult result) {
        super.onLocationResult(result);
        if (isTracking.getValue() != null && isTracking.getValue()) {
            for (Location location : result.getLocations()) {
                addPathPoint(location);
                persistPathPoints();
            }
        }
    }
};

    private void addPathPoint(Location location) {
        if (location != null && isTracking.getValue() != null && isTracking.getValue()) {
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());

            List<List<LatLng>> currentPathPoints = new ArrayList<>(pathPoints.getValue() != null ? pathPoints.getValue() : Collections.emptyList());

            if (!currentPathPoints.isEmpty()) {
                currentPathPoints.get(currentPathPoints.size() - 1).add(pos);
            }

            pathPoints.postValue(currentPathPoints);
        }

        }

private void addEmptyPolyline() {
    List<List<LatLng>> currentPathPoints = pathPoints.getValue();
    List<List<LatLng>> updatedPathPoints;

    if (currentPathPoints != null) {
        updatedPathPoints = new ArrayList<>(currentPathPoints);
        updatedPathPoints.add(new ArrayList<>());
    } else {
        updatedPathPoints = Collections.singletonList(new ArrayList<>());
    }

    pathPoints.postValue(updatedPathPoints);
}

    private void startForegroundService() {
startTimer();
        isTracking.setValue(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build());
        timeRunInSeconds.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long timeInSeconds) {
                if (!serviceKilled) {
                    NotificationCompat.Builder notification = curNotificationBuilder;

                    notification.setContentText(TrackingUtil.getFormattedStopWatchTime(timeInSeconds * 1000L, true));

                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                }

            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,

                NotificationManager.IMPORTANCE_LOW
        );
        notificationManager.createNotificationChannel(channel);
    }

    }

