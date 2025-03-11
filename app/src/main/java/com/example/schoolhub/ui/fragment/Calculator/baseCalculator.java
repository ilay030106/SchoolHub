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
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.ViewFlipper;
        import com.example.schoolhub.R;
        import com.google.android.material.button.MaterialButton;
        import com.google.android.material.materialswitch.MaterialSwitch;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.function.Predicate;

public class baseCalculator extends Fragment {

            private Spinner spinnerBase;
            private EditText etInput;
            private TextView tvResult;
            private String selectedBase = "Decimal";
            private MaterialSwitch advModeSwtch;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                return inflater.inflate(R.layout.fragment_base_calculator, container, false);
            }

            @Override
            public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                spinnerBase = view.findViewById(R.id.spinnerBase);
                etInput = view.findViewById(R.id.etInput);
                tvResult = view.findViewById(R.id.tvResult);
                advModeSwtch = view.findViewById(R.id.advModeSwtch);

                List<MaterialButton> numButtons = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    numButtons.add(view.findViewById(getResources().getIdentifier("btn" + i, "id", requireContext().getPackageName())));
                }
                for (char c = 'A'; c <= 'F'; c++) {
                    numButtons.add(view.findViewById(getResources().getIdentifier("btn" + c, "id", requireContext().getPackageName())));
                }

                spinnerBase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedBase = parent.getItemAtPosition(position).toString();
                        Predicate<MaterialButton> predicate;
                        switch (selectedBase) {
                            case "Binary":
                                predicate = button -> button.getText().toString().matches("[01]");
                                break;
                            case "Hexadecimal":
                                predicate = button -> button.getText().toString().matches("[0-9A-F]");
                                break;
                            default:
                                predicate = button -> button.getText().toString().matches("[0-9]");
                                break;
                        }
                        numButtons.forEach(button -> button.setEnabled(predicate.test(button)));
                        numButtons.stream().filter(button -> !predicate.test(button))
                            .forEach(button -> button.setBackgroundTintList(getResources().getColorStateList(R.color.darkBlue)));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                ViewFlipper viewFlipper = view.findViewById(R.id.viewFlipper);
                GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (e1.getX() - e2.getX() > 100) {
                            viewFlipper.showNext();
                            return true;
                        } else if (e2.getX() - e1.getX() > 100) {
                            viewFlipper.showPrevious();
                            return true;
                        }
                        return false;
                    }
                });
                viewFlipper.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

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
                        result = num == 0 ? 0 : num / 2;
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
        }