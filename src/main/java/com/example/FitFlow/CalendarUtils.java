package com.example.FitFlow.Other;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static String formattedDate(LocalDate date) {
        if (date == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }




    public static String formattedTime(LocalTime time) {
        if (time == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }



    public static String monthYearFromDate(LocalDate date) {
        if (date == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String monthDayFromDate(LocalDate date) {
        if (date == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate date) {
        if (date == null) return new ArrayList<>();
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        for (int i = 0; i < 7; i++) {
            days.add(start.plusDays(i));
        }
        return days;
    }



    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        if (date == null) return new ArrayList<>();
        ArrayList<LocalDate> days = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate start = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = yearMonth.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        while (!start.isAfter(end)) {
            days.add(start);
            start = start.plusDays(1);
        }
        return days;
    }
}