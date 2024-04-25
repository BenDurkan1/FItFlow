package com.example.FitFlow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    private static final int GOAL_STEP_COUNT = 10000;
    private static final long STEP_COUNT_DELAY = 1000;

    private ProgressBar progressBar;
    private TextView steps;
    private Button buttonEditGoal, overallSteps;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount = 0;
    private double magnitudePrevious = 0;
    private DatabaseReference stepCountsRef;

    private Handler handler;
    private Runnable stepCountRunnable = new Runnable() {
        @Override
        public void run() {
            updateStepCount();
            handler.postDelayed(this, STEP_COUNT_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);
        buttonEditGoal = findViewById(R.id.buttonEditGoal);
        overallSteps = findViewById(R.id.overallSteps);

        Intent serviceIntent = new Intent(this, StepCountingService.class);
        startService(serviceIntent);

        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        int goalStepCount = prefs.getInt("goalStepCount", GOAL_STEP_COUNT);
        updateGoalOnUI(goalStepCount);
        stepCount = prefs.getInt("stepCount", 0);
        updateStepCountUI();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("stepCounts");
        }

        handler = new Handler();

        buttonEditGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStepGoal();
            }
        });

        overallSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, StepCountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void editStepGoal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set New Goal");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newGoal = Integer.parseInt(input.getText().toString());
                saveGoal(newGoal);
                updateGoalOnUI(newGoal);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void saveGoal(int newGoal) {
        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("goalStepCount", newGoal);
        editor.apply();
    }

    private void updateGoalOnUI(int newGoal) {
        TextView goalText = findViewById(R.id.goalText);
        goalText.setText("Goal: " + newGoal);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(newGoal);

        updateProgressBar();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCounting();
        handler.postDelayed(stepCountRunnable, STEP_COUNT_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("FitFlowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply();

        stopCounting();
        handler.removeCallbacks(stepCountRunnable);
    }

    private void startCounting() {
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopCounting() {
        sensorManager.unregisterListener(stepDetector);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, StepCountingService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("stepCount", stepCount);
        setResult(RESULT_OK, intent);
        finish();
    }

    private final SensorEventListener stepDetector = new SensorEventListener() {
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
                    updateStepCountUI();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void updateStepCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && stepCountsRef != null) {
            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            String id = currentDate;
            StepCount stepCountData = new StepCount(id, currentDate, stepCount);
            stepCountsRef.child(id).setValue(stepCountData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("CounterActivity", "Step count updated successfully for " + currentDate);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("CounterActivity", "Failed to update step count for " + currentDate + ": " + e.getMessage());
                        }
                    });
        }
    }

    private void updateStepCountUI() {
        steps.setText(String.valueOf(stepCount));
        updateProgressBar();
    }

    private void updateProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        int progress = (int) (((double) stepCount / progressBar.getMax()) * 100);
        progressBar.setProgress(progress);
    }

}
