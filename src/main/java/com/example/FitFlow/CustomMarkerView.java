package com.example.FitFlow.Other;

import android.content.Context;
import android.widget.TextView;

import com.example.FitFlow.R;
import com.example.FitFlow.Run;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomMarkerView extends MarkerView {
    private TextView tvDate;
    private TextView tvDuration;
    private TextView tvAvgSpeed;
    private TextView tvDistance;
    private TextView tvCaloriesBurned;

    private final List<Run> runs;

    public CustomMarkerView(List<Run> runs, Context c) {
        super(c, R.layout.marker_view);
        this.runs = runs;

        tvDate = findViewById(R.id.tvDate);
        tvDuration = findViewById(R.id.tvDuration);
        tvAvgSpeed = findViewById(R.id.tvAvgSpeed);
        tvDistance = findViewById(R.id.tvDistance);
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned);

        setOffset(new MPPointF(-getWidth() / 2f, -getHeight()));
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2f, -getHeight());
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);

        if (e == null) {
            return;
        }

        int curRunId = (int) e.getX();
        Run run = runs.get(curRunId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(run.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        tvDate.setText(formattedDate);

        String avgSpeed = run.getAvgSpeedInKMH() + "km/h";
        tvAvgSpeed.setText(avgSpeed);

        String distanceInKm = run.getDistanceInMeters() / 1000f + "km";
        tvDistance.setText(distanceInKm);

        String formattedDuration = TrackingUtil.getFormattedStopWatchTime(run.getTimeInMillis(),true);
        tvDuration.setText(formattedDuration);

        String caloriesBurned = run.getCaloriesBurned() + "kcal";
        tvCaloriesBurned.setText(caloriesBurned);
    }
}
