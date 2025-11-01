package org.hocnd.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tách biểu thức thành token (số, toán tử, ngoặc...).
 * Hỗ trợ dấu âm đơn ngôi: -3 hoặc (-2+5)
 */
public class ExpressionTokenizer {

    public static List<String> tokenize(String s) {
        ArrayList<String> tokens = new ArrayList<>();
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Biểu thức rỗng");

        s = s.replaceAll("\\s+", "");
        for (int i = 0; i < s.length();) {
            char c = s.charAt(i);

            // số hoặc dấu âm đơn ngôi
            if (Character.isDigit(c) || (c == '-' && (i == 0 || "+-*/(^".indexOf(s.charAt(i - 1)) >= 0))) {
                int j = i + 1;
                while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
                tokens.add(s.substring(i, j));
                i = j;
            } else {
                tokens.add(String.valueOf(c));
                i++;
            }
        }
        return tokens;
    }

    public static boolean isOperator(String t) {
        return "+-*/^".contains(t);
    }
}
