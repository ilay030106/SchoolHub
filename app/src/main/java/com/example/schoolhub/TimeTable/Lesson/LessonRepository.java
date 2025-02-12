package com.example.schoolhub.TimeTable.Lesson;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LessonRepository {
    private final CollectionReference lessonsRef;

    public LessonRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        lessonsRef = db.collection("Lessons");
    }

    // הוספת שיעור חדש
    public void addLesson(Lesson lesson, OnLessonAddedListener listener) {
        lessonsRef.add(lesson)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getLessonsByDay(String day, OnFetchLessonsListener listener) {
        lessonsRef.whereEqualTo("day", day)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Lesson> lessons = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Lesson lesson = doc.toObject(Lesson.class);
                        lesson.setId(doc.getId()); // Set the document ID
                        lessons.add(lesson);
                    }
                    listener.onFetch(lessons);
                })
                .addOnFailureListener(listener::onFailure);
    }



    // בדיקת חפיפה
    public void checkOverlap(Lesson newLesson, OnOverlapCheckListener listener) {
        lessonsRef.whereEqualTo("day", newLesson.getDay())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Lesson existingLesson = doc.toObject(Lesson.class);
                        if (isOverlapping(existingLesson, newLesson)) {
                            listener.onOverlap(true);
                            return;
                        }
                    }
                    listener.onOverlap(false);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // בדיקת חפיפה בין שעות
    private boolean isOverlapping(Lesson existing, Lesson newLesson) {
        return (existing.getStartTime().equals(newLesson.getStartTime()) ||
                existing.getEndTime().equals(newLesson.getEndTime()) ||
                (existing.getStartTime().compareTo(newLesson.getStartTime()) < 0 &&
                        existing.getEndTime().compareTo(newLesson.getStartTime()) > 0));
    }

    // שליפת שמות שיעורים/מורים/כיתות
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void fetchSuggestions(String field, OnFetchSuggestionsListener listener) {
        lessonsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> suggestions = queryDocumentSnapshots.toObjects(Lesson.class).stream()
                    .map(lesson -> {
                        switch (field) {
                            case "name":
                                return lesson.getName() != null ? lesson.getName() : "";
                            case "roomNum":
                                return lesson.getRoomNum() != null ? lesson.getRoomNum() : "";
                            case "teacher":
                                return lesson.getTeacher() != null && lesson.getTeacher().getName() != null
                                        ? lesson.getTeacher().getName()
                                        : "";
                            default:
                                return "";
                        }
                    })

                    .distinct()
                    .toList();
            listener.onFetch(suggestions);
        }).addOnFailureListener(listener::onFailure);
    }

    public void deleteLesson(String lessonId, OnLessonDeletedListener listener) {
        lessonsRef.document(lessonId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Add this interface for the callback
    public interface OnLessonDeletedListener {
        void onSuccess();

        void onFailure(Exception e);
    }


    // ממשקים
    public interface OnLessonAddedListener {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface OnOverlapCheckListener {
        void onOverlap(boolean hasOverlap);

        void onFailure(Exception e);
    }

    public interface OnFetchSuggestionsListener {
        void onFetch(List<String> suggestions);

        void onFailure(Exception e);
    }

    public interface OnFetchLessonsListener {
        void onFetch(List<Lesson> lessons);
        void onFailure(Exception e);
    }
}
