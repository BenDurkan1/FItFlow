package com.example.FitFlow;

public class ReadWriteUserDetails {
    public String fullName;
    public String doB;
    public String gender;
    public String phone;
    public String weight;
    public String imageUrl;

    public ReadWriteUserDetails() {}

    public ReadWriteUserDetails(String fullName, String doB, String gender, String phone, String weight, String imageUrl) {
        this.fullName = fullName;
        this.doB = doB;
        this.gender = gender;
        this.phone = phone;
        this.weight = weight;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDoB() {
        return doB;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
