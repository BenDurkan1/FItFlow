package com.example.FitFlow;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.FitFlow.Other.StepsMarkerView;
import com.example.FitFlow.repository.UI.ViewModels.StepCountViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StepCountActivity extends AppCompatActivity {
    private TextView totalStepsView, averageStepsView;
    private StepCountViewModel viewModel;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        viewModel = new ViewModelProvider(this).get(StepCountViewModel.class);
        totalStepsView = findViewById(R.id.tvTotalSteps);
        averageStepsView = findViewById(R.id.tvAverageSteps);
        barChart = findViewById(R.id.barChart);
        setupBarChart();
        subscribeToObservers();
        //barChart.setMarker(new StepsMarkerView(this, R.layout.stepmarker_view));
    }

    private void setupBarChart() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setAxisLineColor(Color.BLUE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextSize(12f);
        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.getAxisRight().setAxisLineColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextSize(12f);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getDescription().setText("Step Counts Over Time");
        barChart.getDescription().setTextSize(14f);
        barChart.getDescription().setTextColor(Color.GRAY);
        barChart.getLegend().setEnabled(true);
        barChart.getLegend().setTextColor(Color.WHITE);
    }


    private void subscribeToObservers() {
        LiveData<Integer> totalStepsLiveData = viewModel.getTotalSteps();
        LiveData<Double> averageStepsLiveData = viewModel.getAverageSteps();

        totalStepsLiveData.observe(this, totalSteps -> {
            if (totalSteps != null) {
                totalStepsView.setText(String.format("Total Steps: %s", totalSteps));
            } else {
                totalStepsView.setText("Total Steps: 0");
            }
        });

        averageStepsLiveData.observe(this, averageSteps -> {
            if (averageSteps != null) {
                averageStepsView.setText(String.format("Average Steps: %.2f", averageSteps));
            } else {
                averageStepsView.setText("Average Steps: N/A");
            }
        });

        viewModel.getStepCountsSortedByDate().observe(this, stepCounts -> {
            if (stepCounts != null && !stepCounts.isEmpty()) {
                updateChartWithDateSortedSteps(stepCounts);
            } else {
             //   Log.d("StepCountActivity", "Step counts data is empty or null.");
            }
        });
    }


 /*   private void updateAverageSteps(Integer totalSteps, Integer daysCount) {
        Log.d("StepCountActivity", "Total Steps: " + totalSteps + ", Days Count: " + daysCount);
        if (totalSteps != null && daysCount != null && daysCount > 0) {
            double averageSteps = (double) totalSteps / daysCount;
            averageStepsView.setText(String.format("Average Steps: %.2f", averageSteps));
        } else {
            averageStepsView.setText("Average Steps: N/A");
        }
    } */

    private void updateChartWithDateSortedSteps(List<StepCount> stepCounts) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (StepCount step : stepCounts) {
            entries.add(new BarEntry(index, step.getStepCount()));
            String date = step.getDate();
            if (date != null && !date.isEmpty()) {
                labels.add(date);
                index++;
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Steps");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        if (!labels.isEmpty()) {
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            barChart.setData(barData);

            StepsMarkerView mv = new StepsMarkerView(this, R.layout.stepmarker_view, labels);
            barChart.setMarker(mv);

            barChart.notifyDataSetChanged();
            barChart.invalidate();
        } else {
    //        Log.e("StepCountActivity", "No valid date labels found.");
        }
    }


}