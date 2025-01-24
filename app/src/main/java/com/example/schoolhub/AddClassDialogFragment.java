package com.example.schoolhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddClassDialogFragment extends DialogFragment {

    private AutoCompleteTextView classInput, teacherInput;
    private Button startTimeButton, endTimeButton, addButton, colorPickerButton;
    private RadioGroup daysGroup;
    private String selectedDay = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class_dialog, container, false);

        // Initialize Views
        classInput = view.findViewById(R.id.TextInputClass);
        teacherInput = view.findViewById(R.id.TextInputTeacher);
        startTimeButton = view.findViewById(R.id.startTimeBtn);
        endTimeButton = view.findViewById(R.id.endTimeBtn);
        addButton = view.findViewById(R.id.addButton);
        colorPickerButton = view.findViewById(R.id.colorPickerButton);
        daysGroup = view.findViewById(R.id.daysRadioGroup);

        // Handle Day Selection
        setupDaySelection();

        // Handle Start Time Button
        startTimeButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "בחר שעת התחלה", Toast.LENGTH_SHORT).show();
        });

        // Handle End Time Button
        endTimeButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "בחר שעת סיום", Toast.LENGTH_SHORT).show();
        });

        // Handle Add Button
        addButton.setOnClickListener(v -> {
            String className = classInput.getText().toString();
            String teacherName = teacherInput.getText().toString();

            if (className.isEmpty() || teacherName.isEmpty() || selectedDay.isEmpty()) {
                Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "שיעור נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        // Handle Color Picker Button
        colorPickerButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "בחר צבע למקצוע", Toast.LENGTH_SHORT).show();
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

    private void setupDaySelection() {
        daysGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                selectedDay = selectedButton.getText().toString();
                Toast.makeText(getContext(), "יום שנבחר: " + selectedDay, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
