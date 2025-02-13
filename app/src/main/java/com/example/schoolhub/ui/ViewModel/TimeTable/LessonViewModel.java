package com.example.schoolhub.ui.ViewModel.TimeTable;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.data.repository.TimeTable.LessonRepository;

public class LessonViewModel extends AndroidViewModel {
    private final LessonRepository repository;

    public LessonViewModel(Application application) {
        super(application);
        repository = new LessonRepository(application);
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
        repository.fetchSuggestions(field, listener);
    }
}