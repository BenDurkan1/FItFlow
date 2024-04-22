package com.example.FitFlow;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
public class Fitness2 extends AppCompatActivity {

        private Button startBtn, instructionsButton, benefitsButton;
        private TextView textView1, instructionsText, benefitsText;
        private CountDownTimer countDownTimer;
        private boolean timeRunning = false;
        private long timeLeftInMillis = 60000; // Default to 1 minute
        private boolean isFromSavedExercises;

    private LottieAnimationView lottieAnimationView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fitness2);

            // Initialize views
            lottieAnimationView = findViewById(R.id.animation_view);
            startBtn = findViewById(R.id.startbtn);
            textView1 = findViewById(R.id.time);
            instructionsButton = findViewById(R.id.instructionsButton);
            instructionsText = findViewById(R.id.instructionsText);
            benefitsButton = findViewById(R.id.benefitsButton);
            benefitsText = findViewById(R.id.benefitsText);

            // Check if activity opened from saved exercises

            // Set up the view based on ExerciseModel if passed
            ExerciseModel exerciseModel = (ExerciseModel) getIntent().getSerializableExtra("ExerciseModel");
            if (exerciseModel != null) {
                setAnimationAndText(exerciseModel);
            }
            if (savedInstanceState != null) {
                timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
                timeRunning = savedInstanceState.getBoolean("timeRunning");
                updateTimer();
                if (timeRunning) {
                    startTimer();
                }
            }
            isFromSavedExercises = getIntent().getBooleanExtra("isFromSavedExercises", false);

            // Disable timer if not from saved exercises
            if (isFromSavedExercises) {
                enableTimer();
            } else {
                showTimerDisabledMessage();
            }
        }
    // Restore saved state if exists
    private void enableTimer() {
        startBtn.setEnabled(true);
        setupListeners();
    }


    private void checkExerciseInSaved(String exerciseName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId).child("SelectedExercises");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isExerciseSaved = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExerciseModel model = snapshot.getValue(ExerciseModel.class);
                    if (model != null && exerciseName.equals(model.getName())) {
                        isExerciseSaved = true;
                        break;
                    }
                }

                startBtn.setEnabled(isExerciseSaved);
                if (!isExerciseSaved) {
                    Toast.makeText(Fitness2.this, "This exercise is not in your saved exercises.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void setAnimationAndText(ExerciseModel exerciseModel) {
        if (exerciseModel == null) return;

        String animationPath = exerciseModel.getLottieFile();
        Log.d("Fitness2", "Loading Lottie Animation: " + animationPath);

        // Assuming your Lottie files are correctly named and placed in assets/lottie folder.
        // No need to prepend "lottie/" if your ExerciseModel's lottieFile already contains the full path.
        lottieAnimationView.setAnimation(animationPath);
        lottieAnimationView.playAnimation();

        // Set text views for instructions and benefits
        instructionsText.setText(exerciseModel.getInstructions());
        benefitsText.setText(exerciseModel.getBenefits());
    }


    private void showTimerDisabledMessage() {
        startBtn.setEnabled(false);
        Toast.makeText(this, "Timer cannot begin on this page please see Saved Exercises.", Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateAnimationState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateAnimationState();
    }

    private void updateAnimationState() {
        if (lottieAnimationView != null) {
                lottieAnimationView.playAnimation();
            } else {
                lottieAnimationView.pauseAnimation();
            }
        }


    private void setupListeners() {
        startBtn.setOnClickListener(v -> toggleTimer());
        instructionsButton.setOnClickListener(v -> toggleVisibility(instructionsText));
        benefitsButton.setOnClickListener(v -> toggleVisibility(benefitsText));
    }

    private void setAnimationAndText(String exerciseName) {
        String formattedName = exerciseName.toLowerCase().replaceAll("\\s+", "");
        lottieAnimationView.setAnimation("lottie/" + formattedName + ".json");
        lottieAnimationView.playAnimation();

        setDynamicText(instructionsText, exerciseName + "ins");
        setDynamicText(benefitsText, exerciseName + "benefits");
    }

    private void setDynamicText(TextView textView, String resName) {
        int resId = getResources().getIdentifier(resName, "string", getPackageName());
        textView.setText(resId != 0 ? getString(resId) : getString(R.string.default_instructions));
    }

    // Methods for toggleTimer, startTimer, stopTimer, updateTimer

    private void toggleVisibility(View view) {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void toggleTimer() {
        if (timeRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            public void onFinish() {
                timeRunning = false;
                updateTimer();
                startBtn.setText(getString(R.string.start));
            }
        }.start();

        timeRunning = true;
        startBtn.setText(getString(R.string.pause));
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeRunning = false;
        updateTimer();
        startBtn.setText(getString(R.string.start));
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60; // Correctly calculate seconds
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView1.setText(timeLeftFormatted);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("timeRunning", timeRunning);
    }
}
