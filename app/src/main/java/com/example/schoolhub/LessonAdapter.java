package com.example.schoolhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final List<Lesson> lessons;

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item_layout, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.lessonName.setText(lesson.getName());
        holder.teacherName.setText(lesson.getTeacher().getName());
        holder.tv_roomNum.setText(lesson.getRoomNum());
        holder.timeRange.setText(lesson.getStartTime() + " - " + lesson.getEndTime());
        holder.btn_lesson_tasks.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(lesson.getColor())));
        holder.lessonName.setTextColor(android.graphics.Color.parseColor(lesson.getColor()));
        holder.btn_lesson_tasks.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView lessonName, teacherName, timeRange, tv_roomNum;
        MaterialButton btn_lesson_tasks;


        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonName = itemView.findViewById(R.id.tv_lessonName);
            teacherName = itemView.findViewById(R.id.tv_teacherName);
            tv_roomNum = itemView.findViewById(R.id.tv_roomNum);
            timeRange = itemView.findViewById(R.id.tvTimeDisplay);
            btn_lesson_tasks = itemView.findViewById(R.id.btn_lesson_tasks);
        }
    }
}
