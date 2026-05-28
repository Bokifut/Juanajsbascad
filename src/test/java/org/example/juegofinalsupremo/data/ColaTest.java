package org.example.juegofinalsupremo.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaTest {

    @Test
    void enqueue() {
        Cola<Integer> cola = new Cola<>();
        cola.enqueue(10);
        cola.enqueue(20);
        assertEquals(2, cola.size());
        assertEquals(10, cola.peek());
    }

    @Test
    void dequeue() {
        Cola<String> cola = new Cola<>();
        cola.enqueue("A");
        cola.enqueue("B");
        assertEquals("A", cola.dequeue());
        assertEquals(1, cola.size());
        assertEquals("B", cola.peek());
        assertEquals("B", cola.dequeue());
        assertTrue(cola.isEmpty());
        assertThrows(IllegalStateException.class, cola::dequeue);
    }

    @Test
    void peek() {
        Cola<Integer> cola = new Cola<>();
        assertThrows(IllegalStateException.class, cola::peek);
        cola.enqueue(100);
        assertEquals(100, cola.peek());
        cola.enqueue(200);
        assertEquals(100, cola.peek());
    }

    @Test
    void isEmpty() {
        Cola<Integer> cola = new Cola<>();
        assertTrue(cola.isEmpty());
        cola.enqueue(1);
        assertFalse(cola.isEmpty());
        cola.dequeue();
        assertTrue(cola.isEmpty());
    }

    @Test
    void size() {
        Cola<Integer> cola = new Cola<>();
        assertEquals(0, cola.size());
        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);
        assertEquals(3, cola.size());
    }
}
