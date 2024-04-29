package com.example.FitFlow;

import static com.example.FitFlow.Other.CalendarUtils.daysInMonthArray;
import static com.example.FitFlow.Other.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.adapters.DateGridAdapter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class CalendarScreen extends AppCompatActivity implements DateGridAdapter.DateSelectionListener {
    private TextView displayMonthYear;
    private RecyclerView dateGridView;
    private LocalDate currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        initializeUI();

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

        DateGridAdapter monthAdapter = new DateGridAdapter(monthDays, this);
        RecyclerView.LayoutManager gridLayout = new GridLayoutManager(getApplicationContext(), 7);
        dateGridView.setLayoutManager(gridLayout);
        dateGridView.setAdapter(monthAdapter);
    }

    public void goToPreviousMonth(View view) {
        currentDate = currentDate.minusMonths(1);
        updateMonthView();
    }

    public void goToNextMonth(View view) {
        currentDate = currentDate.plusMonths(1);
        updateMonthView();
    }

    @Override
    public void onDateSelected(int position, LocalDate date) {
        currentDate = date;
        updateMonthView();
    }

    public void navigateToWeeklyView(View view) {
        Intent intent = new Intent(this, WeekViewActivity.class);
        intent.putExtra("SELECTED_DATE", currentDate.toString());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDate", currentDate.toString());
    }
}
