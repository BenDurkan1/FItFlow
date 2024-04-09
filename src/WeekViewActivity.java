package com.example.FitFlow;

import static com.example.FitFlow.Other.CalendarUtils.daysInWeekArray;
import static com.example.FitFlow.Other.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.Other.CalendarUtils;
import com.example.FitFlow.adapters.DateGridAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements DateGridAdapter.DateSelectionListener {
    private TextView displayMonthYear;
    private RecyclerView dateGridView;
    private ListView eventListView;
    private LocalDate currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);
        initializeWidgets();

        // Handling the intent to check if a specific date was passed
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_DATE")) {
            String dateStr = intent.getStringExtra("SELECTED_DATE");
            currentDate = LocalDate.parse(dateStr);
        } else {
            currentDate = CalendarUtils.selectedDate; // Fallback to selectedDate if no date was passed via intent
        }

        updateWeekView();
    }

    private void initializeWidgets() {
        dateGridView = findViewById(R.id.calendarRecyclerView);
        displayMonthYear = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    private void updateWeekView() {
        displayMonthYear.setText(monthYearFromDate(currentDate));
        ArrayList<LocalDate> daysOfWeek = daysInWeekArray(currentDate);

        DateGridAdapter weekAdapter = new DateGridAdapter(daysOfWeek, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        dateGridView.setLayoutManager(layoutManager);
        dateGridView.setAdapter(weekAdapter);
    }

    public void previousWeekAction(View view) {
        currentDate = currentDate.minusWeeks(1);
        updateWeekView();
    }

    public void nextWeekAction(View view) {
        currentDate = currentDate.plusWeeks(1);
        updateWeekView();
    }

    @Override
    public void onDateSelected(int position, LocalDate date) {
        currentDate = date;
        updateWeekView();
    }




    public void newEventAction(View view) {
        startActivity(new Intent(this, EventActivity.class));
    }

    public void dailyAction(View view) {
        Intent intent = new Intent(this, DailyViewActivity.class);
        // Pass the current date to DailyViewActivity
        intent.putExtra("SELECTED_DATE", currentDate.toString());
        startActivity(intent);
    }
}
