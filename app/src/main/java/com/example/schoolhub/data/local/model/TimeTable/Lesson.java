package com.example.schoolhub.data.local.model.TimeTable;

import com.google.firebase.firestore.Exclude;

public class Lesson {

    private long id;
    private String name;
    private Teacher teacher;
    private String roomNum;
    private String day;
    private String startTime;
    private String endTime;
    private String color;

    // Empty constructor required for Firestore
    public Lesson() {
    }

    // Full constructor
    public Lesson(String name, Teacher teacher, String roomNum, String day, String startTime, String endTime, String color) {
        this.name = name;
        this.teacher = teacher;
        this.roomNum = roomNum;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
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