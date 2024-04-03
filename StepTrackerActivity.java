package com.example.FitFlow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StepTrackerActivity extends AppCompatActivity {

    private LineChart lineChart;
    private StepCountDAO  stepCountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_tracker);

        lineChart = findViewById(R.id.lineChart);
        stepCountDAO = new StepCountDAO(); // Initialize your DAO

        loadStepData();
    }

    private void loadStepData() {
        stepCountDAO.getStepCounts(new StepCountDAO.DataStatus() {
            @Override
            public void DataIsLoaded(List<StepCount> stepCounts, List<String> keys) {
                List<Entry> entries = new ArrayList<>();
                for (StepCount stepCount : stepCounts) {
                    // Assuming you have a method in StepCount to convert date to a numeric representation or order
                    // Here, just using the size of entries as an x-value for simplicity
                    entries.add(new Entry(entries.size(), stepCount.getStepCount()));
                }
                updateChart(entries);
            }

            @Override
            public void DataIsInserted() {
                // Not needed for loading data
            }

            @Override
            public void DataIsUpdated() {
                // Not needed for loading data
            }

            @Override
            public void DataIsDeleted() {
                // Not needed for loading data
            }
        });
    }

    private void updateChart(List<Entry> entries) {
        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, "Step Count");
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // refresh
        }
    }
}
