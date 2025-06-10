package com.example.schoolhub.ui.fragment.Calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;

public class ButtonManager {

    // Retrieves number buttons from the provided container view as a HashMap.
    public static HashMap<String, MaterialButton> getNumberButtons(View view, Context context) {
        HashMap<String, MaterialButton> numButtons = new HashMap<>();
        ViewGroup container = view.findViewById(R.id.gridNumbers);
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                numButtons.put(button.getText().toString().trim(), button);
            }
        }
        return numButtons;
    }

    public static HashMap<String, MaterialButton> getOperatorButtons(View view, Context context) {
        HashMap<String, MaterialButton> operatorButtons = new HashMap<>();
        ViewGroup container = view.findViewById(R.id.gridOperators);
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                operatorButtons.put(button.getText().toString().trim(), button);
            }
        }
        return operatorButtons;
    }

    // Sets up the click listeners for the non-grid buttons (clear, backspace, convert).
    @SuppressLint("SetTextI18n")
    public static void setupButtons(baseCalculator fragment) {
        fragment.btnClear.setOnClickListener(v -> {
            fragment.getSelectedEq().setText("");
            fragment.getTvResult().setText("Result: ");
        });
        fragment.btnBackSpace.setOnClickListener(v -> {
            String text = Objects.requireNonNull(fragment.getSelectedEq().getText()).toString();
            if (!text.isEmpty()) {
                fragment.getSelectedEq().setText(text.substring(0, text.length() - 1));
            }
        });
        fragment.btnConvert.setOnClickListener(v -> {

            String rawExpression = Objects.requireNonNull(fragment.getSelectedEq().getText()).toString();

            int result;
            try {
                result = BaseConvertor.evaluateExpression(rawExpression);
            } catch (Exception e) {
                Snackbar.make(fragment.requireView(), "Invalid expression", Snackbar.LENGTH_SHORT).show();
                return;
            }

            fragment.getTvResult().setText("Result: " + BaseConvertor.formatResult(result, fragment.selectedBaseResult));
        });
    }


    // Sets up the base selection dialogs.
    public static void setUpBases(baseCalculator fragment) {
        fragment.btnBase1.setOnClickListener(v -> showBaseDialog(fragment, "input1"));
        fragment.btnBaseRes.setOnClickListener(v -> showBaseDialog(fragment, "result"));
    }

    private static void showBaseDialog(baseCalculator fragment, String target) {
        BaseSelectionBottomSheet dialog = BaseSelectionBottomSheet.newInstance(target);
        dialog.setListener(fragment);
        dialog.show(fragment.getParentFragmentManager(), "BaseSelectionBottomSheet");
    }

    // Updates the enabled state of buttons based on the selected base.
    public static void changeButtons(HashMap<String, MaterialButton> buttons, String selectedBase) {
        Predicate<MaterialButton> predicate;
        switch (selectedBase) {
            case "Binary":
                predicate = button -> button.getText().toString().matches("[01]");
                break;
            case "Hexa":
                predicate = button -> button.getText().toString().matches("[0-9A-F]");
                break;
            default:
                predicate = button -> button.getText().toString().matches("[0-9]");
                break;
        }
        for (MaterialButton button : buttons.values()) {
            if (button != null) {
                boolean enabled = predicate.test(button);
                button.setEnabled(enabled);
                button.setBackgroundTintList(enabled ?
                        button.getContext().getResources().getColorStateList(R.color.lightBlue) :
                        button.getContext().getResources().getColorStateList(R.color.darkBlue));
            }
        }
    }

    // Sets up click listeners for grid buttons using the provided HashMap and click callback.
    public static void setupGridButtons(HashMap<String, MaterialButton> numButtons, GridPagerAdapter.OnGridButtonClickListener listener) {
        for (MaterialButton button : numButtons.values()) {
            if (button != null) { // Safety check to avoid NPE.
                button.setOnClickListener(v -> listener.onGridButtonClicked(button.getText().toString()));
            }
        }
    }
}
