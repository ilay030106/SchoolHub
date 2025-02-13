package com.example.schoolhub.data.local.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.schoolhub.data.local.Database.TimeTable.LessonDao;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;

@Database(entities = {Lesson.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LessonDao lessonDao();
}