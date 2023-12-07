package com.example.FitFlow;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CounterActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean running = false;

    private TextView stepsTV;
    private FloatingActionButton fab;
    float steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepsTV = findViewById(R.id.Steps);
        fab = findViewById(R.id.FAB);
        stepsTV.setText(String.valueOf(steps));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(running) {
                     running = false;
                     fab.setImageResource(R.drawable.baseline_assistant_direction_24);
                     Toast.makeText(CounterActivity.this, "Counter Paused",Toast.LENGTH_SHORT).show();
                 } else{
                     running = true;
                     fab.setImageResource(R.drawable.baseline_block_24);
                     Toast.makeText(CounterActivity.this, "Counter Started",Toast.LENGTH_SHORT).show();
                 startCounting();
                 }
            }

            private void startCounting() {

                running = true;
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                if(sensor!=null){
                    sensorManager.registerListener(CounterActivity.this,sensor,SensorManager.SENSOR_DELAY_UI);

                }else {
                    Toast.makeText(CounterActivity.this, "Sensor not found..",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(running) {
            steps = steps + event.values[0];
            stepsTV.setText(String.valueOf(steps));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}