package com.example.FitFlow;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    private static final String STEP_COUNT_KEY = "step_count";
    private static final int GOAL_STEP_COUNT = 10000; 

    private ProgressBar progressBar;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int stepCount = 0;
    private TextView steps;
    private double magnitudePrevious = 0;
    private Button buttonStartStop, overallSteps;

   
    private DatabaseReference mDatabase;
    private ValueEventListener stepCountListener;
    private boolean countingEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

      
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        // Initialize UI components
        steps = findViewById(R.id.steps);
        progressBar = findViewById(R.id.progressBar);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        buttonStartStop = findViewById(R.id.buttonStartStop);
        overallSteps = findViewById(R.id.overallSteps);


        overallSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, StepTrackerActivity.class);
                startActivity(intent);
            }
        });


 
        if (savedInstanceState != null) {
            stepCount = savedInstanceState.getInt(STEP_COUNT_KEY);
            steps.setText(String.valueOf(stepCount));
        }

        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStepCounting();
            }
        });

        retrieveStepCountFromDatabase();
    }

    private void toggleStepCounting() {
        if (!countingEnabled) {
            startCounting();
            buttonStartStop.setText("Stop Counting");
        } else {
            stopCounting();
            buttonStartStop.setText("Start Counting");
        }
        countingEnabled = !countingEnabled;
    }

    private void startCounting() {
        // Register the sensor listener
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopCounting() {
        // Unregister the sensor listener
        sensorManager.unregisterListener(stepDetector);
    }

    private final SensorEventListener stepDetector = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent != null) {
                float xAcceleration = sensorEvent.values[0];
                float yAcceleration = sensorEvent.values[1];
                float zAcceleration = sensorEvent.values[2];

                double magnitude = Math.sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration + zAcceleration * zAcceleration);
                double magnitudeDelta = magnitude - magnitudePrevious;
                magnitudePrevious = magnitude;

                if (magnitudeDelta > 6) {
                    stepCount++;
                    steps.setText(String.valueOf(stepCount));
                    updateProgressBar(); 
                    saveStepCountToDatabase();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void retrieveStepCountFromDatabase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            stepCountListener = mDatabase.child(userID).child("stepCount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        stepCount = snapshot.getValue(Integer.class);
                        steps.setText(String.valueOf(stepCount));
                        updateProgressBar(); 
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void saveStepCountToDatabase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            mDatabase.child(userID).child("stepCounts").child(currentDate).setValue(stepCount);
        }
    }

    private void updateProgressBar() {
        // Calculate progress percentage
        int progress = (int) (((double) stepCount / GOAL_STEP_COUNT) * 100);
        progressBar.setProgress(progress);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_COUNT_KEY, stepCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveStepCountFromDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepCountListener != null) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String userID = firebaseUser.getUid();
                mDatabase.child(userID).child("stepCount").removeEventListener(stepCountListener);
            }
        }
    }
}
