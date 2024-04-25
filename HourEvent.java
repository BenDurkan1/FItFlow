package com.example.FitFlow.Other;

import com.example.FitFlow.Event;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class HourEvent implements Serializable {
    private LocalTime time;
    private LocalDate date;    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    private ArrayList<Event> events;

    public HourEvent(LocalTime time, LocalDate date, ArrayList<Event> events) {
        this.time = time;
        this.date = date;
        this.events = events;
    }


    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
