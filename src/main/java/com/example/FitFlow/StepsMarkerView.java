package com.example.FitFlow.Other;

import android.content.Context;
import android.widget.TextView;

import com.example.FitFlow.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class StepsMarkerView extends MarkerView {

    private TextView tvContent;
    private List<String> dates;

    public StepsMarkerView(Context context, int layoutResource, List<String> dates) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        this.dates = dates;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int currentIndex = (int) e.getX();
        String date = dates.get(currentIndex);
        tvContent.setText("Date: " + date + "\nSteps: " + (int) e.getY());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
