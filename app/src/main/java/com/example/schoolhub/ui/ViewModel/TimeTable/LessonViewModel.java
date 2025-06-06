package com.example.schoolhub.ui.ViewModel.TimeTable;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LessonViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Lesson>> lessons = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        fetchLessonsFromFirebase();
    }

    public LiveData<List<Lesson>> getLessons() {
        return lessons;
    }

    private void fetchLessonsFromFirebase() {
        db.collection("lessons") // Replace with your Firestore collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Lesson> lessonList = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Lesson lesson = document.toObject(Lesson.class);
                            lessonList.add(lesson);
                        }
                        lessons.setValue(lessonList);
                    } else {
                        lessons.setValue(new ArrayList<>()); // Set empty list on failure
                    }
                });
    }
}