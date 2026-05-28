package org.example.juegofinalsupremo.data;

public class Pila<T> {
    private Node<T> top;
    private int size;

    public void push(T value) {
        Node<T> node = new Node<T>(value);
        node.next = top;
        top = node;
        size++;
    }

    public T pop() {
        if (top == null) {
            throw new IllegalStateException("La pila esta vacia");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private static class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
