package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fitness1 extends AppCompatActivity {
    private HashMap<String, Boolean> selectedExercises = new HashMap<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitness1);
        setupClickListeners();
        loadLottieAnimations();
        // addExercisesToFirebase();
        initializeCheckBoxes();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        Button btnSave = findViewById(R.id.btnSaveExercises);
        btnSave.setOnClickListener(view -> saveSelectedExercises());
    }
    private void setupClickListeners() {
        findViewById(R.id.sidePlank1).setOnClickListener(this::handleViewClick);
        findViewById(R.id.pushUps2).setOnClickListener(this::handleViewClick);
        findViewById(R.id.Lunges3).setOnClickListener(this::handleViewClick);
        findViewById(R.id.legRaises14).setOnClickListener(this::handleViewClick);
        findViewById(R.id.crunches4).setOnClickListener(this::handleViewClick);
        findViewById(R.id.abdominalCrunches5).setOnClickListener(this::handleViewClick);
        findViewById(R.id.benchDips7).setOnClickListener(this::handleViewClick);
        findViewById(R.id.cocoons8).setOnClickListener(this::handleViewClick);
        findViewById(R.id.lyingLegRaises9).setOnClickListener(this::handleViewClick);
        findViewById(R.id.mountainClimbers10).setOnClickListener(this::handleViewClick);
        findViewById(R.id.pullUps11).setOnClickListener(this::handleViewClick);
        findViewById(R.id.jumpingJacks12).setOnClickListener(this::handleViewClick);
    }
    private void handleViewClick(View view) {
        String exerciseName = getExerciseNameById(view.getId());
        if (exerciseName != null) {
            ExerciseModel exerciseModel = createExerciseModel(exerciseName);
            if (exerciseModel != null) {
                Intent intent = new Intent(Fitness1.this, Fitness2.class);
                intent.putExtra("ExerciseModel", exerciseModel);
                intent.putExtra("exerciseId", exerciseModel.getId());
                startActivity(intent);
            } else {
             //   Toast.makeText(this, "Exercise model not found for " + exerciseName, Toast.LENGTH_SHORT).show();
            }
        } else {
         //   Toast.makeText(this, "Error: Unknown exercise.", Toast.LENGTH_SHORT).show();
        }
    }



    private void initializeCheckBoxes() {
        CheckBox checkBoxPushUps = findViewById(R.id.checkbox_pushUps);
        CheckBox checkBoxSitUps = findViewById(R.id.checkbox_sitUps);
        CheckBox checkBoxLunges = findViewById(R.id.checkbox_lunges);
        CheckBox checkBoxLegRaises = findViewById(R.id.checkbox_legRaises);
        CheckBox checkBoxCrunches = findViewById(R.id.checkbox_crunches);
        CheckBox checkBoxPullUps = findViewById(R.id.checkbox_pullUps);
        CheckBox checkBoxSidePlank = findViewById(R.id.checkbox_sidePlanks);
        CheckBox checkBoxJumpingJacks = findViewById(R.id.checkbox_jumpingJacks);
        CheckBox checkBoxMountainClimbers = findViewById(R.id.checkbox_mountainClimbers);
        CheckBox checkBoxSkipping = findViewById(R.id.checkbox_skipping);
        CheckBox checkBoxCocoons = findViewById(R.id.checkbox_cocoons);
        CheckBox checkBoxBenchDips = findViewById(R.id.checkbox_benchDips);

        checkBoxSidePlank.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Side Plank", isChecked));
        checkBoxPushUps.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Push Ups", isChecked));
        checkBoxSitUps.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Sit Ups", isChecked));
        checkBoxLunges.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Lunges", isChecked));
        checkBoxLegRaises.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Leg Raises", isChecked));
        checkBoxCrunches.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Crunches", isChecked));
        checkBoxPullUps.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Pull Ups", isChecked));
        checkBoxJumpingJacks.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Jumping Jacks", isChecked));
        checkBoxMountainClimbers.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Mountain Climbers", isChecked));
        checkBoxSkipping.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Skipping", isChecked));
        checkBoxCocoons.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Cocoons", isChecked));
        checkBoxBenchDips.setOnCheckedChangeListener((buttonView, isChecked) -> selectedExercises.put("Bench Dips", isChecked));

    }

    private ExerciseModel createExerciseModel(String exerciseName) {
        String scheduledDate  = "Monday";
         String scheduledTime = "08.00";
        switch (exerciseName) {
            case "Push Ups":
                return new ExerciseModel("Push Ups", 60L, getString(R.string.pushUpsIns), getString(R.string.pushUpsBenefits), "lottie/pushups.json",scheduledDate, scheduledTime);
            case "Sit Ups":
                return new ExerciseModel("Sit Ups", 60L, getString(R.string.sitUpsIns), getString(R.string.sitUpsBenefits), "lottie/situps.json",scheduledDate, scheduledTime);
            case "Lunges":
                return new ExerciseModel("Lunges", 60L, getString(R.string.lungesIns), getString(R.string.lungesBenefits), "lottie/lunges.json", scheduledDate, scheduledTime);
            case "Side Plank":
                return new ExerciseModel("Side Plank", 60L, getString(R.string.sidePlankIns), getString(R.string.sidePlankBenefits), "lottie/sideplank.json", scheduledDate, scheduledTime);
            case "Leg Raises":
                return new ExerciseModel("Leg Raises", 60L, getString(R.string.legRaisesIns), getString(R.string.legRaisesBenefits), "lottie/legraises.json",scheduledDate, scheduledTime);
            case "Crunches":
                return new ExerciseModel("Crunches", 60L, getString(R.string.crunchesIns), getString(R.string.crunchesBenefits), "lottie/crunches.json", scheduledDate, scheduledTime);
            case "Pull Ups":
                return new ExerciseModel("Pull Ups", 60L, getString(R.string.pullUpsIns), getString(R.string.pullUpsBenefits), "lottie/pullups.json", scheduledDate, scheduledTime);
            case "Jumping Jacks":
                return new ExerciseModel("Jumping Jacks", 60L, getString(R.string.jumpingJacksIns), getString(R.string.jumpingJacksBenefits), "lottie/jumpingjacks.json", scheduledDate, scheduledTime);
            case "Mountain Climbers":
                return new ExerciseModel("Mountain Climbers", 60L, getString(R.string.mountainClimbersIns), getString(R.string.mountainClimbersBenefits), "lottie/mountainclimbers.json", scheduledDate, scheduledTime);
            case "Skipping":
                return new ExerciseModel("Skipping", 60L, getString(R.string.skippingIns), getString(R.string.skippingBenefits), "lottie/skipping.json", scheduledDate, scheduledTime);
            case "Cocoons":
                return new ExerciseModel("Cocoons", 60L, getString(R.string.cocoonsIns), getString(R.string.cocoonsBenefits), "lottie/cocoons.json",scheduledDate, scheduledTime);
            case "Bench Dips":
                return new ExerciseModel("Bench Dips", 60L, getString(R.string.benchDipsIns), getString(R.string.benchDipsBenefits), "lottie/benchdips.json", scheduledDate, scheduledTime);
            case "Abdominal Crunches":
                return new ExerciseModel("Abdominal Crunches", 60L, getString(R.string.abdominalCrunchesIns), getString(R.string.abdominalCrunchesBenefits), "lottie/abdominalcrunches.json", scheduledDate, scheduledTime);
            case "Lying Leg Raises":
                return new ExerciseModel("Lying Leg Raises", 60L, getString(R.string.lyingLegRaisesIns), getString(R.string.lyingLegRaisesBenefits), "lottie/lyinglegraises.json", scheduledDate, scheduledTime);
            default:
                return null;
        }
    }



    private void saveSelectedExercises() {
        if (userId == null) {
         //   Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userExercisesRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises");

        userExercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, ExerciseModel> currentSavedExercises = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExerciseModel exercise = snapshot.getValue(ExerciseModel.class);
                    if (exercise != null) {
                        currentSavedExercises.put(exercise.getName(), exercise);
                    }
                }

                List<String> newlyAddedExercises = new ArrayList<>();
                List<String> alreadyAddedExercises = new ArrayList<>();

                for (Map.Entry<String, Boolean> entry : selectedExercises.entrySet()) {
                    if (entry.getValue()) {
                        if (!currentSavedExercises.containsKey(entry.getKey())) {
                            ExerciseModel exerciseModel = createExerciseModel(entry.getKey());
                            if (exerciseModel != null) {
                                DatabaseReference newExerciseRef = userExercisesRef.push();
                                newExerciseRef.setValue(exerciseModel);
                                newlyAddedExercises.add(entry.getKey());
                            }
                        } else {
                            alreadyAddedExercises.add(entry.getKey());
                        }
                    }
                }

                if (!newlyAddedExercises.isEmpty()) {
                    String addedExerciseNames = String.join(", ", newlyAddedExercises);
                    Toast.makeText(Fitness1.this, addedExerciseNames + " added successfully!", Toast.LENGTH_SHORT).show();
                }
                if (!alreadyAddedExercises.isEmpty()) {
                    String existingExerciseNames = String.join(", ", alreadyAddedExercises);
                    Toast.makeText(Fitness1.this, existingExerciseNames + " already added.", Toast.LENGTH_SHORT).show();
                }
                if (newlyAddedExercises.isEmpty() && alreadyAddedExercises.isEmpty()) {
                    Toast.makeText(Fitness1.this, "No new exercises selected.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(Fitness1.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ImageButtonClicked(View view) {
        String exerciseName = getExerciseNameById(view.getId());
        if (exerciseName != null) {
            fetchAndStartExercise(exerciseName);
        } else {
        }
    }

    private void addExercisesToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exercises");

        ExerciseModel lunges = new ExerciseModel("Lunges", 60L, getString(R.string.lungesIns), getString(R.string.lungesBenefits), "lottie/lunges.json" );
        ExerciseModel pushUps = new ExerciseModel("Push Ups", 60L, getString(R.string.pushUpsIns), getString(R.string.pushUpsBenefits), "lottie/pushups.json");
        ExerciseModel sidePlank = new ExerciseModel("Side Plank", 60L, getString(R.string.sidePlankIns), getString(R.string.sidePlankBenefits), "lottie/sideplank.json");
        ExerciseModel legRaises = new ExerciseModel("Leg Raises", 60L, getString(R.string.legRaisesIns), getString(R.string.legRaisesBenefits), "lottie/legraises.json");
        ExerciseModel crunches = new ExerciseModel("Crunches", 60L, getString(R.string.crunchesIns), getString(R.string.crunchesBenefits), "lottie/crunches.json");
        ExerciseModel pullUps = new ExerciseModel("Pull Ups", 60L, getString(R.string.pullUpsIns), getString(R.string.pullUpsBenefits), "lottie/pullups.json");
        ExerciseModel jumpingJacks = new ExerciseModel("Jumping Jacks", 60L, getString(R.string.jumpingJacksIns), getString(R.string.jumpingJacksBenefits), "lottie/jumpingjacks.json");
        ExerciseModel sitUps = new ExerciseModel("Sit Ups", 60L, getString(R.string.sitUpsIns), getString(R.string.sitUpsBenefits), "lottie/situps.json");
        ExerciseModel mountainClimbers = new ExerciseModel("Mountain Climbers", 60L, getString(R.string.mountainClimbersIns), getString(R.string.mountainClimbersBenefits), "lottie/mountainclimbers.json");
        ExerciseModel skipping = new ExerciseModel("Skipping", 60L, getString(R.string.skippingIns), getString(R.string.skippingBenefits), "lottie/skipping.json");
        ExerciseModel cocoons = new ExerciseModel("Cocoons", 60L, getString(R.string.cocoonsIns), getString(R.string.cocoonsBenefits), "lottie/cocoons.json");
        ExerciseModel benchDips = new ExerciseModel("Bench Dips", 60L, getString(R.string.benchDipsIns), getString(R.string.benchDipsBenefits), "lottie/benchdips.json");
        ExerciseModel abdominalCrunches = new ExerciseModel("Abdominal Crunches", 60L, getString(R.string.abdominalCrunchesIns), getString(R.string.abdominalCrunchesBenefits), "lottie/abdominalcrunches.json");
        ExerciseModel lyingLegRaises = new ExerciseModel("Lying Leg Raises", 60L, getString(R.string.lyingLegRaisesIns), getString(R.string.lyingLegRaisesBenefits), "lottie/lyinglegraises.json");

        List<ExerciseModel> exercises = Arrays.asList(lunges, pushUps, sidePlank, legRaises, crunches, pullUps, jumpingJacks, sitUps, mountainClimbers, skipping, cocoons, benchDips, abdominalCrunches, lyingLegRaises); // Add more to this list as needed

        for (ExerciseModel exercise : exercises) {
            databaseReference.push().setValue(exercise);
        }
    }


    private void loadLottieAnimations() {
        loadAnimationFromAssets(R.id.sidePlank, "lottie/sideplank.json");
        loadAnimationFromAssets(R.id.pushUps, "lottie/pushups.json");
        loadAnimationFromAssets(R.id.Lunges, "lottie/lunges.json");
        loadAnimationFromAssets(R.id.legRaises, "lottie/legraises.json");
        loadAnimationFromAssets(R.id.crunches, "lottie/crunches.json");
        loadAnimationFromAssets(R.id.pullUps, "lottie/pullups.json");
        loadAnimationFromAssets(R.id.jumpingJacks, "lottie/jumpingjacks.json");
        loadAnimationFromAssets(R.id.sitUps, "lottie/situps.json");
        loadAnimationFromAssets(R.id.mountainClimbers, "lottie/mountainclimbers.json");
        loadAnimationFromAssets(R.id.skipping, "lottie/skipping.json");
        loadAnimationFromAssets(R.id.cocoons, "lottie/cocoons.json");
        loadAnimationFromAssets(R.id.benchDips, "lottie/benchdips.json");
        loadAnimationFromAssets(R.id.AbdominalCrunches, "lottie/abdominalcrunches.json");
        loadAnimationFromAssets(R.id.lyingLegRaises, "lottie/lyinglegraises.json");
    }

    private void loadAnimationFromAssets(int viewId, String fileName) {
        LottieAnimationView lottieView = findViewById(viewId);
        if (lottieView != null) {
            lottieView.setAnimation(fileName);
            lottieView.playAnimation();
        }
    }


    private String getExerciseNameById(int id) {
        String exerciseName = null;
        if (id == R.id.sidePlank1) {
            exerciseName = "Side Plank";
        } else if (id == R.id.pushUps2) {
            exerciseName = "Push Ups";
        } else if (id == R.id.Lunges3) {
            exerciseName = "Lunges";
        } else if (id == R.id.legRaises14) {
            exerciseName = "Leg Raises";
        } else if (id == R.id.crunches4) {
            exerciseName = "Crunches";
        } else if (id == R.id.abdominalCrunches5) {
            exerciseName = "Abdominal Crunches";
        } else if (id == R.id.benchDips7) {
            exerciseName = "Bench Dips";
        } else if (id == R.id.cocoons8) {
            exerciseName = "Cocoons";
        } else if (id == R.id.lyingLegRaises9) {
            exerciseName = "Lying Leg Raises";
        } else if (id == R.id.mountainClimbers10) {
            exerciseName = "Mountain Climbers";
        } else if (id == R.id.pullUps11) {
            exerciseName = "Pull Ups";
        } else if (id == R.id.jumpingJacks12) {
            exerciseName = "Jumping Jacks";
        } else if (id == R.id.sitUps13) {
            exerciseName = "Sit Ups";
        } else if (id == R.id.skipping14) {
            exerciseName = "Skipping";
        } else {
            Log.e("Fitness1", "Error: Unknown view clicked.");
        }
        return exerciseName;
    }




    private void fetchAndStartExercise(String exerciseName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exercises").child(exerciseName);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExerciseModel exercise = dataSnapshot.getValue(ExerciseModel.class);
                if (exercise != null) {
                    Intent intent = new Intent(Fitness1.this, Fitness2.class);
                    intent.putExtra("ExerciseModel", exercise);
                    startActivity(intent);
                } else {
               //     Log.e("Fitness1", "Exercise details not found for " + exerciseName);
             //       Toast.makeText(Fitness1.this, "Exercise details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Fitness1", "Failed to fetch exercise details: " + databaseError.getMessage());
                Toast.makeText(Fitness1.this, "Failed to fetch exercise details.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}