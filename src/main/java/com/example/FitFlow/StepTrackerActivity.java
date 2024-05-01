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
        stepCountDAO = new StepCountDAO();

        loadStepData();
    }

    private void loadStepData() {
        stepCountDAO.getStepCounts(new StepCountDAO.DataStatus() {
            @Override
            public void DataIsLoaded(List<StepCount> stepCounts, List<String> keys) {
                List<Entry> entries = new ArrayList<>();
                for (StepCount stepCount : stepCounts) {
                   
                    entries.add(new Entry(entries.size(), stepCount.getStepCount()));
                }
                updateChart(entries);
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
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
