package com.example.FitFlow;

import static com.example.FitFlow.Other.CalendarUtils.selectedDate;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FitFlow.Other.CalendarUtils;
import com.example.FitFlow.Other.HourEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyViewActivity extends AppCompatActivity {

    private TextView dateTitleText;
    private TextView dayOfWeekText;
    private ListView hoursListView;
    private static final String SELECTED_DATE_KEY = "selectedDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_view);
        LocalDate passedDate = getIntent().hasExtra("SELECTED_DATE") ?
                LocalDate.parse(getIntent().getStringExtra("SELECTED_DATE")) : LocalDate.now();
        CalendarUtils.selectedDate = passedDate; // Consider using a local variable instead

        initializeViews();
        updateDayView();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(SELECTED_DATE_KEY)) {
            selectedDate = LocalDate.parse(savedInstanceState.getString(SELECTED_DATE_KEY));
            updateDayView();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateDayView();
    }
    private void initializeViews() {
        dateTitleText = findViewById(R.id.monthDayText);
        dayOfWeekText = findViewById(R.id.dayOfWeekTV);
        hoursListView = findViewById(R.id.hourListView);
    }

    private void updateDayView() {
        dateTitleText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekText.setText(dayOfWeek);
        updateHoursListView();
    }

    private void updateHoursListView() {
        fetchEventsForSelectedDate();

    }

    private void fetchEventsForSelectedDate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("SavedCalExercises");
        Query query = ref.orderByChild("date").equalTo(CalendarUtils.formattedDate(selectedDate));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HourEvent> hourlyEvents = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DailyViewActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void navigateToPreviousDay(View view) {
        selectedDate = selectedDate.minusDays(1);
        updateDayView();
    }

    public void navigateToNextDay(View view) {
        selectedDate = selectedDate.plusDays(1);
        updateDayView();
    }


}