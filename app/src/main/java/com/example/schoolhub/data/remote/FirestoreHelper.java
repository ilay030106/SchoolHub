package com.example.schoolhub.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.schoolhub.utils.DeviceIdManager;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveLesson(Context context, Lesson lesson) {
        String deviceId = DeviceIdManager.getDeviceId(context);
        db.collection("devices").document(deviceId)
            .collection("lessons").document(String.valueOf(lesson.getId()))
            .set(lesson)
            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lesson saved!"))
            .addOnFailureListener(e -> Log.e("Firestore", "Error saving lesson", e));
    }
    public void deleteLesson(Context context, Lesson lesson) {
        String deviceId = DeviceIdManager.getDeviceId(context);
        db.collection("devices").document(deviceId)
            .collection("lessons").document(String.valueOf(lesson.getId()))
            .delete()
            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lesson deleted!"))
            .addOnFailureListener(e -> Log.e("Firestore", "Error deleting lesson", e));
    }
    public void updateLesson(Context context, Lesson lesson) {
        String deviceId = DeviceIdManager.getDeviceId(context);
        db.collection("devices").document(deviceId)
            .collection("lessons").document(String.valueOf(lesson.getId()))
            .set(lesson)
            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Lesson updated!"))
            .addOnFailureListener(e -> Log.e("Firestore", "Error updating lesson", e));
    }


}