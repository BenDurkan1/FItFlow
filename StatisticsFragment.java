package com.example.FitFlow.repository.UI.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.FitFlow.Other.CustomMarkerView;
import com.example.FitFlow.Other.TrackingUtil;
import com.example.FitFlow.R;
import com.example.FitFlow.Run;
import com.example.FitFlow.repository.UI.ViewModels.StatisticsViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticsFragment extends Fragment {

    private StatisticsViewModel viewModel;
    private MaterialTextView tvTotalDistanceInfo;
    private MaterialTextView tvTotalDistance;
    private MaterialTextView tvTotalTimeInfo;
    private MaterialTextView tvTotalTime;
    private MaterialTextView tvTotalCaloriesInfo;
    private MaterialTextView tvTotalCalories;
    private MaterialTextView tvAverageSpeedInfo;
    private MaterialTextView tvAverageSpeed;

    private View guideline;
    private View guideline2;

    private BarChart barChart;


  /* public StatisticsFragment() {
        super(R.layout.fragment_statistics);
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        tvTotalDistanceInfo = view.findViewById(R.id.tvTotalDistanceInfo);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvTotalTimeInfo = view.findViewById(R.id.tvTotalTimeInfo);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        tvTotalCaloriesInfo = view.findViewById(R.id.tvTotalCaloriesInfo);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvAverageSpeedInfo = view.findViewById(R.id.tvAverageSpeedInfo);
        tvAverageSpeed = view.findViewById(R.id.tvAverageSpeed);

        guideline = view.findViewById(R.id.guideline);
        guideline2 = view.findViewById(R.id.guideline2);

        barChart = view.findViewById(R.id.barChart);




        subscribeToObservers();
        setupBarChart();

    }

    private void setupBarChart() {
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawLabels(false);
        barChart.getXAxis().setAxisLineColor(Color.WHITE);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.getAxisRight().setAxisLineColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getDescription().setText("Avg Speed Over Time");
        barChart.getLegend().setEnabled(false);
    }

    private void subscribeToObservers() {
        viewModel.getTotalTimeRun().observe(getViewLifecycleOwner(), totalTimeRun -> {
            if (totalTimeRun != null) {
                String totalTimeRunString = TrackingUtil.getFormattedStopWatchTime(totalTimeRun, false);
                tvTotalTime.setText(totalTimeRunString);
            }
        });

        viewModel.getTotalDistance().observe(getViewLifecycleOwner(), totalDistance -> {
            if (totalDistance != null) {
                float km = totalDistance / 1000f;
                float roundedDistance = Math.round(km * 10f) / 10f;
                String totalDistanceString = roundedDistance + " km";
                tvTotalDistance.setText(totalDistanceString);
            }
        });

        viewModel.getTotalAvgSpeed().observe(getViewLifecycleOwner(), avgSpeed -> {
            if (avgSpeed != null) {
                float roundedAvgSpeed = Math.round(avgSpeed * 10f) / 10f;
                String avgSpeedString = roundedAvgSpeed + " km/h";
                tvAverageSpeed.setText(avgSpeedString);
            }
        });

        viewModel.getTotalCaloriesBurned().observe(getViewLifecycleOwner(), totalCalories -> {
            if (totalCalories != null) {
                String totalCaloriesString = totalCalories + " kcal";
                tvTotalCalories.setText(totalCaloriesString);
            }
        });
        viewModel.getRunsSortedByDate().observe(getViewLifecycleOwner(), new Observer<List<Run>>() {
            @Override
            public void onChanged(List<Run> it) {
                if (it != null) {
                    // contain x value and y value of bars
                    List<BarEntry> allAvgSpeeds = new ArrayList<>();
                    for (int i = 0; i < it.size(); i++) {
                        allAvgSpeeds.add(new BarEntry(i, it.get(i).getAvgSpeedInKMH()));
                    }

                    BarDataSet barDataSet = new BarDataSet(allAvgSpeeds, "Avg Speed Over Time");
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.lavender));

                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);

                    List<Run> reversedList = new ArrayList<>(it);
                    Collections.reverse(reversedList);

                    barChart.setMarker(new CustomMarkerView(reversedList, requireContext()));
                    barChart.invalidate();
                }
            }
        });

    }
}