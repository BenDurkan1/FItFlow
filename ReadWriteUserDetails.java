package com.example.FitFlow;

public class ReadWriteUserDetails {
    public String fullName;
    public String doB;
    public String gender;

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

    public String phone;
    public String weight;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String namet,String dobt,String textGender, String phonet, String weight ){

        this.fullName = namet;
        this.doB = dobt;
        this.gender = textGender;
        this.phone = phonet;
        this.weight = weight;
    }
}
