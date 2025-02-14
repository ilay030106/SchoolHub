package com.example.schoolhub.ui.adapter.TimeTable;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.R;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final List<Lesson> lessons;

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item_layout, parent, false);
        return new LessonViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        StringBuilder time = new StringBuilder();
        holder.lessonName.setText(lesson.getName());
        holder.teacherName.setText(lesson.getTeacher().getName());
        holder.roomNum.setText(lesson.getRoomNum());
        time.append(lesson.getEndTime()).append(" - ").append(lesson.getStartTime());

        holder.time.setText(time.toString());
        holder.btn_lesson_tasks.setOnClickListener(view -> {
            //TODO: Open tasks fragment with the wanted lesson
        });
        holder.btn_lesson_tasks.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor(lesson.getColor())
                )
        );
        holder.lessonName.setTextColor(
                android.graphics.Color.parseColor(lesson.getColor())
        );
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView lessonName, teacherName, roomNum, time;
        MaterialButton btn_lesson_tasks;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonName = itemView.findViewById(R.id.tv_lessonName);
            teacherName = itemView.findViewById(R.id.tv_teacherName);
            roomNum = itemView.findViewById(R.id.tv_roomNum);
            time = itemView.findViewById(R.id.tvTimeDisplay);
            btn_lesson_tasks = itemView.findViewById(R.id.btn_lesson_tasks);
        }
    }
}