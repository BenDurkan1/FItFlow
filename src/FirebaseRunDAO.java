package com.example.FitFlow;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class FirebaseRunDAO implements RunDAO {

    private final DatabaseReference databaseReference;

    public FirebaseRunDAO(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    // Implement the methods from RunDAO using Firebase operations
    // ...

    // For example:
    @Override
    public void insertRun(Run run) {
        // Implement Firebase insert operation
        // ...
    }

    @Override
    public void deleteRun(Run run) {

    }

    @Override
    public void getAllRunsSortedByDate(DataCallback<List<Run>> callback) {

    }

    @Override
    public void getAllRunsSortedByTimeInMillis(DataCallback<List<Run>> callback) {

    }

    @Override
    public void getAllRunsSortedByCaloriesBurned(DataCallback<List<Run>> callback) {

    }

    @Override
    public void getAllRunsSortedByAvgSpeed(DataCallback<List<Run>> callback) {

    }

    @Override
    public void getAllRunsSortedByDistance(DataCallback<List<Run>> callback) {

    }

    @Override
    public void getTotalTimeInMillis(DataCallback<Long> callback) {

    }

    @Override
    public void getTotalCaloriesBurned(DataCallback<Integer> callback) {

    }

    @Override
    public void getTotalDistance(DataCallback<Integer> callback) {

    }

    @Override
    public void getTotalAvgSpeed(DataCallback<Double> callback) {

    }
    @Override
    public void saveRunToFirebase(Run run, DataCallback<Boolean> callback) {
        String runId = databaseReference.push().getKey();
        if (runId != null) {
            run.setId(runId);
            databaseReference.child(runId).setValue(run)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onDataLoaded(true);
                        } else {
                            callback.onError(task.getException());
                        }
                    });
        } else {
            callback.onError(new RuntimeException("Failed to generate unique ID for run"));
        }
    }
}

    // Other methods...

