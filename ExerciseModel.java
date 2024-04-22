package com.example.FitFlow;

import java.io.Serializable;
import java.util.UUID;

public class ExerciseModel implements Serializable {
    private String id; // Added ID field
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

    public ExerciseModel(String name, String time, String instructions, String benefits, String lottieFile, String day, String scheduledTime) {
        this.name = name;
        this.time = time;
        this.instructions = instructions;
        this.benefits = benefits;
        this.lottieFile = lottieFile;
        this.day = day;
        this.scheduledTime = scheduledTime;
        // Generate or set the ID as needed, e.g., using UUID.randomUUID().toString()
        this.id = UUID.randomUUID().toString(); // Example of generating a unique ID
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getLottieFile() {
        return lottieFile;
    }

    public void setLottieFile(String lottieFile) {
        this.lottieFile = lottieFile;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
