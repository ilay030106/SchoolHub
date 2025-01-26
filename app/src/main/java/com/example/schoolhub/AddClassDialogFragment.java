package com.example.schoolhub;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.List;

public class AddClassDialogFragment extends DialogFragment {

    private AutoCompleteTextView classInput, teacherInput;
    private MaterialButton startTimeButton, endTimeButton, addButton, colorPickerButton, btn_lesson;
    private RadioGroup daysGroup;
    private String selectedDay = "";
    private String selectedColor = "#1A6392"; // ברירת מחדל לצבע
    private ImageButton closeBtn;

    // רכיבי Preview
    private TextView previewLessonName, previewTeacherName, previewRoomNum, previewStartTime, previewEndTime;
    private View previewColorIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class_dialog, container, false);

        // אתחול ViewModel
        LessonViewModel lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // אתחול רכיבי UI
        initializeUI(view);
        setupDaySelection(view);
        // תצוגה דינמית של Preview
        setupDynamicPreview();

        // AutoComplete לשם השיעור והמורה
        setupAutoComplete(lessonViewModel);

        // כפתור בחירת צבע
        setupColorPicker();

        // כפתור הוספת שיעור
        addButton.setOnClickListener(v -> {
            String className = classInput.getText().toString();
            String teacherName = teacherInput.getText().toString();
            String startTime = startTimeButton.getText().toString();
            String endTime = endTimeButton.getText().toString();

            if (className.isEmpty() || teacherName.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || selectedDay.isEmpty()) {
                Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher teacher = new Teacher(teacherName);
            Lesson newLesson = new Lesson(className, teacher, selectedDay, startTime, endTime, selectedColor);

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
        // רכיבי קלט
        classInput = view.findViewById(R.id.TextInputClass);
        teacherInput = view.findViewById(R.id.TextInputTeacher);
        startTimeButton = view.findViewById(R.id.startTimeBtn);
        endTimeButton = view.findViewById(R.id.endTimeBtn);
        addButton = view.findViewById(R.id.addButton);
        colorPickerButton = view.findViewById(R.id.colorPickerButton);
        daysGroup = view.findViewById(R.id.daysRadioGroup);

        // רכיבי Preview
        previewLessonName = view.findViewById(R.id.tv_lessonName);
        btn_lesson = view.findViewById(R.id.btn_lesson);
        previewTeacherName = view.findViewById(R.id.tv_teacherName);
        previewRoomNum = view.findViewById(R.id.tv_roomNum);
        previewStartTime = view.findViewById(R.id.tvStartTimeDisp);
        previewEndTime = view.findViewById(R.id.tvEndTimeDisp);
    }

    private void setupDynamicPreview() {
        // שינוי שם שיעור
        classInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                previewLessonName.setText(s.toString().isEmpty() ? "שם שיעור" : s.toString());
            }
        });

        // שינוי שם מורה
        teacherInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                previewTeacherName.setText(s.toString().isEmpty() ? "שם המורה" : s.toString());
            }
        });

        // שינוי שעת התחלה
        startTimeButton.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(8)
                    .setMinute(0)
                    .setTitleText("בחר שעת התחלה")
                    .build();

            picker.show(getParentFragmentManager(), "startTimePicker");

            picker.addOnPositiveButtonClickListener(view -> {
                String time = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
                startTimeButton.setText(time);
                previewStartTime.setText("שעת התחלה: " + time);
            });
        });

        // שינוי שעת סיום
        endTimeButton.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(9)
                    .setMinute(0)
                    .setTitleText("בחר שעת סיום")
                    .build();

            picker.show(getParentFragmentManager(), "endTimePicker");

            picker.addOnPositiveButtonClickListener(view -> {
                String time = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
                endTimeButton.setText(time);
                previewEndTime.setText("שעת סיום: " + time);
            });
        });
    }

    private void setupDaySelection(View view) {
        RadioGroup daysGroup = view.findViewById(R.id.daysRadioGroup);

        daysGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                selectedDay = selectedButton.getText().toString(); // שמירת היום שנבחר

            }
        });
    }

    private void setupColorPicker() {
        colorPickerButton.setOnClickListener(v -> {
            // רשימת צבעים אפשריים
            String[] colors = {"#FF5733", "#33FF57", "#3357FF", "#FFD700"};

            // דיאלוג בחירת צבע
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("בחר צבע")
                    .setItems(colors, (dialog, which) -> {
                        selectedColor = colors[which]; // שמור את הצבע הנבחר

                        // עדכון צבע הכפתור
                        btn_lesson.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor(selectedColor)));

                        // עדכון צבע שם השיעור בתצוגה
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
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch suggestions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}
