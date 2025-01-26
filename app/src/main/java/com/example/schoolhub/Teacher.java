package com.example.schoolhub;

public class Teacher {
    private String name;
    private String email;

    // בנאי ריק (נדרש על ידי Firestore)
    public Teacher() {
    }

    // בנאי עם שם בלבד
    public Teacher(String name) {
        this.name = name;
        this.email = null;
    }

    // בנאי מלא
    public Teacher(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters ו-Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
