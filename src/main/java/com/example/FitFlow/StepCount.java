package com.example.FitFlow;

public class StepCount {
    private String id;
    private String date;
    private int stepCount;

    public StepCount() {
    }

    public StepCount(String id, String date, int stepCount) {
        this.id = id;
        this.date = date;
        this.stepCount = stepCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
