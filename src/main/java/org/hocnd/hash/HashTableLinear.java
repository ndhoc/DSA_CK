package org.hocnd.hash;

public class HashTableLinear {
    private Entry[] table;
    private int size;
    private int count;

    public HashTableLinear(int size) {
        this.size = size;
        table = new Entry[size];
        count = 0;
    }

    private int hash(int key) {
        return key % size;
    }

    /** Thêm phần tử, tự mở rộng khi đầy */
    public String insert(int key, String value) {
        if (count >= size * 0.75) { // khi load factor > 75%, mở rộng
            resize();
        }

        int index = hash(key);
        int start = index;

        while (table[index] != null && table[index].key != key) {
            index = (index + 1) % size;
            if (index == start) {
                resize();
                return insert(key, value); // chèn lại sau khi mở rộng
            }
        }

        if (table[index] == null) count++;
        table[index] = new Entry(key, value);
        return "Đã thêm " + key + " vào ô " + index + (count == size ? " (Bảng đã đầy)" : "");
    }

    /** Tự mở rộng bảng khi đầy */
    private void resize() {
        int oldSize = size;
        size *= 2;
        Entry[] oldTable = table;
        table = new Entry[size];
        count = 0;

        for (Entry e : oldTable) {
            if (e != null) {
                rehash(e);
            }
        }
    }

    /** Dùng khi rehash toàn bộ dữ liệu */
    private void rehash(Entry entry) {
        int index = hash(entry.key);
        while (table[index] != null) {
            index = (index + 1) % size;
        }
        table[index] = entry;
        count++;
    }

    public String search(int key) {
        int index = hash(key);
        int start = index;
        while (table[index] != null) {
            if (table[index].key == key) return "Tìm thấy tại ô " + index + ": " + table[index].value;
            index = (index + 1) % size;
            if (index == start) break;
        }
        return "Không tìm thấy!";
    }

    public void clear() {
        table = new Entry[size];
        count = 0;
    }

    public Entry[] getTable() {
        return table;
    }

    public int getSize() {
        return size;
    }
}

