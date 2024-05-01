package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
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

public class Fitness2 extends AppCompatActivity {

    private Button startBtn, instructionsButton, benefitsButton;
    private TextView textView1, instructionsText, benefitsText;
    private CountDownTimer countDownTimer;
    private boolean timeRunning = false;
    private String exerciseId;
    private String userId;

    private long timeLeftInMillis = 60000;
    private LottieAnimationView lottieAnimationView;
    private long startTimeInMillis = timeLeftInMillis;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitness2);

        initViews();
        handleIntent();
        restoreState(savedInstanceState);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(100);
        lottieAnimationView = findViewById(R.id.animation_view);
        startBtn = findViewById(R.id.startbtn);
        textView1 = findViewById(R.id.time);
        instructionsButton = findViewById(R.id.instructionsButton);
        instructionsText = findViewById(R.id.instructionsText);
        benefitsButton = findViewById(R.id.benefitsButton);
        benefitsText = findViewById(R.id.benefitsText);
        Button editTimeButton = findViewById(R.id.editTime);
        editTimeButton.setOnClickListener(v -> showTimeEditDialog());
        setupListeners();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
        //    Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ExerciseModel exerciseModel = (ExerciseModel) intent.getSerializableExtra("ExerciseModel");
        boolean isFromSavedExercises = intent.getBooleanExtra("isFromSavedExercises", false);

        if (exerciseModel != null) {
            setExerciseDetails(exerciseModel);
            if (isFromSavedExercises) {
                enableInteractiveFeatures();
                findViewById(R.id.editTime).setVisibility(View.VISIBLE);
            } else {
                disableInteractiveFeatures();
            }
        } else {
        //    Toast.makeText(this, "Exercise data is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setExerciseDetails(ExerciseModel exerciseModel) {
        textView1.setText(exerciseModel.getName());
        instructionsText.setText(exerciseModel.getInstructions());
        benefitsText.setText(exerciseModel.getBenefits());
        lottieAnimationView.setAnimation(exerciseModel.getLottieFile());
        lottieAnimationView.playAnimation();

        if (exerciseModel.getTime() > 0) {
            updateButtonTimeDisplay(exerciseModel.getTime());
        }
    }
    private void handleFromFitness1(Intent intent) {
        // Display only the animation and disable other functionalities
        String animationPath = intent.getStringExtra("animationPath");
        if (animationPath != null) {
            lottieAnimationView.setAnimation(animationPath);
            lottieAnimationView.playAnimation();
        } else {
            Log.e("Fitness2", "Animation path not provided");
            Toast.makeText(this, "Animation path missing", Toast.LENGTH_SHORT).show();
        }
        disableInteractiveFeatures(); // Disables buttons, etc.
    }
    private void handleFromSavedExercises(Intent intent) {
        fetchInitialExerciseData();
        enableInteractiveFeatures();  // Enables buttons, timers, etc.
    }

    private void setupDefaultBehavior() {
        fetchInitialExerciseData();
        enableInteractiveFeatures();
    }


    private void disableInteractiveFeatures() {
        startBtn.setVisibility(View.GONE);
        instructionsButton.setVisibility(View.VISIBLE);
        benefitsButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.editTime).setVisibility(View.GONE);
        textView1.setVisibility(View.GONE);
    }

    private void enableInteractiveFeatures() {
        startBtn.setVisibility(View.VISIBLE);
        instructionsButton.setVisibility(View.VISIBLE);
        benefitsButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.editTime).setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
    }


    private void showTimeEditDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Set New Time")
                .setMessage("Enter the time in seconds:")
                .setView(input)
                .setPositiveButton("Set", (dialog, which) -> {
                    try {
                        int newTimeInSeconds = Integer.parseInt(input.getText().toString());
                        if (timeRunning) {
                            countDownTimer.cancel();
                            timeLeftInMillis = newTimeInSeconds * 1000;
                            startTimer();
                        } else {
                            timeLeftInMillis = newTimeInSeconds * 1000;
                            updateTimer();
                        }
                        updateExerciseTimeInFirebase(newTimeInSeconds);
                    } catch (NumberFormatException e) {
                      //  Toast.makeText(Fitness2.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void fetchInitialExerciseData() {
        if (exerciseId == null || userId == null) {
         //   Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises")
                .child(exerciseId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                //    Toast.makeText(Fitness2.this, "No exercise data found", Toast.LENGTH_SHORT).show();
                } else {
                    ExerciseModel exercise = dataSnapshot.getValue(ExerciseModel.class);
                    if (exercise != null) {
                        setAnimationAndText(exercise);
                    } else {
                 //       Toast.makeText(Fitness2.this, "Failed to parse exercise data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        //        Toast.makeText(Fitness2.this, "Failed to load exercise data", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis", 60000);
            timeRunning = savedInstanceState.getBoolean("timeRunning", false);
            if (timeRunning) {
                startTimer();
            } else {
                updateTimer();
            }
        }
    }


    private void showTimerDisabledMessage() {
        startBtn.setEnabled(false);
        Toast.makeText(this, "Timer cannot begin on this page. Please see Saved Exercises.", Toast.LENGTH_LONG).show();
    }

    private void enableTimer() {
        startBtn.setEnabled(true);
    }

    private void setAnimationAndText(ExerciseModel exerciseModel) {
        currentExercise = exerciseModel;

        String animationPath = exerciseModel.getLottieFile();
        lottieAnimationView.setAnimation(animationPath);
        lottieAnimationView.playAnimation();

        instructionsText.setText(exerciseModel.getInstructions());
        benefitsText.setText(exerciseModel.getBenefits());

        long timeInSeconds = exerciseModel.getTime();
        updateButtonTimeDisplay(timeInSeconds);
    }


    private void updateButtonTimeDisplay(long timeInSeconds) {
        long minutes = timeInSeconds / 60;
        long seconds = timeInSeconds % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView1.setText(timeFormatted);
        timeLeftInMillis = timeInSeconds * 1000;
        startTimeInMillis = timeLeftInMillis;
        updateTimer();
    }


    private void setupListeners() {
        startBtn.setOnClickListener(v -> toggleTimer());
        instructionsButton.setOnClickListener(v -> toggleVisibility(instructionsText));
        benefitsButton.setOnClickListener(v -> toggleVisibility(benefitsText));
    }

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
                int progress = (int) (100 * millisUntilFinished / startTimeInMillis);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                timeRunning = false;
                updateTimer();
                progressBar.setProgress(0);
                startBtn.setText(R.string.start);
                showCompletionDialog();
            }
        };
        countDownTimer.start();
        timeRunning = true;
        startBtn.setText(R.string.pause);
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(Fitness2.this)
                .setTitle("Exercise Completed")
                .setMessage("Your exercise has been completed and saved.")
                .setPositiveButton("OK", (dialog, which) -> saveExerciseData())
                .show();
    }
            private ExerciseModel currentExercise;
    private void fetchAndSaveCompletedExercise(String exerciseName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference refSelected = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises");

        refSelected.orderByChild("name").equalTo(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ExerciseModel exercise = snapshot.getValue(ExerciseModel.class);
                        if (exercise != null) {
                            String exerciseId = snapshot.getKey();
                            fetchAndCombineExerciseDetails(userId, exerciseId);
                            break;
                        }
                    }
                } else {
              //      Toast.makeText(Fitness2.this, "Exercise not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Fitness2.this, "Failed to fetch details from SelectedExercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchAndCombineExerciseDetails(String userId, String exerciseId) {
        DatabaseReference refSelected = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises")
                .child(exerciseId);

        refSelected.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExerciseModel exercise = dataSnapshot.getValue(ExerciseModel.class);
                if (exercise != null) {

                    if (exercise.getScheduledDate() != null && !exercise.getScheduledDate().isEmpty()) {
                        DatabaseReference refSavedCal = FirebaseDatabase.getInstance().getReference("Registered Users")
                                .child(userId)
                                .child("SavedCalExercises")
                                .child(exercise.getScheduledDate())
                                .child(exercise.getScheduledTime());

                        refSavedCal.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                saveCompletedExercise(exercise);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(Fitness2.this, "Failed to fetch scheduling details.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        saveCompletedExercise(exercise);
                    }
                } else {
                    Toast.makeText(Fitness2.this, "Exercise details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Fitness2.this, "Failed to fetch exercise details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCompletedExercise(ExerciseModel exercise) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("CompletedExercises")
                .child(exercise.getId());

        ref.setValue(exercise)
                .addOnSuccessListener(aVoid -> Toast.makeText(Fitness2.this, "Exercise saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Fitness2.this, "Failed to save exercise.", Toast.LENGTH_SHORT).show());
    }
    private String scheduledDate;
    private String scheduledTime;

    private void saveExerciseData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && currentExercise != null) {

            String userId = user.getUid();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(userId)
                    .child("CompletedExercises")
                    .child(timestamp);

            currentExercise.setScheduledDate(scheduledDate);
            currentExercise.setScheduledTime(scheduledTime);

            ref.setValue(currentExercise)
                    .addOnSuccessListener(aVoid -> {
                       // Toast.makeText(Fitness2.this, "Exercise saved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                       // Toast.makeText(Fitness2.this, "Failed to save exercise.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(Fitness2.this, "No user logged in or no exercise data.", Toast.LENGTH_SHORT).show();
          //  Log.d("Fitness2", "saveExerciseData: User or exercise data is null -> User: " + user + ", Exercise: " + currentExercise);
        }
    }




    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeRunning = false;
        updateTimer();
        startBtn.setText(R.string.start);
    }
    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView1.setText(timeLeftFormatted);
    }

    private void updateExerciseTimeInFirebase(int newTimeInSeconds) {
        if (exerciseId == null || userId == null) {
            Toast.makeText(this, "Invalid data for updating exercise time", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises")
                .child(exerciseId)
                .child("time");

        ref.setValue(newTimeInSeconds)
                .addOnSuccessListener(aVoid -> {
                 //   Toast.makeText(Fitness2.this, "Time updated successfully!", Toast.LENGTH_SHORT).show();
                    timeLeftInMillis = newTimeInSeconds * 1000;
                    startTimeInMillis = timeLeftInMillis;
                    updateTimer();
                })
                .addOnFailureListener(e -> Toast.makeText(Fitness2.this, "Failed to update time.", Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("timeRunning", timeRunning);
    }
}
