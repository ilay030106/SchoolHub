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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.data.repository.TimeTable.LessonRepository;
import com.example.schoolhub.ui.ViewModel.TimeTable.LessonViewModel;
import com.example.schoolhub.data.local.model.TimeTable.Teacher;
import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.List;

public class AddClassDialogFragment extends DialogFragment {

    private MaterialAutoCompleteTextView classInput, teacherInput, ClassNumberInput;
    private MaterialButton startTimeButton, endTimeButton, addButton, colorPickerButton, btn_lesson_tasks;
    private RadioGroup daysGroup;
    private String selectedDay = "", StartTime, EndTime;
    private String selectedColor = "#1A6392"; // Default color

    private TextView previewLessonName, previewTeacherName, previewRoomNum, tvTimeDisplay;

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

            lessonViewModel.addLesson(newLesson, new LessonRepository.OnLessonAddedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "שיעור נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

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

        // Preview UI
        previewLessonName = view.findViewById(R.id.tv_lessonName);
        btn_lesson_tasks = view.findViewById(R.id.btn_lesson_tasks);
        previewTeacherName = view.findViewById(R.id.tv_teacherName);
        previewRoomNum = view.findViewById(R.id.tv_roomNum);
        tvTimeDisplay = view.findViewById(R.id.tvTimeDisplay);
    }

    private void setupDynamicPreview() {
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
                tvTimeDisplay.setText(StartTime + " - ");
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
                tvTimeDisplay.setText(tvTimeDisplay.getText() + EndTime);
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
            String[] colors = {"#FF5733", "#33FF57", "#3357FF", "#FFD700"};

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("בחר צבע")
                    .setItems(colors, (dialog, which) -> {
                        selectedColor = colors[which];
                        btn_lesson_tasks.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor(selectedColor)));
                        previewLessonName.setTextColor(android.graphics.Color.parseColor(selectedColor));
                    })
                    .show();
        });
    }

    private void setupAutoComplete(LessonViewModel viewModel) {
        viewModel.fetchSuggestions("name", new LessonRepository.OnFetchSuggestionsListener() {
            @Override
            public void onFetch(List<String> suggestions) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
                classInput.setAdapter(adapter);
                classInput.setThreshold(1); // Show suggestions after typing 1 character
                classInput.setOnFocusChangeListener((view, hasFocus) -> {
                    if (hasFocus) {
                        classInput.showDropDown(); // Show suggestions when focused
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch suggestions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Repeat for teacherInput
        viewModel.fetchSuggestions("teacher", new LessonRepository.OnFetchSuggestionsListener() {
            @Override
            public void onFetch(List<String> suggestions) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
                teacherInput.setAdapter(adapter);
                teacherInput.setThreshold(1); // Show suggestions after typing 1 character
                teacherInput.setOnFocusChangeListener((view, hasFocus) -> {
                    if (hasFocus) {
                        teacherInput.showDropDown(); // Show suggestions when focused
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch suggestions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Repeat for ClassNumberInput
        viewModel.fetchSuggestions("roomNum", new LessonRepository.OnFetchSuggestionsListener() {
            @Override
            public void onFetch(List<String> suggestions) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
                ClassNumberInput.setAdapter(adapter);
                ClassNumberInput.setThreshold(1); // Show suggestions after typing 1 character
                ClassNumberInput.setOnFocusChangeListener((view, hasFocus) -> {
                    if (hasFocus) {
                        ClassNumberInput.showDropDown(); // Show suggestions when focused
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch suggestions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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