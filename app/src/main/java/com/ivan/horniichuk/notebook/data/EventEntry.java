package com.ivan.horniichuk.notebook.data;

public class EventEntry {
    private long id;
    private String title;
    private int status;
    private long dueDate;
    private int reminderDays;

    public EventEntry() {
    }

    public EventEntry(String title, int status, long dueDate, int reminderDays) {
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
        this.reminderDays = reminderDays;
    }

    public EventEntry(long id, String title, int status, long dueDate, int reminderDays) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
        this.reminderDays = reminderDays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getReminderDays() {
        return reminderDays;
    }

    public void setReminderDays(int reminderDays) {
        this.reminderDays = reminderDays;
    }
}
