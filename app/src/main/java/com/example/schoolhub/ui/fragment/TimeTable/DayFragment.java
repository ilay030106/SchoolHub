package com.example.schoolhub.ui.fragment.TimeTable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.ui.adapter.TimeTable.LessonAdapter;
import com.example.schoolhub.data.repository.TimeTable.LessonRepository;
import com.example.schoolhub.R;

import java.util.List;

public class DayFragment extends Fragment {

    private final String dayName;
    private RecyclerView lessonsList;
    private LessonAdapter lessonAdapter;

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
        loadLessons();
        setupSwipeToDelete();

        return view;
    }

    private void loadLessons() {
        LessonRepository lessonRepository = new LessonRepository(requireContext());

        lessonRepository.getLessonsByDay(dayName, new LessonRepository.OnFetchLessonsListener() {
            @Override
            public void onFetch(List<Lesson> lessons) {
                lessonAdapter = new LessonAdapter(lessons);
                lessonsList.setAdapter(lessonAdapter);
                setupSwipeToDelete();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Lesson lessonToDelete = lessonAdapter.getLessons().get(position);

                deleteLessonFromFirestore(lessonToDelete);
                lessonAdapter.getLessons().remove(position);
                lessonAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                RectF background = new RectF(viewHolder.itemView.getRight() + dX, viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                c.drawRect(background, paint);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(lessonsList);
    }

    private void deleteLessonFromFirestore(Lesson lesson) {
        LessonRepository lessonRepository = new LessonRepository(requireContext());

        lessonRepository.deleteLesson(lesson.getId(), new LessonRepository.OnLessonDeletedListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "השיעור נמחק בהצלחה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "שגיאה במחיקת השיעור: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}