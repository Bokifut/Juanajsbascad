package Cola;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ColaTest {

    @Test
    void encolar() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.encolar(2);
        assertEquals(2, cola.size());
        assertEquals(1, cola.frente());
    }

    @Test
    void desencolar() {
        Cola<String> cola = new Cola<>();
        cola.encolar("A");
        cola.encolar("B");
        assertEquals("A", cola.desencolar());
        assertEquals(1, cola.size());
        assertEquals("B", cola.frente());
        assertEquals("B", cola.desencolar());
        assertTrue(cola.estaVacia());
        assertThrows(NoSuchElementException.class, cola::desencolar);
    }

    @Test
    void frente() {
        Cola<Integer> cola = new Cola<>();
        assertThrows(NoSuchElementException.class, cola::frente);
        cola.encolar(100);
        assertEquals(100, cola.frente());
        assertEquals(1, cola.size()); // Verificar que no se borra
    }

    @Test
    void size() {
        Cola<Integer> cola = new Cola<>();
        assertEquals(0, cola.size());
        cola.encolar(1);
        cola.encolar(1);
        assertEquals(2, cola.size());
        cola.desencolar();
        assertEquals(1, cola.size());
    }

    @Test
    void estaVacia() {
        Cola<Integer> cola = new Cola<>();
        assertTrue(cola.estaVacia());
        cola.encolar(1);
        assertFalse(cola.estaVacia());
        cola.desencolar();
        assertTrue(cola.estaVacia());
    }

    @Test
    void limpiar() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.encolar(2);
        cola.limpiar();
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.size());
        assertThrows(NoSuchElementException.class, cola::frente);
    }

    @Test
    void iterator() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.encolar(2);
        cola.encolar(3);
        
        Iterator<Integer> it = cola.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testToString() {
        Cola<Integer> cola = new Cola<>();
        assertEquals("[]", cola.toString());
        cola.encolar(1);
        cola.encolar(2);
        assertEquals("[1 <- 2]", cola.toString());
    }
}
