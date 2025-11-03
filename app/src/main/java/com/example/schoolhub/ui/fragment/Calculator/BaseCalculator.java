package com.example.schoolhub.ui.fragment.Calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import lombok.Getter;

public class BaseCalculator extends Fragment {

    protected String selectedBase = "Decimal";
    @Getter
    protected TextInputEditText eq;
    protected MaterialButton btnClear, btnConvert, btnBackSpace;
    protected FrameLayout btnDeci, btnHexa, btnBin;
    private int currentValue = 0;
    private View indicatorHexa, indicatorDeci, indicatorBin;
    private TextView tvHexValue, tvDecValue, tvBinValue;
    private GridPagerAdapter gridPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        eq = view.findViewById(R.id.eq1);
        tvHexValue = view.findViewById(R.id.tvHexValue);
        tvDecValue = view.findViewById(R.id.tvDecValue);
        tvBinValue = view.findViewById(R.id.tvBinValue);

        btnClear = view.findViewById(R.id.btnClear);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnDeci = view.findViewById(R.id.btnDeci);
        btnHexa = view.findViewById(R.id.btnHexa);
        btnBin = view.findViewById(R.id.btnBin);

        // Initialize indicator strips
        indicatorHexa = view.findViewById(R.id.indicatorHexa);
        indicatorDeci = view.findViewById(R.id.indicatorDeci);
        indicatorBin = view.findViewById(R.id.indicatorBin);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Setup base selector buttons
        setupBaseButtons();

        // Setup control buttons
        ButtonManager.setupButtons(this);

        // Setup grid adapter
        gridPagerAdapter = new GridPagerAdapter(requireContext(), buttonText -> {
            eq.append(buttonText);
            updateMultiBaseDisplay();
        });
        viewPager.setAdapter(gridPagerAdapter);
        viewPager.setPageTransformer((page, position) -> page.setAlpha(1 - Math.abs(position)));

        // Add text watcher to update display in real-time
        eq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMultiBaseDisplay();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Initialize display
        updateMultiBaseDisplay();
    }

    private void setupBaseButtons() {
        // Set initial active state
        updateIndicators("Decimal");

        btnHexa.setOnClickListener(v -> selectBase("Hexa"));
        btnDeci.setOnClickListener(v -> selectBase("Decimal"));
        btnBin.setOnClickListener(v -> selectBase("Binary"));
    }

    private void selectBase(String base) {
        selectedBase = base;

        // Update indicator colors
        updateIndicators(base);

        // Update grid buttons
        if (gridPagerAdapter != null) {
            gridPagerAdapter.updateGrid(base);
        }

        // Validate current expression
        String exp = Objects.requireNonNull(eq.getText()).toString();
        if (!exp.isEmpty() && !isValidExpression(exp, base)) {
            eq.setText("");
            updateMultiBaseDisplay();
        }
    }

    private void updateIndicators(String activeBase) {
        int activeColor = ContextCompat.getColor(requireContext(), R.color.lightBlue);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.darkBlue);

        // Update all indicators
        indicatorHexa.setBackgroundColor(activeBase.equals("Hexa") ? activeColor : inactiveColor);
        indicatorDeci.setBackgroundColor(activeBase.equals("Decimal") ? activeColor : inactiveColor);
        indicatorBin.setBackgroundColor(activeBase.equals("Binary") ? activeColor : inactiveColor);
    }

    private boolean isValidExpression(String expression, String base) {
        String[] tokens = expression.split("[^0-9A-Fa-f]+", -1);
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            switch (base) {
                case "Binary":
                    if (!token.matches("[01]+")) return false;
                    break;
                case "Hexa":
                    if (!token.matches("[0-9A-Fa-f]+")) return false;
                    break;
                default:
                    if (!token.matches("[0-9]+")) return false;
                    break;
            }
        }
        return true;
    }

    protected void updateMultiBaseDisplay() {
        String expression = Objects.requireNonNull(eq.getText()).toString().trim();

        if (expression.isEmpty()) {
            // Reset to 0
            tvHexValue.setText("0");
            tvDecValue.setText("0");
            tvBinValue.setText("0");
            currentValue = 0;
            return;
        }

        try {
            // Evaluate the expression
            int result = BaseConvertor.evaluateExpression(expression);
            currentValue = result;

            // Update all base displays
            tvHexValue.setText(BaseConvertor.formatResult(result, "Hexa"));
            tvDecValue.setText(BaseConvertor.formatResult(result, "Decimal"));
            tvBinValue.setText(BaseConvertor.formatResult(result, "Binary"));
        } catch (Exception e) {
            // If evaluation fails, show last valid value or 0
            if (currentValue != 0) {
                tvHexValue.setText(BaseConvertor.formatResult(currentValue, "Hexa"));
                tvDecValue.setText(BaseConvertor.formatResult(currentValue, "Decimal"));
                tvBinValue.setText(BaseConvertor.formatResult(currentValue, "Binary"));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}