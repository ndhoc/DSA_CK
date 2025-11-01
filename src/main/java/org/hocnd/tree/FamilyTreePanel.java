package org.hocnd.tree;

import javax.swing.*;
import java.awt.*;

public class FamilyTreePanel extends JPanel {
    private PersonNode root;

    public void setRoot(PersonNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawNode(g2, root, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void drawNode(Graphics2D g, PersonNode node, int x, int y, int offset) {
        // Màu theo giới tính
        if ("Nam".equalsIgnoreCase(node.getGender())) g.setColor(new Color(70, 130, 180)); // xanh
        else g.setColor(new Color(255, 105, 180)); // hồng

        g.fillOval(x - 25, y - 20, 50, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x - 25, y - 20, 50, 40);

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(node.getName());
        g.drawString(node.getName(), x - w / 2, y + 5);

        int childY = y + 90;
        int n = node.getChildren().size();
        int startX = x - (n - 1) * offset / 2;

        for (int i = 0; i < n; i++) {
            PersonNode child = node.getChildren().get(i);
            int childX = startX + i * offset;
            g.drawLine(x, y + 20, childX, childY - 20);
            drawNode(g, child, childX, childY, offset / 2);
        }
    }
}