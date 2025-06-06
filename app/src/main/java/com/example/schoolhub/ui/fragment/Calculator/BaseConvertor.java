package com.example.schoolhub.ui.fragment.Calculator;

import org.mariuszgromada.math.mxparser.Expression;

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

    // ✅ פונקציה שמבצעת את החישוב בפועל
    public static int evaluateExpression(String rawExpression) throws Exception {
        // החלפת מילות מפתח לסימנים מתאימים
        String expr = rawExpression
                .replace("NOT", "~")
                .replace("SHL", "<<")
                .replace("SHR", ">>")
                .replaceAll("\\s+", "");

        Expression expression = new Expression(expr);
        double result = expression.calculate();

        if (Double.isNaN(result)) {
            throw new Exception("Invalid expression");
        }

        return (int) result;
    }
}

