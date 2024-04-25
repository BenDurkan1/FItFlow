package com.example.FitFlow.repository.UI.ViewModels;

import java.util.List;

public class SavedExercisesGroup {
    private List<String> exerciseIds;
    private String date;
    private String time;

    public SavedExercisesGroup() {
    }

    public List<String> getExerciseIds() {
        return exerciseIds;
    }

    public void setExerciseIds(List<String> exerciseIds) {
        this.exerciseIds = exerciseIds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
