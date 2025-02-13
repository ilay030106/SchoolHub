package com.example.schoolhub.data.local.Database.TimeTable;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lesson lesson);

    @Query("SELECT * FROM lessons")
    LiveData<List<Lesson>> getAllLessons();

    @Query("SELECT * FROM lessons WHERE day = :day")
    LiveData<List<Lesson>> getLessonsByDay(String day);
    @Query("DELETE FROM lessons WHERE id = :lessonId")
    void deleteById(String lessonId);
    @Query("SELECT EXISTS(SELECT 1 FROM lessons WHERE day = :day AND ((startTime < :endTime AND endTime > :startTime)))")
    boolean checkOverlap(String day, String startTime, String endTime);
}
