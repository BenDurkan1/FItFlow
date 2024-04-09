package com.example.FitFlow.repository.UI.ViewModels;

import java.util.List;

public class SavedExercisesGroup {
    private List<String> exerciseIds; // Assuming each exercise has a unique ID
    private String date; // Scheduled date for the group
    private String time; // Scheduled time for the group

    // Constructor
    public SavedExercisesGroup() {
    }

    // Getters and Setters
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

    // Add additional methods as needed
}
