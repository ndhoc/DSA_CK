package org.hocnd.expression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Giao diện chính - kết nối toàn bộ chương trình.
 * Xử lý tất cả các ngoại lệ và log chi tiết hoạt động stack.
 */
public class InfixConverterUI extends JFrame {
    private final JTextField tfInfix   = new JTextField(30);
    private final JTextField tfPostfix = new JTextField(30);
    private final JTextField tfPrefix  = new JTextField(30);
    private final JTextField tfResult  = new JTextField(30);
    private final JTextArea logArea    = new JTextArea(22, 52);

    private final JButton btnConvert  = new JButton("Chuyển đổi");
    private final JButton btnEvaluate = new JButton("Tính giá trị");
    private final JButton btnClear    = new JButton("Clear tất cả");

    public InfixConverterUI() {
        setTitle("Infix ↔ Postfix ↔ Prefix (Stack Visual Log)");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(250, 252, 255));

        // FORM
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Biểu thức Infix:")); form.add(tfInfix);
        form.add(new JLabel("Biểu thức Postfix:")); tfPostfix.setEditable(false); form.add(tfPostfix);
        form.add(new JLabel("Biểu thức Prefix:")); tfPrefix.setEditable(false); form.add(tfPrefix);
        form.add(new JLabel("Kết quả:")); tfResult.setEditable(false); form.add(tfResult);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        buttons.add(btnConvert); buttons.add(btnEvaluate); buttons.add(btnClear);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.NORTH);
        left.add(buttons, BorderLayout.CENTER);
        left.setBackground(new Color(250, 252, 255));

        // LOG
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 248, 255));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(480, 0));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x64B5F6), 2),
                "STACK LOG CHI TIẾT",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(25, 118, 210)
        ));

        add(left, BorderLayout.CENTER);
        add(scroll, BorderLayout.EAST);

        // ACTIONS
        btnConvert.addActionListener(this::onConvert);
        btnEvaluate.addActionListener(this::onEvaluate);
        btnClear.addActionListener(e -> clearAll());
    }

    private void onConvert(ActionEvent e) {
        new Thread(() -> {
            StringBuilder log = new StringBuilder();
            try {
                String infix = tfInfix.getText();
                String postfix = ExpressionConverter.infixToPostfix(infix, log);
                String prefix  = ExpressionConverter.infixToPrefix(infix, log);

                SwingUtilities.invokeLater(() -> {
                    tfPostfix.setText(postfix);
                    tfPrefix.setText(prefix);
                    tfResult.setText("");
                    logArea.setText(log.toString());
                });
            } catch (Exception ex) {
                String errMsg = cleanError(ex.getMessage());
                SwingUtilities.invokeLater(() -> {
                    tfResult.setText("Lỗi: " + errMsg);
                    log.append(">>> LỖI: ").append(errMsg).append("\n");
                    logArea.setText(log.toString());
                });
            }
        }).start();
    }

    private void onEvaluate(ActionEvent e) {
        new Thread(() -> {
            StringBuilder log = new StringBuilder();
            try {
                double val = ExpressionEvaluator.evaluatePostfix(tfPostfix.getText(), log);
                SwingUtilities.invokeLater(() -> {
                    tfResult.setText((val == (long) val) ? String.valueOf((long) val) : String.valueOf(val));
                    logArea.setText(log.toString());
                });
            } catch (Exception ex) {
                String errMsg = cleanError(ex.getMessage());
                SwingUtilities.invokeLater(() -> {
                    tfResult.setText("Lỗi: " + errMsg);
                    log.append(">>> LỖI: ").append(errMsg).append("\n");
                    logArea.setText(log.toString());
                });
            }
        }).start();
    }

    private String cleanError(String msg) {
        if (msg == null) return "Không xác định";
        msg = msg.trim();
        if (msg.toLowerCase().startsWith("lỗi:")) msg = msg.substring(4).trim();
        return msg;
    }

    private void clearAll() {
        tfInfix.setText("");
        tfPostfix.setText("");
        tfPrefix.setText("");
        tfResult.setText("");
        logArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InfixConverterUI().setVisible(true));
    }
}
