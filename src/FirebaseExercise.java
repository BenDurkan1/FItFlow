package com.example.FitFlow;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseExercise {

    private DatabaseReference mDatabase;

    public FirebaseExercise() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void saveCustomExercisePlan(String userId, List<ExerciseRV> exercises) {
        // Assuming "CustomPlans" is a node under each user
        DatabaseReference planRef = mDatabase.child("Registered Users").child(userId).child("CustomPlans");
        String planId = planRef.push().getKey(); // Generate a unique ID for the new plan
        Map<String, Object> planDetails = new HashMap<>();
        for (ExerciseRV exercise : exercises) {
            String key = planRef.push().getKey(); // Generate a unique key for each exercise
            planDetails.put(key, exercise);
        }
        planRef.child(planId).setValue(planDetails)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseService", "Exercise plan saved successfully."))
                .addOnFailureListener(e -> Log.e("FirebaseService", "Failed to save exercise plan.", e));
    }
}
