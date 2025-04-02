
package com.example.schoolhub.ui.fragment.Calculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class baseCalculator extends Fragment implements BaseSelectionBottomSheet.BaseSelectionListener {

    protected String selectedBase1 = "Decimal", selectedBase2 = "Decimal", selectedBaseResult = "Decimal";
    protected boolean isAdvMode = false;
    protected TextInputEditText eq1, eq2, selectedEq;
    protected MaterialButton btnClear, btnConvert, btnBackSpace, btnBase1, btnBase2, btnBaseRes;
    private TextView tvResult;
    private MaterialSwitch advModeSwtch;
    private GestureHandler gestureHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedEq = eq1;
        tvResult = view.findViewById(R.id.tvResult);
        advModeSwtch = view.findViewById(R.id.advModeSwtch);
        eq1 = view.findViewById(R.id.eq1);
        eq2 = view.findViewById(R.id.eq2);
        btnClear = view.findViewById(R.id.btnClear);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnBase1 = view.findViewById(R.id.btnBase1);
        btnBase2 = view.findViewById(R.id.btnBase2);
        btnBaseRes = view.findViewById(R.id.btnBaseRes);

        ButtonManager.setUpBases(this);


        List<MaterialButton> numButtons = ButtonManager.getNumberButtons(view, requireContext());

        ButtonManager.setupButtons(this, numButtons);
        gestureHandler = new GestureHandler(view.findViewById(R.id.viewFlipper), () -> isAdvMode);

        MaterialCardView value2CardView = view.findViewById(R.id.value2CardView);
        advModeSwtch.setOnCheckedChangeListener((compoundButton, b) -> {
            isAdvMode = b;
            value2CardView.setVisibility(b ? View.VISIBLE : View.GONE);
        });
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                selectedEq = (TextInputEditText) v;
                String selectedBase = (v == eq1) ? selectedBase1 : selectedBase2;
                ButtonManager.changeButtons(numButtons, selectedBase);
            }
        };
        eq1.setOnFocusChangeListener(focusChangeListener);
        eq2.setOnFocusChangeListener(focusChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gestureHandler = null;
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
    }

    public String getSelectedBase2() {
        return selectedBase2;
    }

    public void setSelectedBase2(String base) {
        this.selectedBase2 = base;
    }

    @Override
    public void onBaseSelected(String base, String target) {
        switch (target) {
            case "input1":
                selectedBase1 = base;
                btnBase1.setText(base);
                ButtonManager.changeButtons(ButtonManager.getNumberButtons(getView(), requireContext()), base);
                break;
            case "input2":
                selectedBase2 = base;
                btnBase2.setText(base);
                ButtonManager.changeButtons(ButtonManager.getNumberButtons(getView(), requireContext()), base);
                break;
            case "result":
                selectedBaseResult = base;
                btnBaseRes.setText(base);
                break;
        }
    }
}
