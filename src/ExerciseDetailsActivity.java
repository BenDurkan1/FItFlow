package com.example.FitFlow;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class ExerciseDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);

        TextView exerciseNameTextView = findViewById(R.id.exerciseName);
        TextView caloriesTextView = findViewById(R.id.tvCalories);
        TextView timeTextView = findViewById(R.id.tvTime);
        TextView descriptionTextView = findViewById(R.id.tvDescription);
        LottieAnimationView exerciseAnimationView = findViewById(R.id.ExerciseAnim);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String exerciseName = extras.getString("exerciseName");
            String imgURL = extras.getString("imgURL");
            int calories = extras.getInt("calories");
            int time = extras.getInt("time");
            int benefitsResId = extras.getInt("benefitsResId");

            exerciseNameTextView.setText(exerciseName);

            if (benefitsResId != 0) {
                descriptionTextView.setText(getString(benefitsResId));
            }

            // Assuming you have a method to load the Lottie animation from URL
            exerciseAnimationView.setAnimationFromUrl(imgURL);
        }
    }
}
