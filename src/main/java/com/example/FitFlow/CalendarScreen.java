package com.example.FitFlow;

import static com.example.FitFlow.Other.CalendarUtils.daysInMonthArray;
import static com.example.FitFlow.Other.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.adapters.DateGridAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CalendarScreen extends AppCompatActivity implements DateGridAdapter.DateSelectionListener {
    private TextView displayMonthYear;
    private RecyclerView dateGridView;
    private LocalDate currentDate;
    private List<LocalDate> activityDays = new ArrayList<>();
    public static final String SELECTED_DATE_KEY = "selectedDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        initializeUI();
        if (savedInstanceState != null && savedInstanceState.containsKey("currentDate")) {
            currentDate = LocalDate.parse(savedInstanceState.getString("currentDate"));
        } else {
            currentDate = LocalDate.now(ZoneId.systemDefault());
        }
        fetchSavedDates();
    }

    private void initializeUI() {
        dateGridView = findViewById(R.id.calendarRecyclerView);
        displayMonthYear = findViewById(R.id.monthYearTV);
    }



    @Override
    protected void onResume() {
        super.onResume();
        fetchSavedDates();
    }



    public void goToPreviousMonth(View view) {
        currentDate = currentDate.minusMonths(1);
        fetchSavedDates();
    }

    public void goToNextMonth(View view) {
        currentDate = currentDate.plusMonths(1);
        fetchSavedDates();
    }

    @Override
    public void onDateSelected(int position, LocalDate date) {
        Intent intent = new Intent(CalendarScreen.this, DailyViewActivity.class);
        intent.putExtra(SELECTED_DATE_KEY, date.toString());
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
            LocalDate selectedDate = LocalDate.parse(selectedDateString);
            this.currentDate = selectedDate;
            updateMonthView();
        }
    }
   // private List<LocalDate> savedDates = new ArrayList<>();

    private void fetchSavedDates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid())
                    .child("SavedCalExercises");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    activityDays.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LocalDate date = LocalDate.parse(snapshot.getKey());
                        activityDays.add(date);
                    }
                    updateMonthView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Firebase", "Failed to read saved exercise dates.");
                }
            });
        }
    }

    private void updateMonthView() {
        displayMonthYear.setText(monthYearFromDate(currentDate));
        ArrayList<LocalDate> monthDays = daysInMonthArray(currentDate);
        DateGridAdapter monthAdapter = new DateGridAdapter(monthDays, activityDays, this);
        dateGridView.setLayoutManager(new GridLayoutManager(this, 7));
        dateGridView.setAdapter(monthAdapter);
    }

}

