package org.example.juegofinalsupremo.data;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaTest {

    @Test
    void testAddAndGetAndSizeAndIsEmpty() {
        Lista<String> lista = new Lista<>();
        assertTrue(lista.isEmpty());
        assertEquals(0, lista.size());

        lista.add("A");
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("A", lista.get(0));

        lista.add("B");
        lista.add("C");
        assertEquals(3, lista.size());
        assertEquals("B", lista.get(1));
        assertEquals("C", lista.get(2));
    }

    @Test
    void testCheckIndex() {
        Lista<String> lista = new Lista<>();
        assertThrows(IndexOutOfBoundsException.class, () -> lista.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.get(-1));

        lista.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> lista.get(1));
    }

    @Test
    void testSet() {
        Lista<String> lista = new Lista<>();
        lista.add("A");
        lista.add("B");
        lista.add("C");

        lista.set(0, "Z");
        assertEquals("Z", lista.get(0));

        lista.set(1, "Y");
        assertEquals("Y", lista.get(1));

        lista.set(2, "X");
        assertEquals("X", lista.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> lista.set(-1, "X"));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.set(3, "X"));
    }

    @Test
    void testRemoveAt() {
        Lista<String> lista = new Lista<>();
        lista.add("A");
        lista.add("B");
        lista.add("C");
        lista.add("D");

        assertEquals("C", lista.removeAt(2));
        assertEquals(3, lista.size());
        assertEquals("D", lista.get(2));

        assertEquals("B", lista.removeAt(1));
        assertEquals(2, lista.size());

        assertEquals("A", lista.removeAt(0));
        assertEquals(1, lista.size());
        assertEquals("D", lista.get(0));

        assertThrows(IndexOutOfBoundsException.class, () -> lista.removeAt(1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.removeAt(-1));
    }

    @Test
    void testRemoveValue() {
        Lista<String> lista = new Lista<>();
        assertFalse(lista.remove("A"));

        lista.add("A");
        assertTrue(lista.remove("A"));
        assertEquals(0, lista.size());

        lista.add("A");
        lista.add("B");
        lista.add("C");
        lista.add("D");

        assertFalse(lista.remove("Z"));

        assertTrue(lista.remove("A"));
        assertEquals(3, lista.size());
        assertEquals("B", lista.get(0));

        assertTrue(lista.remove("C"));
        assertEquals(2, lista.size());
        assertEquals("D", lista.get(1));

        assertTrue(lista.remove("D"));
        assertEquals(1, lista.size());
        assertEquals("B", lista.get(0));

        assertTrue(lista.remove("B"));
        assertTrue(lista.isEmpty());
    }

    @Test
    void testContainsAndIndexOfAndSame() {
        Lista<String> lista = new Lista<>();
        lista.add("A");
        lista.add(null);
        lista.add("C");
        lista.add(new String("C"));

        assertTrue(lista.contains("A"));
        assertTrue(lista.contains(null));
        assertTrue(lista.contains("C"));
        assertFalse(lista.contains("Z"));

        assertEquals(0, lista.indexOf("A"));
        assertEquals(1, lista.indexOf(null));
        assertEquals(2, lista.indexOf("C"));
        assertEquals(-1, lista.indexOf("Z"));

        assertTrue(lista.remove(null));
        assertFalse(lista.contains(null));
    }

    @Test
    void testClear() {
        Lista<String> lista = new Lista<>();
        lista.add("A");
        lista.add("B");
        lista.clear();
        assertTrue(lista.isEmpty());
        assertEquals(0, lista.size());
        assertThrows(IndexOutOfBoundsException.class, () -> lista.get(0));
    }

    @Test
    void testIterator() {
        Lista<String> lista = new Lista<>();
        Iterator<String> emptyIterator = lista.iterator();
        assertFalse(emptyIterator.hasNext());
        assertThrows(NoSuchElementException.class, emptyIterator::next);

        lista.add("A");
        lista.add("B");

        Iterator<String> iterator = lista.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}