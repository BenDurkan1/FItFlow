package com.example.FitFlow;

public class TipModel {
    private String title;
    private String detail;

    // Default constructor is required for Firebase
    public TipModel() {
    }

    public TipModel(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
