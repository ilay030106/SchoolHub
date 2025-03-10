package com.example.schoolhub.ui.fragment.Calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.schoolhub.R;

public class baseCalculator extends Fragment {

    private Spinner spinnerBase;
    private EditText etInput;
    private TextView tvResult;
    private String selectedBase = "Decimal";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views inside the fragment layout
        spinnerBase = view.findViewById(R.id.spinnerBase);
        etInput = view.findViewById(R.id.etInput);
        tvResult = view.findViewById(R.id.tvResult);

        // Set listener for Spinner
        spinnerBase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBase = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ViewFlipper viewFlipper = view.findViewById(R.id.viewFlipper);

        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 100) {
                    viewFlipper.showNext(); // Switch to operators
                    return true;
                } else if (e2.getX() - e1.getX() > 100) {
                    viewFlipper.showPrevious(); // Switch to numbers
                    return true;
                }
                return false;
            }
        });

        viewFlipper.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        // Set button listeners
        setButtonListeners(view);
    }

    private void setButtonListeners(View view) {

        view.findViewById(R.id.btnNot).setOnClickListener(v -> performNotOperation());

        view.findViewById(R.id.btnShiftLeft).setOnClickListener(v -> performOperation("<<"));
        view.findViewById(R.id.btnShiftRight).setOnClickListener(v -> performOperation(">>"));


    }

    private void performOperation(String operator) {
        String inputText = etInput.getText().toString();
        if (inputText.isEmpty()) {
            tvResult.setText("Error: No input!");
            return;
        }

        int num = convertToDecimal(inputText);
        int result = 0;

        switch (operator) {
            case "+":
                result = num + 1;
                break;
            case "-":
                result = num - 1;
                break;
            case "*":
                result = num * 2;
                break;
            case "/":
                if (num == 0) {
                    tvResult.setText("Error: Division by zero!");
                    return;
                }
                result = num / 2;
                break;
            case "&":
                result = num & 1;
                break;
            case "|":
                result = num | 1;
                break;
            case "^":
                result = num ^ 1;
                break;
            case "<<":
                result = num << 1;
                break;
            case ">>":
                result = num >> 1;
                break;
        }

        tvResult.setText("Result: " + formatResult(result));
    }

    private void performNotOperation() {
        String inputText = etInput.getText().toString();
        if (inputText.isEmpty()) {
            tvResult.setText("Error: No input!");
            return;
        }

        int num = convertToDecimal(inputText);
        int result = ~num;
        tvResult.setText("Result: " + formatResult(result));
    }

    @SuppressLint("SetTextI18n")
    private int convertToDecimal(String value) {
        try {
            switch (selectedBase) {
                case "Binary":
                    return Integer.parseInt(value, 2);
                case "Hexadecimal":
                    return Integer.parseInt(value, 16);
                default:
                    return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            tvResult.setText("Error: Invalid number!");
            return 0;
        }
    }

    private String formatResult(int result) {
        switch (selectedBase) {
            case "Binary":
                return Integer.toBinaryString(result);
            case "Hexadecimal":
                return Integer.toHexString(result);
            default:
                return String.valueOf(result);
        }
    }

    private void convertBase() {
        String inputText = etInput.getText().toString();
        if (inputText.isEmpty()) {
            tvResult.setText("Error: No input!");
            return;
        }

        int decimalValue = convertToDecimal(inputText);
        tvResult.setText("Converted: " + formatResult(decimalValue));
    }
}
