package com.example.FitFlow.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.FitFlow.Run;
import com.example.FitFlow.StepCount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MainRepository {

    private DatabaseReference runsRef;
    private final FirebaseAuth firebaseAuth;
    private DatabaseReference stepCountsRef;

    private FirebaseAuth.AuthStateListener authStateListener;
    private final MutableLiveData<Boolean> isAuthenticatedLiveData = new MutableLiveData<>();

    @Inject
    public MainRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        setupAuthStateListener();
    }


    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                runsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                        .child(user.getUid()).child("RunningTable");
                stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                        .child(user.getUid())
                        .child("stepCounts");
                isAuthenticatedLiveData.postValue(true);
            } else {
                runsRef = null;
                stepCountsRef = null;
                isAuthenticatedLiveData.postValue(false);
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public LiveData<Boolean> isUserAuthenticated() {
        return isAuthenticatedLiveData;
    }



    public LiveData<List<StepCount>> fetchStepCounts() {
        MutableLiveData<List<StepCount>> liveData = new MutableLiveData<>();
        if (stepCountsRef != null) {
            stepCountsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<StepCount> stepCounts = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        StepCount step = snapshot.getValue(StepCount.class);
                        if (step != null) {
                            stepCounts.add(step);
                        } else {
                        }
                    }
                    liveData.postValue(stepCounts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    liveData.postValue(null);
                  //  Log.e("Firebase", "Error fetching data: ", databaseError.toException());
                }
            });
        } else {
            liveData.postValue(null);
        }
        return liveData;
    }



    public LiveData<List<Run>> getAllRunsSortedByDate() {
        MutableLiveData<List<Run>> sortedRunsLiveData = new MutableLiveData<>();
        if (runsRef != null) {
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
                }
            });
        } else {
            sortedRunsLiveData.setValue(new ArrayList<>());
        }
        return sortedRunsLiveData;
    }



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
            }
        });
        return sortedRunsLiveData;
    }

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
            }
        });
        return sortedRunsLiveData;
    }

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
            }
        });
        return sortedRunsLiveData;
    }

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
            }
        });
        return sortedRunsLiveData;
    }


    public LiveData<Double> getTotalAvgSpeed() {
        MutableLiveData<Double> totalAvgSpeedLiveData = new MutableLiveData<>();
        if (runsRef != null) {
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
                }
            });
        } else {
            totalAvgSpeedLiveData.setValue(null);
        }
        return totalAvgSpeedLiveData;
    }


    public LiveData<Integer> getTotalDistance() {
        MutableLiveData<Integer> totalDistanceLiveData = new MutableLiveData<>();
        if (runsRef != null) {
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
                }
            });
        } else {
            totalDistanceLiveData.setValue(0);
        }
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
            }
        });
        return totalCaloriesBurnedLiveData;
    }

    public LiveData<Long> getTotalTimeInMillis() {
        MutableLiveData<Long> totalTimeInMillisLiveData = new MutableLiveData<>();
        if (runsRef != null) {
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
                }
            });
        } else {
            Log.e("MainRepository", "Database reference is null.");
        }
        return totalTimeInMillisLiveData;
    }

    public LiveData<List<Run>> getAllRuns() {
        MutableLiveData<List<Run>> allRunsLiveData = new MutableLiveData<>();
        if (runsRef != null) {
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
                }
            });
        } else {
        }
        return allRunsLiveData;
    }







    public LiveData<List<StepCount>> getStepCountsSortedByDate() {
        MutableLiveData<List<StepCount>> liveData = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("stepCounts");

            stepCountsRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<StepCount> stepCounts = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            StepCount step = snapshot.getValue(StepCount.class);
                            if (step != null) {
                                stepCounts.add(step);
                            }
                        }
                        liveData.setValue(stepCounts);
                    } else {
                        liveData.setValue(Collections.emptyList());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching data", databaseError.toException());
                    liveData.setValue(null);
                }
            });
        } else {
            liveData.setValue(null);
        }
        return liveData;
    }

    public void updateStepCount(int additionalSteps) {
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        stepCountsRef.child(currentDate).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                StepCount sc = mutableData.getValue(StepCount.class);
                if (sc == null) {
                    sc = new StepCount(currentDate, currentDate, 0);
                }
                sc.setStepCount(sc.getStepCount() + additionalSteps);
                mutableData.setValue(sc);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                } else {
                }
            }
        });
    }


    public LiveData<Integer> getTotalSteps() {
        MutableLiveData<Integer> totalStepsLiveData = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("stepCounts");
            stepCountsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int sum = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        StepCount step = child.getValue(StepCount.class);
                        if (step != null) {
                            sum += step.getStepCount();
                        }
                    }
                    totalStepsLiveData.setValue(sum);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    totalStepsLiveData.setValue(null);
                }
            });
        }
        return totalStepsLiveData;
    }

    public LiveData<Integer> getDaysCount() {
        MutableLiveData<Integer> daysCountLiveData = new MutableLiveData<>();
        if (stepCountsRef != null) {
            stepCountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashSet<String> uniqueDates = new HashSet<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        StepCount step = snapshot.getValue(StepCount.class);
                        if (step != null && step.getDate() != null) {
                            uniqueDates.add(step.getDate());
                        }
                    }
                    daysCountLiveData.setValue(uniqueDates.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    daysCountLiveData.setValue(null);
                }
            });
        } else {
            daysCountLiveData.setValue(null);
        }
        return daysCountLiveData;
    }


  /*  public void loadSampleData() {
        if (stepCountsRef != null) {
            List<StepCount> sampleSteps = new ArrayList<>();
            sampleSteps.add(new StepCount("20230413", "20230413", 12456));
            sampleSteps.add(new StepCount("20230414", "20230414", 6774));
            sampleSteps.add(new StepCount("20230415", "20230415", 1034));

            for (StepCount stepCount : sampleSteps) {
                stepCountsRef.child(stepCount.getDate()).setValue(stepCount, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("FirebaseError", "Failed to save sample data: " + databaseError.getMessage());
                        } else {
                            Log.d("FirebaseInfo", "Sample data saved successfully");
                        }
                    }
                });
            }
        } else {
            Log.e("FirebaseRef", "stepCountsRef is null, not authenticated or not initialized");
        }
    } */
    public LiveData<Double> getAverageSteps() {
        MutableLiveData<Double> averageStepsLiveData = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference stepCountsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("stepCounts");
            stepCountsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double totalSteps = 0;
                    int totalDays = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        StepCount step = child.getValue(StepCount.class);
                        if (step != null) {
                            totalSteps += step.getStepCount();
                        }
                    }
                    if (totalDays > 0) {
                        double averageSteps = totalSteps / totalDays;
                        averageStepsLiveData.setValue(averageSteps);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    averageStepsLiveData.setValue(null);
                }
            });
        }
        return averageStepsLiveData;
    }


}