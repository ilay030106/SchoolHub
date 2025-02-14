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
    private PartialSwipeCallback partialSwipeCallback;
    private GestureDetector gestureDetector;
    private final Map<Integer, Boolean> swipeDirectionMap = new HashMap<>();

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

        // Set up swipe gestures
        setUpSwipeActions();

        return view;
    }
    private void setUpSwipeActions() {
        float clampDistance = 300f;
        int leftIcon = R.drawable.edit_icon;
        int rightIcon = R.drawable.delete_icon;
        int bgColor = Color.RED;

        partialSwipeCallback = new PartialSwipeCallback(
                clampDistance,
                rightIcon,
                leftIcon,
                bgColor,
                new PartialSwipeCallback.OnSwipeListener() {
                    @Override
                    public void onSwipedLeft(int position, RecyclerView.ViewHolder viewHolder) {
                        lessonAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onSwipedRight(int position, RecyclerView.ViewHolder viewHolder) {
                        lessonAdapter.notifyItemChanged(position);
                    }
                }
        );

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(partialSwipeCallback);
        itemTouchHelper.attachToRecyclerView(lessonsList);

        lessonsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    float x = e.getX();
                    float y = e.getY();

                    View childView = lessonsList.findChildViewUnder(x, y);
                    if (childView != null) {
                        RecyclerView.ViewHolder vh = lessonsList.getChildViewHolder(childView);
                        int position = vh.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            RectF iconBounds = partialSwipeCallback.getIconBounds(position);
                            if (iconBounds != null && iconBounds.contains(x, y)) {
                                Boolean wasSwipedRight = swipeDirectionMap.get(position);
                                if (wasSwipedRight != null) {
                                    if (wasSwipedRight) {
                                        handleRightSwipeClick(position);
                                    } else {
                                        handleLeftSwipeClick(position);
                                    }
                                }
                            } else {
                                lessonsList.post(() -> lessonAdapter.notifyItemChanged(position));
                            }
                        }
                    }
                    return super.onSingleTapUp(e);
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return gestureDetector.onTouchEvent(e);
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }
    /**
     * Checks if the tap is inside the revealed icon's area
     */
    private boolean isInRevealedIconZone(float touchX, RecyclerView.ViewHolder viewHolder) {
        View itemView = viewHolder.itemView;
        int iconAreaWidth = 100; // Adjust width as needed

        int rightIconLeftBoundary = itemView.getRight() - iconAreaWidth;
        int leftIconRightBoundary = itemView.getLeft() + iconAreaWidth;

        return (touchX >= rightIconLeftBoundary) || (touchX <= leftIconRightBoundary);
    }

    /**
     * Handles action when the user clicks on the revealed area for a right swipe
     */
    private void handleRightSwipeClick(int position) {
        // Example: Show a delete confirmation dialog
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Lesson?")
                .setMessage("Are you sure you want to delete this lesson?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    loh.open();
                    Lesson lessonToDelete = lessonAdapter.getLessons().get(position);
                    loh.deleteLessonById(lessonToDelete.getId());
                    loh.close();

                    // Remove from adapter
                    lessonAdapter.getLessons().remove(position);
                    lessonAdapter.notifyItemRemoved(position);

                    Snackbar.make(lessonsList, "Lesson deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                loh.open();
                                loh.createLesson(lessonToDelete);
                                loh.close();
                                lessonAdapter.getLessons().add(position, lessonToDelete);
                                lessonAdapter.notifyItemInserted(position);
                            }).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    lessonAdapter.notifyItemChanged(position);
                })
                .show();
    }

    /**
     * Handles action when the user clicks on the revealed area for a left swipe
     */
    private void handleLeftSwipeClick(int position) {
        // Example: Open edit screen
        Snackbar.make(lessonsList, "Edit feature coming soon!", Snackbar.LENGTH_SHORT).show();
    }
}
