package org.example.juegofinalsupremo.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PilaTest {

    @Test
    void push() {
        Pila<Integer> pila = new Pila<>();
        pila.push(10);
        pila.push(20);
        assertEquals(2, pila.size());
        assertFalse(pila.isEmpty());
    }

    @Test
    void pop() {
        Pila<String> pila = new Pila<>();
        pila.push("A");
        pila.push("B");
        
        assertEquals("B", pila.pop());
        assertEquals(1, pila.size());
        assertEquals("A", pila.pop());
        assertTrue(pila.isEmpty());
        
        assertThrows(IllegalStateException.class, pila::pop);
    }

    @Test
    void isEmpty() {
        Pila<Integer> pila = new Pila<>();
        assertTrue(pila.isEmpty());
        pila.push(1);
        assertFalse(pila.isEmpty());
        pila.pop();
        assertTrue(pila.isEmpty());
    }

    @Test
    void size() {
        Pila<Integer> pila = new Pila<>();
        assertEquals(0, pila.size());
        pila.push(1);
        pila.push(2);
        assertEquals(2, pila.size());
        pila.pop();
        assertEquals(1, pila.size());
    }
}
