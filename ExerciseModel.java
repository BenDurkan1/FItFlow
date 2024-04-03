package com.example.FitFlow;

import java.io.Serializable;

public class ExerciseModel implements Serializable {
    private String name;
    private String time;
    private String instructions;
    private String benefits;
    private String lottieFile;
    private String day;
    private String scheduledTime;

    // Constructor
    // No-argument constructor
    public ExerciseModel() {
    }
    public ExerciseModel(String name, String time, String day, String scheduledTime, String instructions, String benefits, String lottieFile) {
        this.name = name;
        this.time = time;
        this.day = day;
        this.scheduledTime = scheduledTime;
        this.instructions = instructions;
        this.benefits = benefits;
        this.lottieFile = lottieFile;
    }

    // Getters (and optionally setters)
    public String getName() { return name; }
    public String getTime() { return time; }
    public String getDay() { return day; }
    public String getScheduledTime() { return scheduledTime; }
    public String getInstructions() { return instructions; }
    public String getBenefits() { return benefits; }
    public String getLottieFile() { return lottieFile; }

    // Setters if necessary
    // Example:
    // public void setName(String name) {
    //     this.name = name;
    // }
}