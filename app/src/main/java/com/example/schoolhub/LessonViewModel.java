package com.example.schoolhub;

import android.os.Build;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class LessonViewModel extends ViewModel {
    private final LessonRepository repository;

    public LessonViewModel() {
        repository = new LessonRepository();
    }

    public void addLesson(Lesson lesson, LessonRepository.OnLessonAddedListener listener) {
        repository.checkOverlap(lesson, new LessonRepository.OnOverlapCheckListener() {
            @Override
            public void onOverlap(boolean hasOverlap) {
                if (hasOverlap) {
                    listener.onFailure(new Exception("השיעור מתנגש עם שיעור קיים."));
                } else {
                    repository.addLesson(lesson, listener);
                }
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void fetchSuggestions(String field, LessonRepository.OnFetchSuggestionsListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            repository.fetchSuggestions(field, listener);
        }
    }
}
