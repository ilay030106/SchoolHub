
package com.example.schoolhub.ui.fragment.Calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ButtonManager {

    public static List<MaterialButton> getNumberButtons(View view, Context context) {
        List<MaterialButton> numButtons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numButtons.add(view.findViewById(context.getResources().getIdentifier("btn" + i, "id", context.getPackageName())));
        }
        for (char c = 'A'; c <= 'F'; c++) {
            numButtons.add(view.findViewById(context.getResources().getIdentifier("btn" + c, "id", context.getPackageName())));
        }
        return numButtons;
    }

    @SuppressLint("SetTextI18n")
    public static void setupButtons(baseCalculator fragment, List<MaterialButton> numButtons) {
        fragment.btnClear.setOnClickListener(v -> fragment.getSelectedEq().setText(""));
        fragment.btnBackSpace.setOnClickListener(v -> {
            String text = fragment.getSelectedEq().getText().toString();
            if (!text.isEmpty()) {
                fragment.getSelectedEq().setText(text.substring(0, text.length() - 1));
            }
        });
        numButtons.forEach(b -> b.setOnClickListener(v -> fragment.getSelectedEq().append(b.getText().toString())));
        fragment.btnConvert.setOnClickListener(v -> {
            int value1 = fragment.eq1.getText().toString().isEmpty() ? 0 : BaseConvertor.convertToDecimal(fragment.eq1.getText().toString(), fragment.getSelectedBase1());
            int value2 = fragment.isAdvMode ? BaseConvertor.convertToDecimal(fragment.eq2.getText().toString(), fragment.getSelectedBase2()) : 0;
            int result = value1 + value2;
            fragment.getTvResult().setText("Result: " + BaseConvertor.formatResult(result, fragment.selectedBaseResult));
        });

    }

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

    public static void changeButtons(List<MaterialButton> buttons, String selectedBase) {
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

        for (MaterialButton button : buttons) {
            boolean enabled = predicate.test(button);
            button.setEnabled(enabled);
            button.setBackgroundTintList(enabled ? button.getContext().getResources().getColorStateList(R.color.lightBlue) :
                    button.getContext().getResources().getColorStateList(R.color.darkBlue));
        }
    }
}