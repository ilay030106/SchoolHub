package com.example.schoolhub.data.local.Database.TimeTable;

import androidx.room.TypeConverter;

import com.example.schoolhub.data.local.model.TimeTable.Teacher;
import com.google.gson.Gson;

public class TeacherConverter {
    @TypeConverter
    public static String fromTeacher(Teacher teacher) {
        return new Gson().toJson(teacher);
    }

    @TypeConverter
    public static Teacher toTeacher(String teacherString) {
        return new Gson().fromJson(teacherString, Teacher.class);
    }
}