package com.example.FitFlow;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class exerciseDetails extends AppCompatActivity {

    private TextView exerciseNameTv, caloriesTv, timeTv, descTv;
    private LottieAnimationView exerciseLAV;
    private String exerciseName, desc, imgUrl;
    private int calories, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);
        exerciseNameTv = findViewById(R.id.exerciseName);
        caloriesTv = findViewById(R.id.tvCalories);
        timeTv = findViewById(R.id.tvTime);
        descTv = findViewById(R.id.tvDescription);
        exerciseLAV = findViewById(R.id.ExerciseAnim);

        exerciseName = getIntent().getStringExtra("exerciseName");
        desc = getIntent().getStringExtra("description");
        imgUrl = getIntent().getStringExtra("imgUrl");
        calories = getIntent().getIntExtra("calories", 0);
        time = getIntent().getIntExtra("time", 0 );

        exerciseNameTv.setText(exerciseName);
        caloriesTv.setText("Calories :" + calories);
        timeTv.setText("Time " + "Min");
        descTv.setText(desc);
        exerciseLAV.setAnimationFromUrl(imgUrl);




    }
}