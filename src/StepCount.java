package com.example.FitFlow;

public class StepCount {
    private String date;
    private int stepCount;

    public StepCount() {
        // Default constructor required for calls to DataSnapshot.getValue(StepCount.class)
    }

    public StepCount(String date, int stepCount) {
        this.date = date;
        this.stepCount = stepCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
}
