package com.example.schoolhub.ui.fragment.TimeTable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.data.remote.FireBaseLessonRepository;
import com.example.schoolhub.ui.ViewModel.TimeTable.LessonViewModel;
import com.example.schoolhub.data.local.model.TimeTable.Teacher;
import com.example.schoolhub.R;
import com.example.schoolhub.ui.adapter.TimeTable.ColorAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class AddClassDialogFragment extends DialogFragment {

    private MaterialAutoCompleteTextView classInput, teacherInput, ClassNumberInput;

    private ImageButton closeBtn;
    private MaterialButton startTimeButton, endTimeButton, addButton, colorPickerButton, btn_lesson_tasks;
    private RadioGroup daysGroup;
    private String selectedDay = "", StartTime, EndTime;
    private String selectedColor = "#1A6392"; // Default color

    private TextView previewLessonName, previewTeacherName, previewRoomNum, tvTimeDisplay;
    private FireBaseLessonRepository repository = new FireBaseLessonRepository();

    private static final String[] PASTEL_COLORS = {
            "#AEC6CF", // Pastel Blue
            "#77DD77", // Pastel Green
            "#F49AC2", // Pastel Pink
            "#FFB347", // Pastel Orange
            "#B39EB5", // Pastel Purple
            "#FF6961"  // Light Red
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class_dialog, container, false);

        LessonViewModel lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        initializeUI(view);
        setupDaySelection();
        setupDynamicPreview();
        setupAutoComplete(lessonViewModel);
        setupColorPicker();

        addButton.setOnClickListener(v -> handleAddLesson(view));
        closeBtn.setOnClickListener(v -> handleCloseDialog());

        return view;
    }

    private void handleAddLesson(View view) {
        String className = classInput.getText().toString().trim();
        String teacherName = teacherInput.getText().toString().trim();
        String roomNum = ClassNumberInput.getText().toString().trim();
        String startTime = startTimeButton.getText().toString().trim();
        String endTime = endTimeButton.getText().toString().trim();

        if (className.isEmpty() || teacherName.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || selectedDay.isEmpty()) {
            Snackbar.make(requireView(), "נא למלא את כל השדות", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Teacher teacher = new Teacher(teacherName);
        Lesson newLesson = new Lesson("0", className, teacher, roomNum, selectedDay, startTime, endTime, selectedColor);

        repository.isLessonOverlapping(newLesson, isOverlapping -> {
            if (isOverlapping) {
                Snackbar.make(requireView(), "קיים שיעור חופף במערכת!", Snackbar.LENGTH_SHORT).show();
            } else {
                repository.createLesson(newLesson, requireView());
                dismiss();
            }
        });
    }

    private void handleCloseDialog() {
        if (areFieldsNotEmpty()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("האם אתה בטוח שברצונך לצאת?")
                    .setPositiveButton("כן", (dialog, which) -> dismiss())
                    .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            dismiss();
        }
    }

    private void initializeUI(View view) {
        classInput = view.findViewById(R.id.TextInputClass);
        teacherInput = view.findViewById(R.id.TextInputTeacher);
        ClassNumberInput = view.findViewById(R.id.ClassNumberInput);
        startTimeButton = view.findViewById(R.id.startTimeBtn);
        endTimeButton = view.findViewById(R.id.endTimeBtn);
        addButton = view.findViewById(R.id.addButton);
        colorPickerButton = view.findViewById(R.id.colorPickerButton);
        daysGroup = view.findViewById(R.id.daysRadioGroup);
        closeBtn = view.findViewById(R.id.closeBtn);
        // Preview UI
        previewLessonName = view.findViewById(R.id.tv_lessonName);
        btn_lesson_tasks = view.findViewById(R.id.btn_lesson_tasks);
        previewTeacherName = view.findViewById(R.id.tv_teacherName);
        previewRoomNum = view.findViewById(R.id.tv_roomNum);
        tvTimeDisplay = view.findViewById(R.id.tvTimeDisplay);
    }

    private boolean areFieldsNotEmpty() {
        return !classInput.getText().toString().isEmpty() ||
                !teacherInput.getText().toString().isEmpty() ||
                !ClassNumberInput.getText().toString().isEmpty() ||
                !startTimeButton.getText().toString().isEmpty() ||
                !endTimeButton.getText().toString().isEmpty() ||
                !selectedDay.isEmpty();

    }

    private void setupDynamicPreview() {
        StringBuilder time = new StringBuilder();
        // Update Preview based on Input
        classInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                previewLessonName.setText(s.toString().isEmpty() ? "שם שיעור" : s.toString());
            }
        });

        teacherInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                previewTeacherName.setText(s.toString().isEmpty() ? "שם המורה" : s.toString());
            }
        });

        ClassNumberInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                previewRoomNum.setText(s.toString().isEmpty() ? "מספר החדר" : s.toString());
            }
        });

        startTimeButton.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(8)
                    .setMinute(0)
                    .setTitleText("בחר שעת התחלה")
                    .build();

            picker.show(getParentFragmentManager(), "startTimePicker");

            picker.addOnPositiveButtonClickListener(view -> {
                StartTime = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
                startTimeButton.setText(StartTime);
                tvTimeDisplay.setText(time.append(" - ").append(StartTime).toString());
            });
        });

        endTimeButton.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(9)
                    .setMinute(0)
                    .setTitleText("בחר שעת סיום")
                    .build();

            picker.show(getParentFragmentManager(), "endTimePicker");

            picker.addOnPositiveButtonClickListener(view -> {

                EndTime = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
                endTimeButton.setText(EndTime);
                time.insert(0, EndTime);
                tvTimeDisplay.setText(time.toString());
            });
        });
    }

    private void setupDaySelection() {
        daysGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            selectedDay = selectedButton != null ? selectedButton.getText().toString() : "";
        });
    }

    private void setupColorPicker() {
        colorPickerButton.setOnClickListener(v -> {
            // הכנה של AlertDialog (בלי כפתורי OK/Cancel אוטומטיים)
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            final AlertDialog dialog = builder.create();

            // טוענים את ה-Layout של הגריד
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_color_picker, null);

            // מוצאים את ה-RecyclerView ומגדירים LayoutManager כגריד של 3 עמודות
            RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerColorPicker);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

            // יוצרים Adapter עם מערך הצבעים הפסטליים שלנו
            ColorAdapter adapter = new ColorAdapter(PASTEL_COLORS, colorHex -> {
                // המשתמש בחר צבע בגריד
                selectedColor = colorHex;

                // עדכון ה-UI לפריוויו:
                btn_lesson_tasks.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor(selectedColor)
                        )
                );
                previewLessonName.setTextColor(
                        android.graphics.Color.parseColor(selectedColor)
                );

                // סגירת הדיאלוג לאחר הבחירה
                dialog.dismiss();
            });

            recyclerView.setAdapter(adapter);

            // מציבים את ה-View
            dialog.setView(dialogView);
            dialog.show();
        });
    }

    private void setupAutoComplete(LessonViewModel viewModel) {
        viewModel.getClassNames().observe(getViewLifecycleOwner(), classNames -> {
            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, classNames);
            classInput.setAdapter(classAdapter);
        });
        viewModel.getTeacherNames().observe(getViewLifecycleOwner(), teacherNames -> {
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, teacherNames);
            teacherInput.setAdapter(teacherAdapter);
        });
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}

