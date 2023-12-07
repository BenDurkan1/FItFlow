package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Path extends AppCompatActivity {

    private LinearLayout exercise,  stepCounter;
    private LottieAnimationView exerciseLAV, counterLAV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        exercise = findViewById(R.id.liftExercise);
        stepCounter = findViewById(R.id.cardioExercise);
        exerciseLAV = findViewById(R.id.idLift);
        counterLAV = findViewById(R.id.idCardio);


        exerciseLAV.setAnimationFromUrl("https://lottie.host/6a94a05a-be15-4b89-81ee-c9f0f4a67dd7/0aHcu7T8Qo.lottie");
        counterLAV.setAnimationFromUrl("https://lottie.host/44309984-f81c-4ecc-8f2e-aa1d8a3c4201/mrkGLrA8FD.lottie");

        exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Path.this,ExerciseActivity.class);
                startActivity(intent);


            }
        });

        stepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Path.this,CounterActivity.class);
                startActivity(intent);
            }
        });
    }
}