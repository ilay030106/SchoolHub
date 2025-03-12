package com.example.schoolhub.ui.fragment.Calculator;

public class BaseConvertor {

    public static int convertToDecimal(String value, String base) {
        try {
            switch (base) {
                case "Binary":
                    return Integer.parseInt(value, 2);
                case "Hexa":
                    return Integer.parseInt(value, 16);
                default:
                    return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return 0; // Error case handled in UI
        }
    }

    public static String formatResult(int result, String base) {
        switch (base) {
            case "Binary":
                return Integer.toBinaryString(result);
            case "Hexa":
                return Integer.toHexString(result);
            default:
                return String.valueOf(result);
        }
    }
}
