package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.Other.CalendarUtils;
import com.example.FitFlow.adapters.ExerciseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    private AutoCompleteTextView searchAutoCompleteTextView;
    private TextView eventDateDisplay, eventTimeDisplay;
    private LocalDate selectedDate;
    private ExerciseAdapter adapter; // Use the same adapter for both RecyclerViews

    private LocalTime eventTime = LocalTime.now();

    private RecyclerView recyclerView;
    private RecyclerView savedExercisesRecyclerView; // Change the RecyclerView reference
    private List<ExerciseModel> exercises = new ArrayList<>();
    private List<String> exerciseNames = new ArrayList<>();
    private String userId; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        eventDateDisplay = findViewById(R.id.eventDateTV);
        eventTimeDisplay = findViewById(R.id.eventTimeTV);

        // Use intent to pass selectedDate or fallback to current date if not available
        selectedDate = getIntent().hasExtra("selectedDate") ?
                LocalDate.parse(getIntent().getStringExtra("selectedDate")) :
                LocalDate.now(); // Fallback to current date

        eventDateDisplay.setText(getString(R.string.event_date, CalendarUtils.formattedDate(selectedDate)));
        eventTimeDisplay.setText(getString(R.string.event_time, CalendarUtils.formattedTime(eventTime)));

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

        adapter = new ExerciseAdapter(exercises, true); // true to enable selection mode
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ExerciseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExerciseModel exerciseModel) {
                // Navigate to DateTimePickerActivity and pass the entire ExerciseModel
                Intent intent = new Intent(EventActivity.this, DateTimePickerActivity.class);
                intent.putExtra("ExerciseModel", exerciseModel); // Passing the entire object
                startActivity(intent);
            }
        });
        loadSavedExercises();


        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);
        searchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> filteredExercises = filterExercises(s.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EventActivity.this, android.R.layout.simple_dropdown_item_1line, filteredExercises);
                searchAutoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                Toast.makeText(EventActivity.this, "Failed to load exercises.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<String> filterExercises(String query) {
        Log.d("ExerciseFilter", "Exercise filter: " + exerciseNames.toString());

        List<String> filteredList = new ArrayList<>();
        for (ExerciseModel exercise : exercises) {
            if (exercise.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(exercise.getName());
            }
        }
        return filteredList;
    }

}
