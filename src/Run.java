package com.example.FitFlow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class Run {
    private String imgBase64; // Use a base64-encoded string representation of the Bitmap
    private long timestamp;
    private float avgSpeedInKMH;
    private int distanceInMeters;
    private long timeInMillis;
    private int caloriesBurned;
    private String id;

    public Run() {
        // Default constructor required for calls to DataSnapshot.getValue(Run.class)
    }

    public Run(Bitmap img, long timestamp, float avgSpeedInKMH, int distanceInMeters, long timeInMillis, int caloriesBurned) {
        this.imgBase64 = convertBitmapToBase64(img);
        this.timestamp = timestamp;
        this.avgSpeedInKMH = avgSpeedInKMH;
        this.distanceInMeters = distanceInMeters;
        this.timeInMillis = timeInMillis;
        this.caloriesBurned = caloriesBurned;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getAvgSpeedInKMH() {
        return avgSpeedInKMH;
    }

    public int getDistanceInMeters() {
        return distanceInMeters;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Helper method to convert Bitmap to base64
    private String convertBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Input Bitmap cannot be null");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Helper method to convert base64 to Bitmap
    private Bitmap convertBase64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("Input Base64 string cannot be null or empty");
        }
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Run otherRun = (Run) obj;

        // Compare fields for equality
        return this.id.equals(otherRun.id)
                && this.timestamp == otherRun.timestamp
                && Float.compare(this.avgSpeedInKMH, otherRun.avgSpeedInKMH) == 0
                && this.distanceInMeters == otherRun.distanceInMeters
                && this.timeInMillis == otherRun.timeInMillis
                && this.caloriesBurned == otherRun.caloriesBurned
                && Objects.equals(this.imgBase64, otherRun.imgBase64);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, avgSpeedInKMH, distanceInMeters, timeInMillis, caloriesBurned, imgBase64);
    }
}
