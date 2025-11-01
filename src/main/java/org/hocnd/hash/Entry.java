package org.hocnd.hash;

public class Entry {
    public int key;
    public String value;

    public Entry(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
