package com.example.schoolhub.data.local.model.TimeTable;

import com.google.firebase.firestore.Exclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    private String id;
    private String name;
    private Teacher teacher;
    private String roomNum;
    private String day;
    private String startTime;
    private String endTime;
    private String color;


}

