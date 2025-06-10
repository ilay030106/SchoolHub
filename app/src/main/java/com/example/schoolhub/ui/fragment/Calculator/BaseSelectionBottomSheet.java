package com.example.schoolhub.ui.fragment.Calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schoolhub.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import lombok.Setter;

public class BaseSelectionBottomSheet extends BottomSheetDialogFragment {

    // ✅ FIX: Set the listener manually before showing the dialog
    @Setter
    private BaseSelectionListener listener;
    private String target;  // Store the target selection field

    public static BaseSelectionBottomSheet newInstance(String target) {
        BaseSelectionBottomSheet fragment = new BaseSelectionBottomSheet();
        Bundle args = new Bundle();
        args.putString("target", target);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_base_selector, container, false);

        target = getArguments() != null ? getArguments().getString("target") : "";

        MaterialButton btnDecimal = view.findViewById(R.id.button_decimal);
        MaterialButton btnBinary = view.findViewById(R.id.button_binary);
        MaterialButton btnHex = view.findViewById(R.id.button_hex);

        View.OnClickListener baseClickListener = v -> {
            if (listener != null) {  // ✅ FIX: Listener is set manually
                listener.onBaseSelected(((MaterialButton) v).getText().toString(), target);
            }
            dismiss();
        };

        btnDecimal.setOnClickListener(baseClickListener);
        btnBinary.setOnClickListener(baseClickListener);
        btnHex.setOnClickListener(baseClickListener);

        return view;
    }

    public interface BaseSelectionListener {
        void onBaseSelected(String base, String target);
    }
}
