package com.example.FitFlow.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.FitFlow.Run;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class MainRepository {

    private final DatabaseReference runsRef;

    @Inject
    public MainRepository() {
        // Get reference to the "runs" node in Firebase Realtime Database
        runsRef = FirebaseDatabase.getInstance().getReference("running_table");
    }

    public void insertRun(Run run) {
        // Generate a unique key for the new run
        String runId = runsRef.push().getKey();
        if (runId != null) {
            // Set the run object under the generated key
            runsRef.child(runId).setValue(run);
        } else {
            Log.e("MainRepository", "Failed to generate a unique key for the new run");
        }
    }

    public void deleteRun(Run run) {
        // Delete the run from the Firebase Realtime Database
        runsRef.child(run.getId()).removeValue();
    }

    // Implement methods for fetching runs sorted by different criteria as needed
    // For example:
    // public LiveData<List<Run>> getAllRunsSortedByDate()
    public LiveData<List<Run>> getAllRunsSortedByDate() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        runsRef.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                Collections.sort(runs, (run1, run2) -> Long.compare(run1.getTimestamp(), run2.getTimestamp()));
                sortedRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return sortedRunsLiveData;
    }
    // public LiveData<List<Run>> getAllRunsSortedByDistance()
    public LiveData<List<Run>> getAllRunsSortedByDistance() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        runsRef.orderByChild("distanceInMeters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                Collections.sort(runs, (run1, run2) -> Integer.compare(run1.getDistanceInMeters(), run2.getDistanceInMeters()));
                sortedRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return sortedRunsLiveData;
    }
    // public LiveData<List<Run>> getAllRunsSortedByTimeInMillis()
    public LiveData<List<Run>> getAllRunsSortedByTimeInMillis() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        runsRef.orderByChild("timeInMillis").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                Collections.sort(runs, (run1, run2) -> Long.compare(run1.getTimeInMillis(), run2.getTimeInMillis()));
                sortedRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return sortedRunsLiveData;
    }
    // public LiveData<List<Run>> getAllRunsSortedByAvgSpeed()
    public LiveData<List<Run>> getAllRunsSortedByAvgSpeed() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        runsRef.orderByChild("avgSpeedInKMH").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                Collections.sort(runs, (run1, run2) -> Float.compare(run1.getAvgSpeedInKMH(), run2.getAvgSpeedInKMH()));
                sortedRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return sortedRunsLiveData;
    }
    // public LiveData<List<Run>> getAllRunsSortedByCaloriesBurned()
    public LiveData<List<Run>> getAllRunsSortedByCaloriesBurned() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        runsRef.orderByChild("caloriesBurned").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                Collections.sort(runs, (run1, run2) -> Integer.compare(run1.getCaloriesBurned(), run2.getCaloriesBurned()));
                sortedRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return sortedRunsLiveData;
    }

    // Additional methods for retrieving aggregated data, if needed
    // For example:
    // public LiveData<Double> getTotalAvgSpeed()
    public LiveData<Double> getTotalAvgSpeed() {
        MutableLiveData<Double> totalAvgSpeedLiveData = new MutableLiveData<>();
        runsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalAvgSpeed = 0;
                int totalRunsCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        totalAvgSpeed += run.getAvgSpeedInKMH();
                        totalRunsCount++;
                    }
                }
                if (totalRunsCount > 0) {
                    totalAvgSpeed /= totalRunsCount;
                }
                totalAvgSpeedLiveData.setValue(totalAvgSpeed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return totalAvgSpeedLiveData;
    }
    // public LiveData<Integer> getTotalDistance()
    // public LiveData<Integer> getTotalCaloriesBurned()
    // public LiveData<Long> getTotalTimeInMillis()
    public LiveData<Integer> getTotalDistance() {
        MutableLiveData<Integer> totalDistanceLiveData = new MutableLiveData<>();
        runsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalDistance = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        totalDistance += run.getDistanceInMeters();
                    }
                }
                totalDistanceLiveData.setValue(totalDistance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return totalDistanceLiveData;
    }

    public LiveData<Integer> getTotalCaloriesBurned() {
        MutableLiveData<Integer> totalCaloriesBurnedLiveData = new MutableLiveData<>();
        runsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalCaloriesBurned = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        totalCaloriesBurned += run.getCaloriesBurned();
                    }
                }
                totalCaloriesBurnedLiveData.setValue(totalCaloriesBurned);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return totalCaloriesBurnedLiveData;
    }

    public LiveData<Long> getTotalTimeInMillis() {
        MutableLiveData<Long> totalTimeInMillisLiveData = new MutableLiveData<>();
        runsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalTimeInMillis = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        totalTimeInMillis += run.getTimeInMillis();
                    }
                }
                totalTimeInMillisLiveData.setValue(totalTimeInMillis);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return totalTimeInMillisLiveData;
    }
    public LiveData<List<Run>> getAllRuns() {
        MutableLiveData<List<Run>> allRunsLiveData = new MutableLiveData<>();
        runsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (run != null) {
                        runs.add(run);
                    }
                }
                allRunsLiveData.setValue(runs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
        return allRunsLiveData;
    }


    // Method to save a run to Firebase Realtime Database
    public void saveRunToFirebase(Run run) {
        // Generate a unique key for the new run
        String runId = runsRef.push().getKey();
        if (runId != null) {
            // Set the run object under the generated key
            runsRef.child(runId).setValue(run);
        } else {
            Log.e("MainRepository", "Failed to generate a unique key for the new run");
        }
    }
}
