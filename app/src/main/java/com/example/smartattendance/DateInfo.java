package com.example.smartattendance;

public class DateInfo {
    private static DateInfo dateInfo = null;
    private String date;

    public DateInfo() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static DateInfo getInstance() {
        if (dateInfo == null) {
            dateInfo = new DateInfo();
        }
        return dateInfo;
    }
}