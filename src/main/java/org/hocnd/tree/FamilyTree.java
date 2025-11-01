package org.hocnd.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class FamilyTree {
    private PersonNode root;

    public FamilyTree(String rootName) {
        root = new PersonNode(rootName, "Nam");
    }

    public PersonNode getRoot() { return root; }

    public void addChild(PersonNode parent, String name, String gender) {
        if (parent == null) return;
        parent.addChild(new PersonNode(name, gender));
    }

    public void rename(PersonNode node, String newName) {
        if (node != null && newName != null && !newName.isEmpty()) {
            node.setName(newName);
        }
    }

    public void delete(PersonNode node) {
        if (node == null || node.getParent() == null) return;
        node.getParent().removeChild(node);
    }

    public DefaultMutableTreeNode buildTreeNode(PersonNode p) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(p);
        for (PersonNode child : p.getChildren()) {
            treeNode.add(buildTreeNode(child));
        }
        return treeNode;
    }
}
