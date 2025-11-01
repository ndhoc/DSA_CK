package org.hocnd.tree;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FamilyTreeFrame frame = new FamilyTreeFrame();
            frame.setVisible(true);
        });
    }
}

