package com.example.schoolhub;

public class Lesson {
    private String name;
    private Teacher teacher;
    private String day;
    private String startTime; // במקום Time
    private String endTime;   // במקום Time
    private String color;

    // בנאי ריק (נדרש על ידי Firestore)
    public Lesson() {
    }

    // בנאי מלא
    public Lesson(String name, Teacher teacher, String day, String startTime, String endTime, String color) {
        this.name = name;
        this.teacher = teacher;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    // Getters ו-Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
