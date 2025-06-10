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
                return Integer.toHexString(result).toUpperCase();
            default:
                return String.valueOf(result);
        }
    }

    // ✅ פונקציה שמבצעת את החישוב בפועל
    public static int evaluateExpression(String rawExpression) throws Exception {
        String expr = rawExpression
                .replace("NOT", "~")
                .replace("SHL", "<<")
                .replace("SHR", ">>")
                .replaceAll("\\s+", "");

        // Recursively replace bitwise operators with function calls for mxparser
        expr = replaceBitwiseOperators(expr);

        // Register bitwise functions
        org.mariuszgromada.math.mxparser.Function bitand = new org.mariuszgromada.math.mxparser.Function("bitand(a,b) = floor(a) & floor(b)");
        org.mariuszgromada.math.mxparser.Function bitor = new org.mariuszgromada.math.mxparser.Function("bitor(a,b) = floor(a) | floor(b)");
        org.mariuszgromada.math.mxparser.Function bitxor = new org.mariuszgromada.math.mxparser.Function("bitxor(a,b) = floor(a) ^ floor(b)");

        Expression expression = new Expression(expr, bitand, bitor, bitxor);
        double result = expression.calculate();
        if (Double.isNaN(result)) {
            throw new Exception("Invalid expression");
        }
        return (int) result;
    }

    // Recursively replace bitwise operators with function calls for mxparser (regex approach)
    private static String replaceBitwiseOperators(String expr) {
        // Replace &
        while (expr.matches(".*[0-9A-Fa-f)]+&[0-9A-Fa-f(]+.*")) {
            expr = expr.replaceAll("([0-9A-Fa-f)]+)&([0-9A-Fa-f(]+)", "bitand($1,$2)");
        }
        // Replace |
        while (expr.matches(".*[0-9A-Fa-f)]+\\|[0-9A-Fa-f(]+.*")) {
            expr = expr.replaceAll("([0-9A-Fa-f)]+)\\|([0-9A-Fa-f(]+)", "bitor($1,$2)");
        }
        // Replace ^
        while (expr.matches(".*[0-9A-Fa-f)]+\\^[0-9A-Fa-f(]+.*")) {
            expr = expr.replaceAll("([0-9A-Fa-f)]+)\\^([0-9A-Fa-f(]+)", "bitxor($1,$2)");
        }
        return expr;
    }

    // Replace a given operator with a function, respecting parentheses and precedence
    private static String replaceOperator(String expr, char op, String func) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        int last = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (depth == 0 && c == op) {
                // Find left operand
                int l = i - 1;
                while (l >= 0 && (Character.isLetterOrDigit(expr.charAt(l)) || expr.charAt(l) == ')'))
                    l--;
                String left = expr.substring(last, i).trim();
                // Find right operand
                int r = i + 1;
                int startR = r;
                int paren = 0;
                while (r < expr.length() && (paren > 0 || Character.isLetterOrDigit(expr.charAt(r)) || expr.charAt(r) == '(' || expr.charAt(r) == ')')) {
                    if (expr.charAt(r) == '(') paren++;
                    if (expr.charAt(r) == ')') paren--;
                    r++;
                }
                String right = expr.substring(startR, r).trim();
                sb.append(func).append('(').append(left).append(',').append(right).append(')');
                last = r;
                i = r - 1;
            }
        }
        if (last < expr.length()) sb.append(expr.substring(last));
        return sb.length() == 0 ? expr : sb.toString();
    }

    // Helper: auto-detect base (hex if contains A-F, else decimal)
    private static int parseIntAutoBase(String value) {
        if (value.matches("[0-9A-Fa-f]+")) {
            if (value.matches(".*[A-Fa-f].*")) {
                return Integer.parseInt(value, 16);
            } else {
                return Integer.parseInt(value, 10);
            }
        }
        return 0;
    }
}
