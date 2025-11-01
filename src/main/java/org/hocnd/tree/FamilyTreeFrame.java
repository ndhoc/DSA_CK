package org.hocnd.tree;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FamilyTreeFrame extends JFrame {
    private FamilyTree family;
    private JTree tree;
    private DefaultTreeModel model;
    private JTextField txtName;
    private JComboBox<String> genderBox;
    private PersonNode selected;
    private FamilyTreePanel rightPanel;
    private JTextArea logArea; // 🟩 Khu vực ghi log
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public FamilyTreeFrame() {
        super("Cây Gia Phả - Java Swing (Có Log Panel)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 650);
        setLayout(new BorderLayout());

        // Tắt icon mặc định JTree
        UIManager.put("Tree.openIcon", null);
        UIManager.put("Tree.closedIcon", null);
        UIManager.put("Tree.leafIcon", null);

        family = new FamilyTree("Tổ tiên");
        selected = family.getRoot();

        // ==== Panel hiển thị bên phải ====
        rightPanel = new FamilyTreePanel();
        rightPanel.setBackground(new Color(250, 250, 250));
        rightPanel.setRoot(family.getRoot());

        // ==== JTree bên trái ====
        DefaultMutableTreeNode rootNode = family.buildTreeNode(family.getRoot());
        model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new FamilyTreeRenderer());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tree), rightPanel);
        splitPane.setDividerLocation(320);
        add(splitPane, BorderLayout.CENTER);

        // ==== LOG PANEL (dưới cùng) ====
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("📜 Nhật ký thao tác"));
        add(logScroll, BorderLayout.SOUTH);

        // ==== Khi chọn node ====
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode sel = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (sel != null) {
                selected = (PersonNode) sel.getUserObject();
                rightPanel.setRoot(selected);
                addLog("Chọn thành viên \"" + selected.getName() + "\"");
            }
        });

        // ==== PANEL CHỨC NĂNG ====
        JPanel control = new JPanel();
        txtName = new JTextField(10);
        genderBox = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JButton btnAdd = new JButton("Thêm");
        JButton btnRename = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnStats = new JButton("Thống kê");
        JButton btnListLevel = new JButton("Liệt kê thế hệ");

        control.add(new JLabel("Tên:"));
        control.add(txtName);
        control.add(new JLabel("Giới tính:"));
        control.add(genderBox);
        control.add(btnAdd);
        control.add(btnRename);
        control.add(btnDelete);
        control.add(btnStats);
        control.add(btnListLevel);
        add(control, BorderLayout.NORTH);

        // ==== CÁC CHỨC NĂNG ====
        btnAdd.addActionListener(e -> {
            if (selected == null) return;
            String name = txtName.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
                return;
            }
            selected.addChild(new PersonNode(name, gender));
            refreshTree();
            addLog("Thêm thành viên \"" + name + "\" (" + gender + ") vào \"" + selected.getName() + "\"");
        });

        btnRename.addActionListener(e -> {
            if (selected == null) return;

            String name = txtName.getText().trim();
            String gender = (String) genderBox.getSelectedItem();

            if (!name.isEmpty()) {
                String oldName = selected.getName();
                String oldGender = selected.getGender();

                selected.setName(name);
                selected.setGender(gender);

                refreshTree();
                addLog("Cập nhật \"" + oldName + "\" (" + oldGender + ") → \"" + name + "\" (" + gender + ")");
            }
        });


        btnDelete.addActionListener(e -> {
            if (selected == null || selected.getParent() == null) {
                JOptionPane.showMessageDialog(this, "Không thể xóa node gốc!");
                return;
            }
            addLog("Xóa thành viên \"" + selected.getName() + "\" và toàn bộ con cháu của họ");
            selected.getParent().removeChild(selected);
            selected = family.getRoot();
            refreshTree();
        });

        btnStats.addActionListener(e -> {
            int gens = countGenerations(family.getRoot());
            int desc = countDescendants(selected);
            JOptionPane.showMessageDialog(this,
                    "Tổng số thế hệ: " + gens + "\n" +
                            "Số con cháu của \"" + selected.getName() + "\": " + desc);
            addLog("Thống kê: " + gens + " thế hệ, " + desc + " con cháu của \"" + selected.getName() + "\"");
        });

        btnListLevel.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Nhập số thứ tự thế hệ (tính từ 1):");
            try {
                int level = Integer.parseInt(input);
                java.util.List<PersonNode> list = getMembersAtLevel(family.getRoot(), level);
                if (list.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có thành viên ở thế hệ " + level);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (PersonNode p : list)
                        sb.append("- ").append(p.getName()).append(" (").append(p.getGender()).append(")\n");
                    JOptionPane.showMessageDialog(this,
                            "Các thành viên ở thế hệ " + level + ":\n" + sb);
                }
                addLog("Liệt kê thành viên ở thế hệ " + level + " (" + list.size() + " người)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ!");
            }
        });

        expandAll();
    }

    // ==== Renderer emoji thế hệ ====
    static class FamilyTreeRenderer extends DefaultTreeCellRenderer {
        private final Icon rootIcon = UIManager.getIcon("FileView.computerIcon");
        private final Icon gen2Icon = UIManager.getIcon("FileView.directoryIcon");
        private final Icon gen3Icon = UIManager.getIcon("FileView.fileIcon");
        private final Icon gen4Icon = UIManager.getIcon("Tree.expandedIcon");

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode node) {
                Object user = node.getUserObject();
                if (user instanceof PersonNode p) {
                    int depth = node.getLevel();
                    Icon icon = switch (depth) {
                        case 0 -> rootIcon;
                        case 1 -> gen2Icon;
                        case 2 -> gen3Icon;
                        default -> gen4Icon;
                    };
                    setIcon(icon);
                    setText(p.getName() + " (" + p.getGender() + ")");
                }
            }
            return this;
        }
    }

    // ==== Hàm tiện ích ====
    private void addLog(String message) {
        String time = "[" + timeFormat.format(new Date()) + "] ";
        logArea.append(time + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void refreshTree() {
        DefaultMutableTreeNode newRoot = family.buildTreeNode(family.getRoot());
        model.setRoot(newRoot);
        model.reload();
        expandAll();
        rightPanel.setRoot(selected);
    }

    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
    }

    private int countGenerations(PersonNode node) {
        if (node == null) return 0;
        if (node.getChildren().isEmpty()) return 1;
        int max = 0;
        for (PersonNode c : node.getChildren()) {
            max = Math.max(max, countGenerations(c));
        }
        return max + 1;
    }

    private int countDescendants(PersonNode node) {
        if (node == null) return 0;
        int count = node.getChildren().size();
        for (PersonNode c : node.getChildren()) {
            count += countDescendants(c);
        }
        return count;
    }

    private java.util.List<PersonNode> getMembersAtLevel(PersonNode root, int level) {
        java.util.List<PersonNode> res = new java.util.ArrayList<>();
        dfs(root, 1, level, res);
        return res;
    }

    private void dfs(PersonNode node, int depth, int target, java.util.List<PersonNode> res) {
        if (node == null) return;
        if (depth == target) res.add(node);
        for (PersonNode c : node.getChildren()) dfs(c, depth + 1, target, res);
    }
}
