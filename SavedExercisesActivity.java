package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.adapters.ExerciseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedExercisesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseModel> exercises = new ArrayList<>();
    private String userId; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_exercises);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.savedExercisesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExerciseAdapter(exercises);
        recyclerView.setAdapter(adapter);

        // Correct usage of intent within the click listener
        adapter.setOnItemClickListener(exerciseModel -> {
            Intent intent = new Intent(SavedExercisesActivity.this, Fitness2.class);
            intent.putExtra("ExerciseModel", exerciseModel); // Ensure ExerciseModel implements Serializable properly.
            intent.putExtra("isFromSavedExercises", true); // Indicate access from SavedExercisesActivity
            startActivity(intent);
        });

        loadSavedExercises();


    // Button for navigating to CalendarActivity
        Button calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedExercisesActivity.this, MyCalendarActivity.class);
                startActivity(intent);
            }
        });
}
    private void loadSavedExercises() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ExerciseModel> fetchedExercises = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExerciseModel exercise = snapshot.getValue(ExerciseModel.class);
                    if (exercise != null) {
                        fetchedExercises.add(exercise);
                    }
                }
                adapter.updateData(fetchedExercises);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SavedExercisesActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
