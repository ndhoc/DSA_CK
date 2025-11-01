package org.hocnd.hash;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class HashApp extends JFrame {
    private JTextField txtKey = new JTextField(6);
    private JTextField txtValue = new JTextField(6);
    private JTextField txtSize = new JTextField("5", 4);
    private JComboBox<String> cbMethod = new JComboBox<>(new String[]{"Linear Probing", "Chaining"});
    private JTextArea txtLog = new JTextArea(8, 50);

    private JButton btnInsert = new JButton("Thêm");
    private JButton btnSearch = new JButton("Tìm");
    private JButton btnClear = new JButton("Xóa tất cả");
    private JButton btnRandom = new JButton("Get Random 10");
    private JButton btnInit = new JButton("Khởi tạo");

    private JPanel tablePanel = new JPanel();
    private JScrollPane scrollTable;

    private HashTableLinear linear;
    private HashTableChaining chain;

    public HashApp() {
        setTitle("Hash Table - Division Method");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==== Panel trên cùng ====
        JPanel top = new JPanel();
        top.add(new JLabel("Số ô:"));
        top.add(txtSize);
        top.add(btnInit);
        top.add(new JLabel("Key:"));
        top.add(txtKey);
        top.add(new JLabel("Value:"));
        top.add(txtValue);
        top.add(cbMethod);
        top.add(btnInsert);
        top.add(btnSearch);
        top.add(btnClear);
        top.add(btnRandom);

        // ==== Log ====
        txtLog.setEditable(false);
        JScrollPane logScroll = new JScrollPane(txtLog);

        // ==== Bảng hiển thị ====
        tablePanel.setLayout(new GridLayout(0, 1, 5, 5));
        scrollTable = new JScrollPane(tablePanel);
        scrollTable.setPreferredSize(new Dimension(600, 250));

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JLabel("Bảng băm:", JLabel.CENTER), BorderLayout.NORTH);
        center.add(scrollTable, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        // ==== Sự kiện ====
        btnInit.addActionListener(e -> initTables());
        btnInsert.addActionListener(e -> insertAction());
        btnSearch.addActionListener(e -> searchAction());
        btnClear.addActionListener(e -> clearAction());
        btnRandom.addActionListener(e -> randomAction());

        initTables(); // khởi tạo mặc định
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initTables() {
        try {
            int n = Integer.parseInt(txtSize.getText().trim());
            if (n <= 0) throw new NumberFormatException();
            linear = new HashTableLinear(n);
            chain = new HashTableChaining(n);
            txtLog.append("Khởi tạo bảng với " + n + " ô.\n");
            updateDisplay();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số ô không hợp lệ!");
        }
    }

    private void insertAction() {
        try {
            int key = Integer.parseInt(txtKey.getText().trim());
            String value = txtValue.getText().trim();
            String msg;
            if (cbMethod.getSelectedIndex() == 0)
                msg = linear.insert(key, value);
            else
                msg = chain.insert(key, value);
            txtLog.append(msg + "\n");
            updateDisplay();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập key hợp lệ!");
        }
    }

    private void searchAction() {
        try {
            int key = Integer.parseInt(txtKey.getText().trim());
            String msg = (cbMethod.getSelectedIndex() == 0)
                    ? linear.search(key)
                    : chain.search(key);
            txtLog.append(msg + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập key hợp lệ!");
        }
    }

    private void clearAction() {
        if (cbMethod.getSelectedIndex() == 0) linear.clear();
        else chain.clear();
        txtLog.append("Đã xóa toàn bộ dữ liệu!\n");
        updateDisplay();
    }

    /** Sinh ngẫu nhiên 10 phần tử **/
    private void randomAction() {
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            int key = rand.nextInt(1000);
            String value = "Val" + key;
            if (cbMethod.getSelectedIndex() == 0)
                linear.insert(key, value);
            else
                chain.insert(key, value);
        }
        txtLog.append("Đã thêm ngẫu nhiên 10 phần tử.\n");
        updateDisplay();
    }

    /** Hiển thị bảng **/
    private void updateDisplay() {
        tablePanel.removeAll();
        int selected = cbMethod.getSelectedIndex();

        if (selected == 0) { // Linear
            Entry[] arr = linear.getTable();
            for (int i = 0; i < arr.length; i++) {
                JLabel lbl = new JLabel("Ô " + i + ": " + (arr[i] == null ? "(trống)" : arr[i]), JLabel.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(arr[i] == null ? new Color(245, 245, 245) : new Color(200, 255, 200));
                lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                tablePanel.add(lbl);
            }
        } else { // Chaining
            List<Entry>[] list = chain.getTable();
            for (int i = 0; i < list.length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ô ").append(i).append(": ");
                if (list[i].isEmpty()) sb.append("(trống)");
                else {
                    for (Entry e : list[i]) sb.append(e).append(" → ");
                    sb.setLength(sb.length() - 3);
                }

                JTextArea area = new JTextArea(sb.toString());
                area.setWrapStyleWord(true);
                area.setLineWrap(true);
                area.setEditable(false);
                area.setBackground(list[i].isEmpty() ? new Color(245, 245, 245) : new Color(200, 220, 255));
                area.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JScrollPane sp = new JScrollPane(area);
                sp.setPreferredSize(new Dimension(550, 40));
                sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                tablePanel.add(sp);
            }
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HashApp::new);
    }
}
