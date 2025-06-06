package com.example.schoolhub.ui.fragment.TimeTable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.R;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.ui.ViewModel.TimeTable.LessonViewModel;
import com.example.schoolhub.ui.adapter.TimeTable.LessonAdapter;

import java.util.ArrayList;
import java.util.List;

public class DayFragment extends Fragment {

    private final String dayName;
    private RecyclerView lessonsList;
    private LessonAdapter lessonAdapter;
    private LessonViewModel lessonViewModel;

    public DayFragment(String dayName) {
        this.dayName = dayName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        TextView textView = view.findViewById(R.id.day_title);
        lessonsList = view.findViewById(R.id.LessonsList);

        textView.setText(dayName);
        lessonsList.setLayoutManager(new LinearLayoutManager(getContext()));

        lessonAdapter = new LessonAdapter(new ArrayList<>());
        lessonsList.setAdapter(lessonAdapter);

        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        lessonViewModel.getLessons().observe(getViewLifecycleOwner(), this::updateLessons);

        return view;
    }

    private void updateLessons(List<Lesson> lessons) {
        android.util.Log.d("DayFragment", "updateLessons called, lessons size: " + (lessons != null ? lessons.size() : 0));
        if (lessons != null) {
            for (Lesson lesson : lessons) {
                android.util.Log.d("DayFragment", "Lesson: " + lesson);
            }
        }
        List<Lesson> filteredLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getDay() != null && lesson.getDay().equalsIgnoreCase(dayName)) {
                filteredLessons.add(lesson);
            }
        }
        android.util.Log.d("DayFragment", "Filtered lessons size: " + filteredLessons.size());
        lessonAdapter.updateLessons(filteredLessons);
    }
}

