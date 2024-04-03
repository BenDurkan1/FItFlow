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
    private HashMap<String, Boolean> selectedExercises = new HashMap<>(); // Initialize it
    private String userId; // Make sure it's assigned correctly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitness1);
        loadLottieAnimations();
        // addExercisesToFirebase(); // Consider if you really need to call this every time
        initializeCheckBoxes();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null; // Assign user ID

        Button btnSave = findViewById(R.id.btnSaveExercises); // Make sure you have this button in your XML
        btnSave.setOnClickListener(view -> saveSelectedExercises());
    }

    private void initializeCheckBoxes() {
        // Assuming these are your CheckBox IDs
        CheckBox checkBoxPushUps = findViewById(R.id.checkbox_pushUps);
        CheckBox checkBoxSitUps = findViewById(R.id.checkbox_sitUps);
        CheckBox checkBoxLunges = findViewById(R.id.checkbox_lunges);
        CheckBox checkBoxLegRaises = findViewById(R.id.checkbox_legRaises);
        CheckBox checkBoxCrunches = findViewById(R.id.checkbox_crunches);
        CheckBox checkBoxPullUps = findViewById(R.id.checkbox_pullUps);
        CheckBox checkBoxJumpingJacks = findViewById(R.id.checkbox_jumpingJacks);
        CheckBox checkBoxMountainClimbers = findViewById(R.id.checkbox_mountainClimbers);
        CheckBox checkBoxSkipping = findViewById(R.id.checkbox_skipping);
        CheckBox checkBoxCocoons = findViewById(R.id.checkbox_cocoons);
        CheckBox checkBoxBenchDips = findViewById(R.id.checkbox_benchDips);
        // Add more CheckBox initializations here...

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
        // Example of creating an ExerciseModel based on the exercise name
        // You might need a more sophisticated way to manage this mapping,
        // possibly even fetching from Firebase if these details are stored there.
        switch (exerciseName) {
            case "Push Ups":
                return new ExerciseModel("Push Ups", "60", getString(R.string.pushUpsIns), getString(R.string.pushUpsBenefits), "lottie/pushups.json", "Monday", "08:00");
            case "Sit Ups":
                return new ExerciseModel("Sit Ups", "60", getString(R.string.sitUpsIns), getString(R.string.sitUpsBenefits), "lottie/situps.json","Monday", "08:00");
            case "Lunges":
                return new ExerciseModel("Lunges", "60", getString(R.string.lungesIns), getString(R.string.lungesBenefits), "lottie/lunges.json", "Monday", "08:00");
            case "Side Plank":
                return new ExerciseModel("Side Plank", "60", getString(R.string.sidePlankIns), getString(R.string.sidePlankBenefits), "lottie/sideplank.json", "Monday", "08:00");
            case "Leg Raises":
                return new ExerciseModel("Leg Raises", "60", getString(R.string.legRaisesIns), getString(R.string.legRaisesBenefits), "lottie/legraises.json", "Monday", "08:00");
            case "Crunches":
                return new ExerciseModel("Crunches", "60", getString(R.string.crunchesIns), getString(R.string.crunchesBenefits), "lottie/crunches.json", "Monday", "08:00");
            case "Pull Ups":
                return new ExerciseModel("Pull Ups", "60", getString(R.string.pullUpsIns), getString(R.string.pullUpsBenefits), "lottie/pullups.json", "Monday", "08:00");
            case "Jumping Jacks":
                return new ExerciseModel("Jumping Jacks", "60", getString(R.string.jumpingJacksIns), getString(R.string.jumpingJacksBenefits), "lottie/jumpingjacks.json", "Monday", "08:00");
            case "Mountain Climbers":
                return new ExerciseModel("Mountain Climbers", "60", getString(R.string.mountainClimbersIns), getString(R.string.mountainClimbersBenefits), "lottie/mountainclimbers.json", "Monday", "08:00");
            case "Skipping":
                return new ExerciseModel("Skipping", "60", getString(R.string.skippingIns), getString(R.string.skippingBenefits), "lottie/skipping.json", "Monday", "08:00");
            case "Cocoons":
                return new ExerciseModel("Cocoons", "60", getString(R.string.cocoonsIns), getString(R.string.cocoonsBenefits), "lottie/cocoons.json", "Monday", "08:00");
            case "Bench Dips":
                return new ExerciseModel("Bench Dips", "60", getString(R.string.benchDipsIns), getString(R.string.benchDipsBenefits), "lottie/benchdips.json", "Monday", "08:00");
            case "Abdominal Crunches":
                return new ExerciseModel("Abdominal Crunches", "60", getString(R.string.abdominalCrunchesIns), getString(R.string.abdominalCrunchesBenefits), "lottie/abdominalcrunches.json", "Monday", "08:00");
            case "Lying Leg Raises":
                return new ExerciseModel("Lying Leg Raises", "60", getString(R.string.lyingLegRaisesIns), getString(R.string.lyingLegRaisesBenefits), "lottie/lyinglegraises.json", "Monday", "08:00");
            // Add more cases for other exercises
            default:
                return null; // Return null if no matching exercise found
        }
    }


    private void saveSelectedExercises() {
        if (userId == null) {
            Toast.makeText(Fitness1.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userExercisesRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises");

        // Fetch current saved exercises
        userExercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> currentSavedExercises = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExerciseModel exercise = snapshot.getValue(ExerciseModel.class);
                    if (exercise != null) {
                        currentSavedExercises.add(exercise.getName());
                    }
                }

                // Save new selected exercises if they are not already saved
                for (Map.Entry<String, Boolean> entry : selectedExercises.entrySet()) {
                    if (entry.getValue() && !currentSavedExercises.contains(entry.getKey())) {
                        ExerciseModel exerciseModel = createExerciseModel(entry.getKey());
                        if (exerciseModel != null) {
                            userExercisesRef.push().setValue(exerciseModel);
                            Toast.makeText(Fitness1.this, entry.getKey() + " added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (entry.getValue() && currentSavedExercises.contains(entry.getKey())) {
                        Toast.makeText(Fitness1.this, entry.getKey() + " already added.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Fitness1.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExercisesToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exercises");

        // Define exercises
        ExerciseModel lunges = new ExerciseModel("Lunges", "60", getString(R.string.lungesIns), getString(R.string.lungesBenefits), "lottie/lunges.json", "Monday", "08:00");
        ExerciseModel pushUps = new ExerciseModel("Push Ups", "60", getString(R.string.pushUpsIns), getString(R.string.pushUpsBenefits), "lottie/pushups.json", "Monday", "08:00");
        ExerciseModel sidePlank = new ExerciseModel("Side Plank", "60", getString(R.string.sidePlankIns), getString(R.string.sidePlankBenefits), "lottie/sideplank.json", "Monday", "08:00");
        ExerciseModel legRaises = new ExerciseModel("Leg Raises", "60", getString(R.string.legRaisesIns), getString(R.string.legRaisesBenefits), "lottie/legraises.json", "Monday", "08:00");
        ExerciseModel crunches = new ExerciseModel("Crunches", "60", getString(R.string.crunchesIns), getString(R.string.crunchesBenefits), "lottie/crunches.json", "Monday", "08:00");
        ExerciseModel pullUps = new ExerciseModel("Pull Ups", "60", getString(R.string.pullUpsIns), getString(R.string.pullUpsBenefits), "lottie/pullups.json", "Monday", "08:00");
        ExerciseModel jumpingJacks = new ExerciseModel("Jumping Jacks", "60", getString(R.string.jumpingJacksIns), getString(R.string.jumpingJacksBenefits), "lottie/jumpingjacks.json", "Monday", "08:00");
        ExerciseModel sitUps = new ExerciseModel("Sit Ups", "60", getString(R.string.sitUpsIns), getString(R.string.sitUpsBenefits), "lottie/situps.json", "Monday", "08:00");
        ExerciseModel mountainClimbers = new ExerciseModel("Mountain Climbers", "60", getString(R.string.mountainClimbersIns), getString(R.string.mountainClimbersBenefits), "lottie/mountainclimbers.json", "Monday", "08:00");
        ExerciseModel skipping = new ExerciseModel("Skipping", "60", getString(R.string.skippingIns), getString(R.string.skippingBenefits), "lottie/skipping.json", "Monday", "08:00");
        ExerciseModel cocoons = new ExerciseModel("Cocoons", "60", getString(R.string.cocoonsIns), getString(R.string.cocoonsBenefits), "lottie/cocoons.json", "Monday", "08:00");
        ExerciseModel benchDips = new ExerciseModel("Bench Dips", "60", getString(R.string.benchDipsIns), getString(R.string.benchDipsBenefits), "lottie/benchdips.json", "Monday", "08:00");
        ExerciseModel abdominalCrunches = new ExerciseModel("Abdominal Crunches", "60", getString(R.string.abdominalCrunchesIns), getString(R.string.abdominalCrunchesBenefits), "lottie/abdominalcrunches.json", "Monday", "08:00");
        ExerciseModel lyingLegRaises = new ExerciseModel("Lying Leg Raises", "60", getString(R.string.lyingLegRaisesIns), getString(R.string.lyingLegRaisesBenefits), "lottie/lyinglegraises.json", "Monday", "08:00");
        // Add more exercises as needed

        // Add exercises to Firebase
        List<ExerciseModel> exercises = Arrays.asList(lunges, pushUps, sidePlank, legRaises, crunches, pullUps, jumpingJacks, sitUps, mountainClimbers, skipping, cocoons, benchDips, abdominalCrunches, lyingLegRaises); // Add more to this list as needed

        for (ExerciseModel exercise : exercises) {
            databaseReference.push().setValue(exercise);
        }
    }


    private void loadLottieAnimations() {
        // Directly load animations from the assets folder
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

    // Initialize your checkboxes

    public void ImageButtonClicked(View view) {
        String exerciseName = "";

        if (view.getId() == R.id.sidePlank1) {
            exerciseName = "Side Plank";
        } else if (view.getId() == R.id.pushUps2) {
            exerciseName = "Push Ups";
        } else if (view.getId() == R.id.Lunges3) {
            exerciseName = "Lunges";
        } else if (view.getId() == R.id.legRaises14) {
            exerciseName = "Leg Raises";
        } else if (view.getId() == R.id.crunches4) {
            exerciseName = "Crunches";
        } else if (view.getId() == R.id.abdominalCrunches5) {
            exerciseName = "Abdominal Crunches";
        } else if (view.getId() == R.id.benchDips7) {
            exerciseName = "Bench Dips";
        } else if (view.getId() == R.id.cocoons8) {
            exerciseName = "Cocoons";
        } else if (view.getId() == R.id.lyingLegRaises9) {
            exerciseName = "Lying Leg Raises";
        } else if (view.getId() == R.id.mountainClimbers10) {
            exerciseName = "Mountain Climbers";
        } else if (view.getId() == R.id.pullUps11) {
            exerciseName = "Pull Ups";
        } else if (view.getId() == R.id.jumpingJacks12) {
            exerciseName = "Jumping Jacks";
        } else if (view.getId() == R.id.sitUps13) {
            exerciseName = "Sit Ups";
        } else if (view.getId() == R.id.skipping14) {
            exerciseName = "Skipping";
        } else {
            Log.e("Fitness1", "Error: Unknown view clicked.");
            return;
        }

        if (exerciseName != null) {
            Log.d("Fitness1", "Clicked exercise name: " + exerciseName);
            ExerciseModel exerciseModel = createExerciseModel(exerciseName);

            if (exerciseModel != null) {
                Intent intent = new Intent(Fitness1.this, Fitness2.class);
                intent.putExtra("ExerciseModel", exerciseModel);
                intent.putExtra("isFromSavedExercises", false);
                startActivity(intent);
            } else {
                Log.e("Fitness1", "Error: Exercise model not found for name: " + exerciseName);
                Toast.makeText(this, "Error: Exercise model not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Fitness1", "Error: Unknown view clicked.");
            Toast.makeText(this, "Error: Unknown exercise.", Toast.LENGTH_SHORT).show();
        }
    }
}