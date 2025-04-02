package com.example.schoolhub.ui.fragment.Calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.function.Predicate;

public class ButtonManager {

    // Retrieves number buttons from the provided container view as a HashMap.
    public static HashMap<String, MaterialButton> getNumberButtons(View view, Context context) {
        HashMap<String, MaterialButton> numButtons = new HashMap<>();
        // Loop through digits 0-9.
        for (int i = 0; i < 10; i++) {
            MaterialButton button = view.findViewById(context.getResources()
                    .getIdentifier("btn" + i, "id", context.getPackageName()));
            numButtons.put(String.valueOf(i), button);
        }
        // Loop through letters A-F.
        for (char c = 'A'; c <= 'F'; c++) {
            MaterialButton button = view.findViewById(context.getResources()
                    .getIdentifier("btn" + c, "id", context.getPackageName()));
            numButtons.put(String.valueOf(c), button);
        }
        return numButtons;
    }

    // Sets up the click listeners for the non-grid buttons (clear, backspace, convert).
    @SuppressLint("SetTextI18n")
    public static void setupButtons(baseCalculator fragment) {
        fragment.btnClear.setOnClickListener(v -> fragment.getSelectedEq().setText(""));
        fragment.btnBackSpace.setOnClickListener(v -> {
            String text = fragment.getSelectedEq().getText().toString();
            if (!text.isEmpty()) {
                fragment.getSelectedEq().setText(text.substring(0, text.length() - 1));
            }
        });
        fragment.btnConvert.setOnClickListener(v -> {
            int value1 = fragment.eq1.getText().toString().isEmpty() ? 0 :
                    BaseConvertor.convertToDecimal(fragment.eq1.getText().toString(), fragment.getSelectedBase1());
            int value2 = fragment.isAdvMode ?
                    BaseConvertor.convertToDecimal(fragment.eq2.getText().toString(), fragment.getSelectedBase2()) : 0;
            int result = value1 + value2;
            fragment.getTvResult().setText("Result: " + BaseConvertor.formatResult(result, fragment.selectedBaseResult));
        });
    }

    // Sets up the base selection dialogs.
    public static void setUpBases(baseCalculator fragment) {
        fragment.btnBase1.setOnClickListener(v -> showBaseDialog(fragment, "input1"));
        fragment.btnBase2.setOnClickListener(v -> showBaseDialog(fragment, "input2"));
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
