package com.runningtracker.data.model.history;

public class DateHistoryObject {

    private String dateTime;
    private String dateTimeHide;

    public DateHistoryObject() {
    }

    public DateHistoryObject(String dateTime, String dateTimeHide) {
        this.dateTime = dateTime;
        this.dateTimeHide = dateTimeHide;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTimeHide() {
        return dateTimeHide;
    }

    public void setDateTimeHide(String dateTimeHide) {
        this.dateTimeHide = dateTimeHide;
    }
}
