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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class baseCalculator extends Fragment implements BaseSelectionBottomSheet.BaseSelectionListener {

    protected String selectedBase1 = "Decimal", selectedBase2 = "Decimal", selectedBaseResult = "Decimal";
    protected boolean isAdvMode = false;
    protected TextInputEditText eq1, eq2, selectedEq;
    protected MaterialButton btnClear, btnConvert, btnBackSpace, btnBase1, btnBase2, btnBaseRes;
    private TextView tvResult;
    private MaterialSwitch advModeSwtch;
    private ViewPager2 viewPager;
    // Keep a reference to the adapter for updating the grid.
    private GridPagerAdapter gridPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views and buttons.
        eq1 = view.findViewById(R.id.eq1);
        eq2 = view.findViewById(R.id.eq2);
        selectedEq = eq1;
        tvResult = view.findViewById(R.id.tvResult);
        advModeSwtch = view.findViewById(R.id.advModeSwtch);
        btnClear = view.findViewById(R.id.btnClear);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnBase1 = view.findViewById(R.id.btnBase1);
        btnBase2 = view.findViewById(R.id.btnBase2);
        btnBaseRes = view.findViewById(R.id.btnBaseRes);
        viewPager = view.findViewById(R.id.viewPager);

        // Set up base selection dialogs.
        ButtonManager.setUpBases(this);

        // Set up non-grid buttons.
        ButtonManager.setupButtons(this);

        // Toggle visibility for the second input based on advanced mode.
        MaterialCardView value2CardView = view.findViewById(R.id.value2CardView);
        advModeSwtch.setOnCheckedChangeListener((compoundButton, b) -> {
            isAdvMode = b;
            value2CardView.setVisibility(b ? View.VISIBLE : View.GONE);
        });

        // When focus changes, update the grid with the correct base.
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                selectedEq = (TextInputEditText) v;
                String base = (v == eq1) ? selectedBase1 : selectedBase2;
                if (gridPagerAdapter != null) {
                    gridPagerAdapter.updateGrid(base);
                }
            }
        };
        eq1.setOnFocusChangeListener(focusChangeListener);
        eq2.setOnFocusChangeListener(focusChangeListener);

        // Initialize ViewPager2 with the custom adapter.
        gridPagerAdapter = new GridPagerAdapter(requireContext(), buttonText -> {
            selectedEq.append(buttonText);
        });
        viewPager.setAdapter(gridPagerAdapter);
        viewPager.setPageTransformer((page, position) -> page.setAlpha(1 - Math.abs(position)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // No additional cleanup required.
    }

    public TextInputEditText getSelectedEq() {
        return selectedEq;
    }

    public TextView getTvResult() {
        return tvResult;
    }

    public void setSelectedBaseRes(String base) {
        this.selectedBaseResult = base;
    }

    public String getSelectedBase1() {
        return selectedBase1;
    }

    public void setSelectedBase1(String base) {
        this.selectedBase1 = base;
        if (selectedEq == eq1 && gridPagerAdapter != null) {
            gridPagerAdapter.updateGrid(base);
        }
    }

    public String getSelectedBase2() {
        return selectedBase2;
    }

    public void setSelectedBase2(String base) {
        this.selectedBase2 = base;
        if (selectedEq == eq2 && gridPagerAdapter != null) {
            gridPagerAdapter.updateGrid(base);
        }
    }

    @Override
    public void onBaseSelected(String base, String target) {
        switch (target) {
            case "input1":
                selectedBase1 = base;
                btnBase1.setText(base);
                if (selectedEq == eq1 && gridPagerAdapter != null) {
                    gridPagerAdapter.updateGrid(base);
                }
                break;
            case "input2":
                selectedBase2 = base;
                btnBase2.setText(base);
                if (selectedEq == eq2 && gridPagerAdapter != null) {
                    gridPagerAdapter.updateGrid(base);
                }
                break;
            case "result":
                selectedBaseResult = base;
                btnBaseRes.setText(base);
                break;
        }
    }
}
