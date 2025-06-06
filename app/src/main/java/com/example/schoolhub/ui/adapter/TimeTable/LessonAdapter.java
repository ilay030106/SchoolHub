package com.example.schoolhub.ui.adapter.TimeTable;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.schoolhub.R;
import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    Lesson lesson;

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
    public void onBindViewHolder(@NonNull LessonViewHolder holder, @SuppressLint("RecyclerView") int position) {
        lesson = lessons.get(position);
        StringBuilder time = new StringBuilder();
        holder.lessonName.setText(lesson.getName());
        holder.teacherName.setText(lesson.getTeacher().getName());
        holder.roomNum.setText(lesson.getRoomNum());
        time.append(lesson.getEndTime()).append(" - ").append(lesson.getStartTime());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.rightBottomWrapper);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.leftBottomWrapper);

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

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.rightBottomWrapper);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.leftBottomWrapper);

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                // When the surface view completely covers the bottom view
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                // Called when swiping
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                // Called before opening
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                if (layout.getDragEdge() == SwipeLayout.DragEdge.Right) {
                    Log.d("SWIPE", "Swiped Right (Delete)");
                } else if (layout.getDragEdge() == SwipeLayout.DragEdge.Left) {
                    Log.d("SWIPE", "Swiped Left (Favorite)");
                }
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                // Called before closing
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                // Called when user's hand is released
            }
        });

        // Delete Button Action
        holder.btnDelete.setOnClickListener(v -> {
            lessons.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lessons.size());
            Snackbar.make(holder.itemView, "Lesson deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v1 -> {

                        lessons.add(position, lesson);
                        notifyItemInserted(position);
                    }).show();
        });

        // Favorite Button Action
        holder.btnEdit.setOnClickListener(v -> {
            //TODO: Open edit lesson fragment
        });
    }

    public void updateLessons(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
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
        MaterialButton btn_lesson_tasks, btnDelete, btnEdit;
        SwipeLayout swipeLayout;
        LinearLayout leftBottomWrapper, rightBottomWrapper;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            leftBottomWrapper = itemView.findViewById(R.id.left_bottom_wrapper);
            rightBottomWrapper = itemView.findViewById(R.id.right_bottom_wrapper);
            lessonName = itemView.findViewById(R.id.tv_lessonName);
            teacherName = itemView.findViewById(R.id.tv_teacherName);
            roomNum = itemView.findViewById(R.id.tv_roomNum);
            time = itemView.findViewById(R.id.tvTimeDisplay);
            btn_lesson_tasks = itemView.findViewById(R.id.btn_lesson_tasks);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);

        }
    }
}