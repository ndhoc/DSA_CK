package org.hocnd.expression;

import java.util.LinkedList;

/**
 * Lớp ngăn xếp tổng quát (generic stack) sử dụng LinkedList.
 */
public class MyStack<E> {
    private final LinkedList<E> st = new LinkedList<>();

    public void push(E x) { st.addFirst(x); }
    public E pop() { return st.isEmpty() ? null : st.removeFirst(); }
    public E peek() { return st.isEmpty() ? null : st.getFirst(); }
    public boolean isEmpty() { return st.isEmpty(); }
}
