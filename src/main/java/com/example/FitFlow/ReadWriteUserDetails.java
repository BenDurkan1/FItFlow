package com.example.FitFlow;

public class ReadWriteUserDetails {
    public String fullName;
    public String doB;
    public String gender;
    public String phone;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String namet,String dobt,String textGender, String phonet ){

        this.fullName = namet;
        this.doB = dobt;
        this.gender = textGender;
        this.phone = phonet;
    }
}
