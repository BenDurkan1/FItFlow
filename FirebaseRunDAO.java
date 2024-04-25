package com.example.FitFlow;

import com.google.firebase.database.DatabaseReference;

public class FirebaseRunDAO implements RunDAO {

    private final DatabaseReference databaseReference;

    public FirebaseRunDAO(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
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



