package com.example.schoolhub.ui.fragment.Calculator;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import lombok.Getter;

public class baseCalculator extends Fragment implements BaseSelectionBottomSheet.BaseSelectionListener {

    protected String selectedBase1 = "Decimal", selectedBaseResult = "Decimal";
    private String lastResultBase = "Decimal";

    protected TextInputEditText eq;
    protected MaterialButton btnClear, btnConvert, btnBackSpace, btnBase1, btnBaseRes;
    @Getter
    private TextView tvResult;
    private GridPagerAdapter gridPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eq = view.findViewById(R.id.eq1);

        tvResult = view.findViewById(R.id.tvResult);
        btnClear = view.findViewById(R.id.btnClear);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnBase1 = view.findViewById(R.id.btnBase1);
        btnBaseRes = view.findViewById(R.id.btnBaseRes);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        ButtonManager.setUpBases(this);
        ButtonManager.setupButtons(this);

        gridPagerAdapter = new GridPagerAdapter(requireContext(), buttonText -> {
            eq.append(buttonText);
        });
        viewPager.setAdapter(gridPagerAdapter);
        viewPager.setPageTransformer((page, position) -> page.setAlpha(1 - Math.abs(position)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public TextInputEditText getSelectedEq() {
        return eq;
    }


    public void setSelectedBase1(String base) {
        this.selectedBase1 = base;
        if (gridPagerAdapter != null) {
            gridPagerAdapter.updateGrid(base);
        }
    }

    @Override
    public void onBaseSelected(String base, String target) {
        switch (target) {
            case "input1":
                selectedBase1 = base;
                btnBase1.setText(base);
                String exp = Objects.requireNonNull(eq.getText()).toString();
                if (gridPagerAdapter != null) {
                    gridPagerAdapter.updateGrid(base);
                }
                if (!exp.isEmpty()) {
                    // Validate all number tokens in the expression for the selected base
                    boolean valid = true;
                    String[] tokens = exp.split("[^0-9A-Fa-f]+", -1); // split by non-number chars
                    for (String token : tokens) {
                        if (token.isEmpty()) continue;
                        switch (base) {
                            case "Binary":
                                if (!token.matches("[01]+")) valid = false;
                                break;
                            case "Hexa":
                                if (!token.matches("[0-9A-Fa-f]+")) valid = false;
                                break;
                            default:
                                if (!token.matches("[0-9]+")) valid = false;
                                break;
                        }
                        if (!valid) break;
                    }
                    if (!valid) {
                        eq.setText("");
                    }
                }
                break;
            case "result":
                selectedBaseResult = base;
                btnBaseRes.setText(base);
                String resText = Objects.requireNonNull(tvResult.getText()).toString();
                if (!resText.isEmpty()) {
                    String prefix = "Result: ";
                    if (resText.startsWith(prefix)) {
                        String numeric = resText.substring(prefix.length()).trim();
                        // Convert from lastResultBase to decimal, then to new base
                        int decimalValue = BaseConvertor.convertToDecimal(numeric, lastResultBase);
                        String formatted = BaseConvertor.formatResult(decimalValue, base);
                        tvResult.setText(prefix + formatted);
                    } else {
                        int decimalValue = BaseConvertor.convertToDecimal(resText.trim(), lastResultBase);
                        String formatted = BaseConvertor.formatResult(decimalValue, base);
                        tvResult.setText(prefix + formatted);
                    }
                }
                lastResultBase = base;
                break;
        }
    }
}