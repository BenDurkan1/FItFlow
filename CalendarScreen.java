package com.example.FitFlow;

import static com.example.FitFlow.Other.CalendarUtils.daysInMonthArray;
import static com.example.FitFlow.Other.CalendarUtils.monthYearFromDate;
import static com.example.FitFlow.Other.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.adapters.DateGridAdapter;
import com.example.FitFlow.repository.UI.ViewModels.SavedExercisesGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarScreen extends AppCompatActivity implements DateGridAdapter.DateSelectionListener {
    private TextView displayMonthYear;
    private RecyclerView dateGridView;
    private LocalDate currentDate;
    public static final String SELECTED_DATE_KEY = "selectedDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        initializeUI();
        fetchSavedDates();
        // Check if there's a saved state and use it to set currentDate
        if (savedInstanceState != null && savedInstanceState.containsKey("currentDate")) {
            currentDate = LocalDate.parse(savedInstanceState.getString("currentDate"));
        } else {
            currentDate = LocalDate.now(ZoneId.systemDefault()); // Enhanced for time zone awareness
        }

        updateMonthView();
    }

    private void initializeUI() {
        dateGridView = findViewById(R.id.calendarRecyclerView);
        displayMonthYear = findViewById(R.id.monthYearTV);
    }

    private void updateMonthView() {
        displayMonthYear.setText(monthYearFromDate(currentDate));
        ArrayList<LocalDate> monthDays = daysInMonthArray(currentDate);

        // Pass the savedDates list to the adapter
        DateGridAdapter monthAdapter = new DateGridAdapter(monthDays, this, selectedDate, savedDates);
        RecyclerView.LayoutManager gridLayout = new GridLayoutManager(getApplicationContext(), 7);
        dateGridView.setLayoutManager(gridLayout);
        dateGridView.setAdapter(monthAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSavedDates(); // This method will re-fetch and update the UI accordingly.
    }
    public void startDateTimePickerActivity(View view) {
        Intent intent = new Intent(this, DateTimePickerActivity.class);
        // Pass any required data to DateTimePickerActivity, if needed
        startActivityForResult(intent, 1); // Use a request code to identify the result
    }

    public void goToPreviousMonth(View view) {
        currentDate = currentDate.minusMonths(1);
        fetchSavedDates(); // Ensure this calls updateMonthView() after data is fetched
    }

    public void goToNextMonth(View view) {
        currentDate = currentDate.plusMonths(1);
        fetchSavedDates(); // Ensure this calls updateMonthView() after data is fetched
    }

    @Override
    public void onDateSelected(int position, LocalDate date) {
        Intent intent = new Intent(CalendarScreen.this, DailyViewActivity.class);
        intent.putExtra(SELECTED_DATE_KEY, date.toString());
        startActivity(intent);
    }
    public void navigateToWeeklyView(View view) {
        Intent intent = new Intent(this, WeekViewActivity.class);
        // Pass the selected date to the WeekViewActivity
        intent.putExtra("SELECTED_DATE", currentDate.toString());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDate", currentDate.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String selectedDateString = data.getStringExtra("selectedDate");
            LocalDate selectedDate = LocalDate.parse(selectedDateString); // Parse the string back to a LocalDate
            this.currentDate = selectedDate; // Update your currentDate with the selected one
            updateMonthView(); // Refresh your calendar view to reflect the change
        }
    }
    private List<LocalDate> savedDates = new ArrayList<>();

    private void fetchSavedDates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Registered Users")
                    .child(user.getUid())
                    .child("SavedExerciseGroups");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    savedDates.clear();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH); // Specify the correct pattern and locale
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SavedExercisesGroup group = snapshot.getValue(SavedExercisesGroup.class);
                        if (group != null) {
                            try {
                                LocalDate date = LocalDate.parse(group.getDate(), formatter);
                                if (!savedDates.contains(date)) {
                                    savedDates.add(date);
                                }
                            } catch (DateTimeParseException e) {
                                e.printStackTrace(); // Log the parsing error
                            }
                        }
                    }
                    updateMonthView(); // Call updateMonthView here after fetching and parsing dates
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

}
