package com.example.FitFlow;

public class ExerciseRV {
    private String exerciseName;
    private String exerciseDescription;
    private String imgURL;

    public ExerciseRV(String exerciseName, String exerciseDescription, String imgURL, int calories, int time) {
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
        this.imgURL = imgURL;
        this.calories = calories;
        this.time = time;
    }

    private int calories, time;

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseDescription() {
        return exerciseDescription;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
