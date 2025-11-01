package org.hocnd.hash;

import java.util.*;

public class HashTableChaining {
    private List<Entry>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTableChaining(int size) {
        this.size = size;
        table = new LinkedList[size];
        for (int i = 0; i < size; i++)
            table[i] = new LinkedList<>();
    }

    private int hash(int key) {
        return key % size;
    }

    public String insert(int key, String value) {
        int index = hash(key);
        for (Entry e : table[index])
            if (e.key == key) return "Key đã tồn tại!";
        table[index].add(new Entry(key, value));
        return "Đã thêm " + key + " vào danh sách tại ô " + index;
    }

    public String search(int key) {
        int index = hash(key);
        for (Entry e : table[index])
            if (e.key == key) return "Tìm thấy tại ô " + index + ": " + e.value;
        return "Không tìm thấy!";
    }

    public void clear() {
        for (List<Entry> bucket : table)
            bucket.clear();
    }

    public List<Entry>[] getTable() {
        return table;
    }
}
