package com.example.FitFlow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StepCountDAO {
    private DatabaseReference databaseReference;

    public StepCountDAO() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
    }

    public void saveStepCount(StepCount stepCount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child(userId).child("stepCounts").child(stepCount.getDate()).setValue(stepCount.getStepCount());
        }
    }

   
    public interface DataStatus {
        void DataIsLoaded(List<StepCount> stepCounts, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public void getStepCounts(DataStatus dataStatus) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference stepCountsRef = databaseReference.child(userId).child("stepCounts");
            stepCountsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<StepCount> stepCounts = new ArrayList<>();
                    List<String> keys = new ArrayList<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        keys.add(keyNode.getKey());
                        StepCount stepCount = keyNode.getValue(StepCount.class);
                        stepCounts.add(stepCount);
                    }
                    dataStatus.DataIsLoaded(stepCounts, keys);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

}
