package com.example.schoolhub.ui.fragment.TimeTable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

        // ViewModel Initialization
        LessonViewModel lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // Initialize UI
        initializeUI(view);
        setupDaySelection(view);
        setupDynamicPreview();
        setupAutoComplete(lessonViewModel);
        setupColorPicker();

        // Add Button Listener
        addButton.setOnClickListener(v -> {
            String className = classInput.getText().toString();
            String teacherName = teacherInput.getText().toString();
            String roomNum = ClassNumberInput.getText().toString();
            String startTime = startTimeButton.getText().toString();
            String endTime = endTimeButton.getText().toString();

            if (className.isEmpty() || teacherName.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || selectedDay.isEmpty()) {
                Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }
            Teacher teacher = new Teacher(teacherName);
            Lesson newLesson = new Lesson(className, teacher, roomNum, selectedDay, startTime, endTime, selectedColor);
            if (!loh.isLessonOverlapping(newLesson)) {
                loh.createLesson(newLesson);
                loh.close();
                Snackbar snackbar = Snackbar.make(view, "שיעור נוסף בהצלחה", Snackbar.LENGTH_LONG);
                snackbar.setAction("בטל", v1 -> {
                   loh.open();
                   loh.deleteLessonById(newLesson.getId());
                   loh.close();
                });
                snackbar.show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "שיעור חופף עם שיעור קיים", Toast.LENGTH_SHORT).show();
            }

        });
        closeBtn.setOnClickListener(v ->{
            if(areFieldsNotEmpty()){
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("האם אתה בטוח שברצונך לצאת?").setPositiveButton("כן", (dialog, which) -> {
                    dismiss();
                }).setNegativeButton("לא", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
            }
            else {
                dismiss();
            }

        } );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
               !endTimeButton.getText().toString().isEmpty()||
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

    private void setupDaySelection(View view) {
        daysGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                selectedDay = selectedButton.getText().toString();
            }
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