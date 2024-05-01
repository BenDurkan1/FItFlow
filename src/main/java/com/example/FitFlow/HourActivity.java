package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Set;

public class HourActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ExerciseModel> exercises = new ArrayList<>();
    private ExerciseAdapter adapter;
    private Button scheduleButton;
    private String selectedDate, selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour);

        TextView hourDateTextView = findViewById(R.id.hourDateTextView);
        TextView hourTimeTextView = findViewById(R.id.hourTimeTextView);
        scheduleButton = findViewById(R.id.scheduleButton);

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");
        selectedTime = intent.getStringExtra("selectedTime");

        hourDateTextView.setText("Date: " + selectedDate);
        hourTimeTextView.setText("Time: " + selectedTime);

        recyclerView = findViewById(R.id.savedExercisesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(exercises, true);
        recyclerView.setAdapter(adapter);

        loadSavedExercises();

        scheduleButton.setOnClickListener(view -> scheduleExercises());
    }

    private void scheduleExercises() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Set<String> selectedIds = adapter.getSelectedExerciseIds();
            if (!selectedIds.isEmpty()) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                        .child(user.getUid())
                        .child("SavedCalExercises")
                        .child(selectedDate)
                        .child(selectedTime);

                for (ExerciseModel exercise : exercises) {
                    if (selectedIds.contains(exercise.getId())) {
                        exercise.setScheduledDate(selectedDate);
                        exercise.setScheduledTime(selectedTime);
                        ref.child(exercise.getId()).setValue(exercise);
                    }
                }

            //    Toast.makeText(HourActivity.this, "Exercises scheduled successfully!", Toast.LENGTH_SHORT).show();
                redirectToDailyView();
            } else {
            //    Toast.makeText(this, "No exercises selected.", Toast.LENGTH_SHORT).show();
            }
        } else {
          //  Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }




    private void loadSavedExercises() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(userId)
                    .child("SelectedExercises");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    exercises.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ExerciseModel exercise = snapshot.getValue(ExerciseModel.class);
                        if (exercise != null) {
                            exercises.add(exercise);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HourActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }
    private void redirectToDailyView() {
        Intent intent = new Intent(HourActivity.this, DailyViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
