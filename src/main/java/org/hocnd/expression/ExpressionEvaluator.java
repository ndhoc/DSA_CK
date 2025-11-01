package org.hocnd.expression;

import java.util.*;

/**
 * Tính giá trị biểu thức hậu tố, xử lý và ghi log toàn bộ quá trình.
 * Có bắt và ném lại các ngoại lệ cho UI hiển thị.
 */
public class ExpressionEvaluator {

    public static double evaluatePostfix(String postfix, StringBuilder log) {
        MyStack<Double> st = new MyStack<>();
        if (postfix == null || postfix.trim().isEmpty())
            throw new IllegalArgumentException("Biểu thức rỗng");

        List<String> tokens = Arrays.asList(postfix.trim().split("\\s+"));
        log.append("=== BẮT ĐẦU TÍNH GIÁ TRỊ ===\n");

        for (String t : tokens) {
            if (t.isEmpty()) continue;
            log.append("Đọc token: ").append(t).append("\n");

            if (isNumber(t)) {
                double v = Double.parseDouble(t);
                st.push(v);
                log.append("Push ").append(fmt(v)).append(" vào stack\n");
            } else {
                Double b = st.pop(), a = st.pop();
                if (a == null || b == null)
                    throw new IllegalArgumentException("Thiếu toán hạng cho " + t);

                log.append("Pop a=").append(fmt(a)).append(", b=").append(fmt(b)).append("\n");
                double r;
                switch (t) {
                    case "+": r = a + b; break;
                    case "-": r = a - b; break;
                    case "*": r = a * b; break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("chia cho 0");
                        r = a / b; break;
                    case "^": r = Math.pow(a, b); break;
                    default: throw new IllegalArgumentException("Toán tử không hợp lệ: " + t);
                }

                st.push(r);
                log.append("Tính ").append(fmt(a)).append(" ").append(t).append(" ").append(fmt(b))
                        .append(" = ").append(fmt(r)).append(" → Push ").append(fmt(r)).append(" vào stack\n");
            }
        }

        Double res = st.pop();
        if (res == null || !st.isEmpty())
            throw new IllegalArgumentException("Biểu thức không hợp lệ");

        log.append(">>> KẾT QUẢ CUỐI CÙNG: ").append(fmt(res)).append("\n");
        return res;
    }

    private static boolean isNumber(String s) {
        try { Double.parseDouble(s); return true; }
        catch (Exception e) { return false; }
    }

    private static String fmt(double v) {
        return (v == (long) v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
