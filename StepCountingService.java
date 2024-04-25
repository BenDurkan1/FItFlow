package com.example.FitFlow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCountingService extends Service implements SensorEventListener {
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "step_count_channel";
  //  private static final int GOAL_NOTIFICATION_ID = 124;
    private static final int GOAL_STEP_COUNT = 10000;

    private final IBinder binder = new LocalBinder();
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount = 0;
    private double magnitudePrevious = 0;
    private DatabaseReference stepCountsRef;

    public class LocalBinder extends Binder {
        StepCountingService getService() {
            return StepCountingService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        stepCount = prefs.getInt("stepCount", 0);

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("stepCounts");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event != null) {
            float xAcceleration = event.values[0];
            float yAcceleration = event.values[1];
            float zAcceleration = event.values[2];
            double magnitude = Math.sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration + zAcceleration * zAcceleration);
            double magnitudeDelta = magnitude - magnitudePrevious;
            magnitudePrevious = magnitude;

            if (magnitudeDelta > 6) {
                stepCount++;
                updateStepCount();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateStepCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && stepCountsRef != null) {
            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            StepCount stepCountData = new StepCount(currentDate, currentDate, stepCount);
            stepCountsRef.child(currentDate).setValue(stepCountData);
        }
        saveStepCount(stepCount);
        checkGoalReached();
    }








    private void saveStepCount(int count) {
        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("stepCount", count);
        editor.apply();
    }

    private void checkGoalReached() {
        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        int goal = prefs.getInt("goalStepCount", GOAL_STEP_COUNT);
        boolean goalReached = prefs.getBoolean("goalReached", false);

        if (stepCount >= goal && !goalReached) {
            sendGoalReachedNotification();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("goalReached", true);
            editor.apply();
        }
    }

    private void sendGoalReachedNotification() {
        Handler mainHandler = new Handler(getMainLooper());
        mainHandler.post(() -> Toast.makeText(StepCountingService.this, "Congratulations! You've reached your step goal of " + stepCount + " steps!", Toast.LENGTH_LONG).show());
    }
    private void refreshNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = buildNotification();
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, CounterActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Step Counting Service")
                .setContentText("Steps: " + stepCount)
                .setSmallIcon(R.drawable.icons8_run_48)
                .setContentIntent(contentIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Step Count Service ";
            String description = "Continued step counting";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
