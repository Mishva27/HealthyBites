package com.example.healthybites.models;

public class NotificationModel {
    private String title;
    private String message;
    private String type;
    private String time;

    public NotificationModel(String title, String message, String type, String time) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    // Setters (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
