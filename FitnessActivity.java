package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class FitnessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

// Initialize the LottieAnimationView instances
        LottieAnimationView exerciseAnim = findViewById(R.id.ExerciseAnim);
        LottieAnimationView exerciseAdd = findViewById(R.id.ExerciseAdd);
        LottieAnimationView exerciseAdd3 = findViewById(R.id.ExerciseAdd3);
        LottieAnimationView exerciseAdd4 = findViewById(R.id.ExerciseAdd4);


        // The URL of the Lottie animation
        String lottieUrl = "https://lottie.host/6a94a05a-be15-4b89-81ee-c9f0f4a67dd7/0aHcu7T8Qo.lottie";

        // Set the animation URL for each LottieAnimationView
        exerciseAnim.setAnimationFromUrl(lottieUrl);
        exerciseAdd.setAnimationFromUrl(lottieUrl);
        exerciseAdd3.setAnimationFromUrl(lottieUrl);
        exerciseAdd4.setAnimationFromUrl(lottieUrl);


        // First button
        Button button1 = findViewById(R.id.my_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button 1 click
                Toast.makeText(FitnessActivity.this, "Button 1 clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FitnessActivity.this, Fitness1.class);
                startActivity(intent);
            }
        });

        // Second button
        Button button2 = findViewById(R.id.my_button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button 2 click
                Toast.makeText(FitnessActivity.this, "Button 2 clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FitnessActivity.this, Fitness2.class);
                startActivity(intent);
            }
        });

        // Third button
        Button nutrition= findViewById(R.id.nutrition_button3);
        nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button 3 click
                Toast.makeText(FitnessActivity.this, "nutrition clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FitnessActivity.this, Food.class);
                startActivity(intent);
            }
        });

        Button savedEx = findViewById(R.id.savedExercises_button3);
        savedEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button 3 click
                Toast.makeText(FitnessActivity.this, "Button 3 clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FitnessActivity.this, SavedExercisesActivity.class); // Change SavedExercisesActivity to your activity name
                startActivity(intent);
            }
        });
    }
}
