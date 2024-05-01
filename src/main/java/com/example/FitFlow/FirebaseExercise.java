package com.example.FitFlow;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseExercise {

    private DatabaseReference mDatabase;

    public FirebaseExercise() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


}
