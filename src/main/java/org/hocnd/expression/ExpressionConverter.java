package org.hocnd.expression;

import java.util.*;

/**
 * Chuyển đổi biểu thức từ Infix sang Postfix và Prefix.
 * Ghi log chi tiết quá trình hoạt động của Stack.
 */
public class ExpressionConverter {

    public static String infixToPostfix(String infix, StringBuilder log) {
        log.append("=== BẮT ĐẦU CHUYỂN ĐỔI INFIX → POSTFIX ===\n");
        List<String> tokens = ExpressionTokenizer.tokenize(infix);
        List<String> result = convert(tokens, false, log);
        String res = String.join(" ", result);
        log.append(">>> KẾT QUẢ POSTFIX: ").append(res).append("\n\n");
        return res;
    }

    public static String infixToPrefix(String infix, StringBuilder log) {
        log.append("=== BẮT ĐẦU CHUYỂN ĐỔI INFIX → PREFIX ===\n");
        List<String> tokens = ExpressionTokenizer.tokenize(infix);
        ArrayList<String> rev = new ArrayList<>();

        for (int i = tokens.size() - 1; i >= 0; i--) {
            String t = tokens.get(i);
            if (t.equals("(")) rev.add(")");
            else if (t.equals(")")) rev.add("(");
            else rev.add(t);
        }

        log.append("Đã đảo biểu thức và đổi ngoặc.\n");
        List<String> post = convert(rev, true, log);
        Collections.reverse(post);
        String res = String.join(" ", post);
        log.append(">>> KẾT QUẢ PREFIX: ").append(res).append("\n\n");
        return res;
    }

    private static List<String> convert(List<String> tokens, boolean reversed, StringBuilder log) {
        MyStack<String> st = new MyStack<>();
        ArrayList<String> out = new ArrayList<>();

        for (String t : tokens) {
            log.append("Đọc token: ").append(t).append("\n");

            if (isNumber(t)) {
                out.add(t);
                log.append("→ Thêm ").append(t).append(" vào output\n");
            } else if (t.equals("(")) {
                st.push(t);
                log.append("Push ( vào stack\n");
            } else if (t.equals(")")) {
                while (!st.isEmpty() && !st.peek().equals("(")) {
                    String op = st.pop();
                    out.add(op);
                    log.append("Pop ").append(op).append(" → output\n");
                }
                if (st.isEmpty()) throw new IllegalArgumentException("Ngoặc không khớp");
                st.pop();
                log.append("Pop ( khỏi stack\n");
            } else if (ExpressionTokenizer.isOperator(t)) {
                while (!st.isEmpty() && ExpressionTokenizer.isOperator(st.peek()) &&
                        (prec(st.peek()) > prec(t) ||
                                (prec(st.peek()) == prec(t) && (reversed ? rightAssoc(t) : !rightAssoc(t))))) {
                    String op = st.pop();
                    out.add(op);
                    log.append("Pop ").append(op).append(" → output\n");
                }
                st.push(t);
                log.append("Push ").append(t).append(" vào stack\n");
            } else {
                throw new IllegalArgumentException("Ký tự không hợp lệ: " + t);
            }
        }

        while (!st.isEmpty()) {
            String op = st.pop();
            if ("()".contains(op)) throw new IllegalArgumentException("Ngoặc không khớp");
            out.add(op);
            log.append("Pop ").append(op).append(" → output\n");
        }
        return out;
    }

    private static boolean isNumber(String s) {
        try { Double.parseDouble(s); return true; }
        catch (Exception e) { return false; }
    }

    private static int prec(String op) {
        switch (op) {
            case "^": return 3;
            case "*": case "/": return 2;
            case "+": case "-": return 1;
            default: return -1;
        }
    }

    private static boolean rightAssoc(String op) { return "^".equals(op); }
}
