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

public class baseCalculator extends Fragment implements BaseSelectionBottomSheet.BaseSelectionListener {

    protected String selectedBase1 = "Decimal", selectedBaseResult = "Decimal";
    protected boolean isAdvMode = false;
    protected TextInputEditText eq;
    protected MaterialButton btnClear, btnConvert, btnBackSpace, btnBase1, btnBaseRes;
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
                if (gridPagerAdapter != null) {
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