package com.example.schoolhub.ui.fragment.TimeTable;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.data.local.Database.TimeTable.LessonsOpenHelper;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.ui.adapter.PartialSwipeCallback;
import com.example.schoolhub.ui.adapter.TimeTable.LessonAdapter;
import com.example.schoolhub.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class DayFragment extends Fragment {

    private final String dayName;
    private RecyclerView lessonsList;
    private LessonAdapter lessonAdapter;
    private LessonsOpenHelper loh;

    public DayFragment(String dayName) {
        this.dayName = dayName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        TextView textView = view.findViewById(R.id.day_title);
        lessonsList = view.findViewById(R.id.LessonsList);

        loh = LessonsOpenHelper.getInstance(getContext());
        loh.open();
        lessonAdapter = new LessonAdapter(loh.getLessonsForDay(dayName));
        lessonsList.setAdapter(lessonAdapter);
        loh.close();

        textView.setText(dayName);
        lessonsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }







}
