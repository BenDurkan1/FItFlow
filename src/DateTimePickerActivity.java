package com.example.FitFlow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FitFlow.repository.UI.ViewModels.SavedExercisesGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateTimePickerActivity extends AppCompatActivity {
    private Button dateButton, timeButton, saveButton;
    private int year, month, day, hour, minute;
    // Use a list to store exercise IDs or names
    private List<String> selectedExerciseIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime_picker);

        // Retrieve the list of selected exercise IDs or names
        Intent intent = getIntent();
        selectedExerciseIds = intent.getStringArrayListExtra("selectedExerciseIds");

        dateButton = findViewById(R.id.datePickerButton);
        timeButton = findViewById(R.id.timeButton);
        saveButton = findViewById(R.id.saveButton);

        initializeDatePicker();
        initializeTimePicker();
        setupSaveButton();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveExerciseGroup());
    }

    private void saveExerciseGroup() {
        if (selectedExerciseIds.isEmpty()) {
            Toast.makeText(this, "No exercises selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedDate = makeDateString(day, month + 1, year);
        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Registered Users")
                    .child(user.getUid())
                    .child("SavedExerciseGroups")
                    .push(); // Use push() for a unique ID

            // Create an instance of SavedExercisesGroup with the selected exercises, date, and time
            SavedExercisesGroup group = new SavedExercisesGroup();
            group.setExerciseIds(selectedExerciseIds);
            group.setDate(selectedDate);
            group.setTime(selectedTime);

            databaseReference.setValue(group).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DateTimePickerActivity.this, "Group saved successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DateTimePickerActivity.this, "Failed to save group.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "You need to be logged in to save exercise groups.", Toast.LENGTH_SHORT).show();
        }
    }

    // The rest of your methods (initializeDatePicker, initializeTimePicker, etc.) remain the same.


    private void initializeDatePicker() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        updateDateButtonText();

        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    this::onDateSet,
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        updateDateButtonText();
    }

    private void updateDateButtonText() {
        String date = makeDateString(day, month + 1, year);
        dateButton.setText(date);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        } else {
            return "JAN"; // Default to JAN if month is out of bounds
        }
    }

    private void initializeTimePicker() {
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minute = Calendar.getInstance().get(Calendar.MINUTE);
        updateTimeButtonText();

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    this::onTimeSet,
                    hour, minute, true);
            timePickerDialog.show();
        });
    }

    private void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        updateTimeButtonText();
    }

    private void updateTimeButtonText() {
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        timeButton.setText(time);
    }

    public void saveEvent(View view) {
        // Your save event logic
    }
}

