package com.example.FitFlow;

import java.io.Serializable;
import java.util.UUID;

public class ExerciseModel implements Serializable {
    private String id;
    private String firebaseKey;

    private String name;
    private long time;
    private String instructions;
    private String benefits;
    private String lottieFile;
    private String scheduledDate;
    private String scheduledTime;



    public ExerciseModel() {
    }

  /*  public void setTime(long time) {
        this.time = time;
    } */

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    public ExerciseModel(String name, long time, String instructions, String benefits, String lottieFile) {
        this(name, time, instructions, benefits, lottieFile, null, null); // Call the main constructor with nulls for date and time
    }


    public ExerciseModel(String name, long time, String instructions, String benefits, String lottieFile, String scheduledDate, String scheduledTime) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.time = time;
        this.instructions = instructions;
        this.benefits = benefits;
        this.lottieFile = lottieFile;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
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

    public long getTime() {
        return time;
    }

 public void setTime(Object time) {
        if (time instanceof Long) {
            this.time = (Long) time;
        } else if (time instanceof String) {
            try {
                this.time = Long.parseLong((String) time);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Time is not a valid number", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid type for time: " + time.getClass().getName());
        }
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
    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }


}
