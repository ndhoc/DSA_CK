package org.hocnd.tree;

import java.util.*;

public class PersonNode {
    private String name;
    private String gender; // Nam / Nữ
    private List<PersonNode> children = new ArrayList<>();
    private PersonNode parent;

    public PersonNode(String name, String gender) {
        this.name = (name == null || name.isEmpty()) ? "(Không tên)" : name.trim();
        this.gender = (gender == null || gender.isEmpty()) ? "Nam" : gender;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name.trim(); }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public PersonNode getParent() { return parent; }
    public void setParent(PersonNode parent) { this.parent = parent; }

    public List<PersonNode> getChildren() { return children; }

    public void addChild(PersonNode child) {
        if (child != null) {
            child.setParent(this);
            children.add(child);
        }
    }

    public boolean removeChild(PersonNode child) {
        if (child == null) return false;
        boolean ok = children.remove(child);
        if (ok) child.setParent(null);
        return ok;
    }

    @Override
    public String toString() {
        return name + " (" + gender + ")";
    }
}