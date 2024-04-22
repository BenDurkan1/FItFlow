package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Food extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);

        // Find buttons by their IDs
        Button btnNutrition = findViewById(R.id.btnNutrition);
        Button btnInjuryPrevention = findViewById(R.id.btnInjuryPrevention);
        Button btnGoalSetting = findViewById(R.id.btnGoalSetting);
        Button btnFitnessMyths = findViewById(R.id.btnFitnessMyths);
        Button btnExerciseForm = findViewById(R.id.btnExerciseForm);
        Button btnMindfulness = findViewById(R.id.btnMindfulness);
        Button btnFitnessGoals = findViewById(R.id.btnFitnessGoals);

        // Set click listeners for buttons
        btnNutrition.setOnClickListener(this);
        btnInjuryPrevention.setOnClickListener(this);
        btnGoalSetting.setOnClickListener(this);
        btnFitnessMyths.setOnClickListener(this);
        btnExerciseForm.setOnClickListener(this);
        btnMindfulness.setOnClickListener(this);
        btnFitnessGoals.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Handle click events for buttons
        Intent intent = new Intent(this, TipDetailActivity.class);
        String exerciseName = "";

        if (view.getId() == R.id.btnNutrition) {
            exerciseName = "Nutrition & Healthy Eating";
        } else if (view.getId() == R.id.btnInjuryPrevention) {
            exerciseName = "Injury Prevention and Recovery";
        } else if (view.getId() == R.id.btnGoalSetting) {
            exerciseName = "Motivation and Goal Setting";
        } else if (view.getId() == R.id.btnFitnessMyths) {
            exerciseName = "Fitness Myths";
        } else if (view.getId() == R.id.btnExerciseForm) {
            exerciseName = "Exercise Form and Technique";
        } else if (view.getId() == R.id.btnMindfulness) {
            exerciseName = "Mindfulness & Stress Management";
        } else if (view.getId() == R.id.btnFitnessGoals) {
            exerciseName = "Fitness for Specific Goals";
        } else {
            Log.e("Food", "Error: Unknown button clicked.");
            return;
        }

        // Add the exercise name to the intent and start the activity
        intent.putExtra("ExerciseName", exerciseName);
        startActivity(intent);
    }

}
