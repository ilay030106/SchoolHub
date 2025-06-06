package com.example.schoolhub.ui.ViewModel.TimeTable;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.data.remote.FireBaseLessonRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LessonViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Lesson>> lessons = new MutableLiveData<>();
    private final MutableLiveData<List<String>> classNames = new MutableLiveData<>();
    private final MutableLiveData<List<String>> teacherNames = new MutableLiveData<>();
    private final FirebaseFirestore db;
    private final FireBaseLessonRepository repository;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        repository = new FireBaseLessonRepository();
        fetchLessonsFromRepository();
    }

    public LiveData<List<Lesson>> getLessons() {
        return lessons;
    }

    public LiveData<List<String>> getClassNames() {
        return classNames;
    }

    public LiveData<List<String>> getTeacherNames() {
        return teacherNames;
    }

    private void fetchLessonsFromRepository() {
        repository.fetchLessonsForCurrentUser(lessonList -> {
            List<String> classNameList = new ArrayList<>();
            List<String> teacherNameList = new ArrayList<>();
            for (Lesson lesson : lessonList) {
                if (lesson.getName() != null && !classNameList.contains(lesson.getName())) {
                    classNameList.add(lesson.getName());
                }
                if (lesson.getTeacher() != null && lesson.getTeacher().getName() != null && !teacherNameList.contains(lesson.getTeacher().getName())) {
                    teacherNameList.add(lesson.getTeacher().getName());
                }
            }
            android.util.Log.d("LessonViewModel", "Repository returned " + lessonList.size() + " lessons");
            lessons.postValue(lessonList);
            classNames.postValue(classNameList);
            teacherNames.postValue(teacherNameList);
        });
    }
}

