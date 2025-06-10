package com.example.schoolhub.data.remote;


import static com.example.schoolhub.utils.AppUtils.showSnackbar;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FireBaseLessonRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String uid = user != null ? user.getUid() : null;

    public void createLesson(Lesson lesson, View view) {
        if (uid == null) return;
        db.collection("users")
                .document(uid)
                .collection("lessons")
                .add(lesson)
                .addOnSuccessListener(documentReference -> {
                    lesson.setId(documentReference.getId());
                    db.collection("users")
                            .document(uid)
                            .collection("lessons")
                            .document(lesson.getId())
                            .set(lesson);
                    addTeacherIfNotExists(lesson);
                    showSnackbar(view, "שיעור נוצר בהצלחה!");
                })
                .addOnFailureListener(e -> showSnackbar(view, "שגיאה ביצירת שיעור: " + e.getMessage()));
    }

    private void addTeacherIfNotExists(Lesson lesson) {
        if (uid == null) return;
        db.collection("users")
                .document(uid)
                .collection("teachers")
                .whereEqualTo("name", lesson.getTeacher())
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) {
                        db.collection("users")
                                .document(uid)
                                .collection("teachers")
                                .add(lesson.getTeacher());
                    }
                });
    }

    public void deleteLesson(Lesson lesson, View view) {
        if (uid == null) return;
        db.collection("users")
                .document(uid)
                .collection("lessons")
                .document(lesson.getId())
                .delete().addOnSuccessListener(task -> {
                    showSnackbar(view, "שיעור נמחק בהצלחה!", "בטל", v -> createLesson(lesson, view));
                });
    }



    /**
     * Checks if the given lesson overlaps with any existing lesson for the user (same day, time overlap).
     *
     * @param lesson   The lesson to check for overlap.
     * @param callback Callback with true if overlapping, false otherwise.
     */
    public void isLessonOverlapping(Lesson lesson, OverlapCallback callback) {
        if (uid == null) {
            callback.onResult(false);
            return;
        }
        db.collection("users")
                .document(uid)
                .collection("lessons")
                .whereEqualTo("day", lesson.getDay())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean overlap = false;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Lesson existing = doc.toObject(Lesson.class);
                        if (existing == null || (existing.getId() != null && existing.getId().equals(lesson.getId())))
                            continue;
                        if (isTimeOverlap(existing.getStartTime(), existing.getEndTime(), lesson.getStartTime(), lesson.getEndTime())) {
                            overlap = true;
                            break;
                        }
                    }
                    callback.onResult(overlap);
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }


    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        int s1 = parseTime(start1);
        int e1 = parseTime(end1);
        int s2 = parseTime(start2);
        int e2 = parseTime(end2);
        return s1 < e2 && s2 < e1;
    }

    /**
     * Parses time string "HH:mm" to minutes since midnight.
     */
    private int parseTime(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Fetch all lessons for the current user.
     *
     * @param callback Callback with the list of lessons (empty if none or error).
     */
    public void fetchLessonsForCurrentUser(LessonsCallback callback) {
        if (uid == null) {
            android.util.Log.e("FireBaseLessonRepository", "UID is null, cannot fetch lessons");
            callback.onLessonsResult(new ArrayList<>());
            return;
        }
        db.collection("users")
                .document(uid)
                .collection("lessons")
                .get()
                .addOnCompleteListener(task -> {
                    List<Lesson> lessonList = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Lesson lesson = document.toObject(Lesson.class);
                            if (lesson != null) {
                                lessonList.add(lesson);
                            } else {
                                android.util.Log.w("FireBaseLessonRepository", "Failed to map document to Lesson: " + document.getId());
                            }
                        }
                        android.util.Log.d("FireBaseLessonRepository", "Fetched " + lessonList.size() + " lessons for user");
                    } else {
                        android.util.Log.e("FireBaseLessonRepository", "Failed to fetch lessons: " + (task.getException() != null ? task.getException().getMessage() : "unknown error"));
                    }
                    callback.onLessonsResult(lessonList);
                });
    }

    public interface LessonsCallback {
        void onLessonsResult(List<Lesson> lessons);
    }

    /**
     * Callback interface for overlap check.
     */
    public interface OverlapCallback {
        void onResult(boolean isOverlapping);
    }

    public interface SnackbarActionCallBack {
        void action();
    }
}
