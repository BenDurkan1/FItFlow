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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SavedExercisesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseModel> exercises = new ArrayList<>();
    private String userId;
    private boolean isSelectionMode = false;
    private Set<String> selectedExerciseIds = new HashSet<>();

    private Button btnEdit, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_exercises);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;


        recyclerView = findViewById(R.id.savedExercisesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(exercises, false);
        recyclerView.setAdapter(adapter);
        loadSavedExercises();

        btnEdit = findViewById(R.id.btnToggleEdit);
        btnDelete = findViewById(R.id.btnDeleteExercises);

        btnEdit.setOnClickListener(v -> {
            boolean newMode = !adapter.isSelectionMode;
            adapter.setSelectionMode(newMode);
            btnDelete.setVisibility(newMode ? View.VISIBLE : View.GONE);
        });
        Button calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedExercisesActivity.this, CalendarScreen.class);
                startActivity(intent);
            }
        });


        btnDelete.setOnClickListener(v -> deleteSelectedExercises());
        adapter.setOnItemClickListener(exerciseModel -> {
            if (adapter.isSelectionMode) {
                adapter.toggleSelection(exerciseModel.getId());
            } else {
                Intent intent = new Intent(SavedExercisesActivity.this, Fitness2.class);
                intent.putExtra("ExerciseModel", exerciseModel);
                intent.putExtra("exerciseId", exerciseModel.getFirebaseKey());
                intent.putExtra("isFromSavedExercises", true);
                startActivity(intent);
            }
        });
    }

    private void deleteSelectedExercises() {
        List<String> selectedKeys = adapter.getSelectedItems().stream()
                .map(id -> exercises.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(ExerciseModel::getFirebaseKey)
                .collect(Collectors.toList());

        if (selectedKeys.isEmpty()) {
            Toast.makeText(this, "No exercises selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userId)
                .child("SelectedExercises");

        AtomicInteger successfulDeletions = new AtomicInteger(0);

        for (String key : selectedKeys) {
            ref.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (successfulDeletions.incrementAndGet() == selectedKeys.size()) {
                        Toast.makeText(SavedExercisesActivity.this, "Selected exercises deleted successfully.", Toast.LENGTH_SHORT).show();
                        adapter.clearSelections();
                        adapter.setSelectionMode(false);
                        btnDelete.setVisibility(View.GONE);
                        loadSavedExercises();
                    }
                } else {
                    Toast.makeText(SavedExercisesActivity.this, "Failed to delete selected exercises.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    private void loadSavedExercises() {
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
                        exercise.setFirebaseKey(snapshot.getKey()); // Store the Firebase key in the model
                        exercises.add(exercise);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify data changed
                if (!adapter.hasSelectedItems()) {
                    adapter.setSelectionMode(false); // Exit selection mode if no items are selected
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SavedExercisesActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
