package com.example.schoolhub.data.repository.TimeTable;
import java.util.stream.Collectors;
import android.content.Context;

import androidx.room.Room;
import com.example.schoolhub.data.remote.FirestoreHelper;
import com.example.schoolhub.data.local.Database.AppDatabase;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class LessonRepository {
    private final AppDatabase db;
    private final FirebaseFirestore firestore;
    private final FirestoreHelper firestoreHelper;
    private final Context context;


    public LessonRepository(Context context) {
        this.context = context;

        db = Room.databaseBuilder(context, AppDatabase.class, "schoolhub-db").build();
        firestore = FirebaseFirestore.getInstance();
        firestoreHelper = new FirestoreHelper();
    }

    public void addLesson(Lesson lesson, OnLessonAddedListener listener) {
        new Thread(() -> {
            try {
                db.lessonDao().insert(lesson);
                firestoreHelper.saveLesson(context,lesson);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailure(e);
            }
        }).start();
    }

    public void getLessonsByDay(String day, OnFetchLessonsListener listener) {
        new Thread(() -> {
            try {
                List<Lesson> lessons = (List<Lesson>) db.lessonDao().getLessonsByDay(day);
                listener.onFetch(lessons);
            } catch (Exception e) {
                listener.onFailure(e);
            }
        }).start();
    }

    public void deleteLesson(String lessonId, OnLessonDeletedListener listener) {
        new Thread(() -> {
            try {
                db.lessonDao().deleteById(lessonId);
                firestore.collection("lessons").document(lessonId).delete();
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailure(e);
            }
        }).start();
    }

    public void checkOverlap(Lesson lesson, OnOverlapCheckListener listener) {
        new Thread(() -> {
            try {
                boolean hasOverlap = db.lessonDao().checkOverlap(lesson.getDay(), lesson.getStartTime(), lesson.getEndTime());
                listener.onOverlap(hasOverlap);
            } catch (Exception e) {
                listener.onFailure(e);
            }
        }).start();
    }

    public void fetchSuggestions(String field, OnFetchSuggestionsListener listener) {
        firestore.collection("lessons").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> suggestions = task.getResult().toObjects(Lesson.class).stream()
                        .map(lesson -> {
                            switch (field) {
                                case "name":
                                    return lesson.getName();
                                case "teacher":
                                    return lesson.getTeacher().getName();
                                case "roomNum":
                                    return lesson.getRoomNum();
                                default:
                                    return "";
                            }
                        }).distinct().collect(Collectors.toList());
                listener.onFetch(suggestions);
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    public interface OnLessonAddedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnFetchLessonsListener {
        void onFetch(List<Lesson> lessons);
        void onFailure(Exception e);
    }

    public interface OnLessonDeletedListener {
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
}