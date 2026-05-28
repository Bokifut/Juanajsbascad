package org.example.juegofinalsupremo.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Lista<T> implements Iterable<T> {
    private Node<T> head;
    private int size;

    public void add(T value) {
        Node<T> node = new Node<T>(value);
        if (head == null) {
            head = node;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
        size++;
    }

    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    public void set(int index, T value) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.value = value;
    }

    public T removeAt(int index) {
        checkIndex(index);
        T removed;
        if (index == 0) {
            removed = head.value;
            head = head.next;
        } else {
            Node<T> previous = head;
            for (int i = 0; i < index - 1; i++) {
                previous = previous.next;
            }
            removed = previous.next.value;
            previous.next = previous.next.next;
        }
        size--;
        return removed;
    }

    public boolean remove(T value) {
        if (head == null) {
            return false;
        }
        if (same(head.value, value)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (same(current.next.value, value)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (same(current.value, value)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                if (current == null) {
                    throw new NoSuchElementException("La lista esta vacia");
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
    }

    private boolean same(T left, T right) {
        return left == right || (left != null && left.equals(right));
    }

    private static class Node<T> {
        private T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
