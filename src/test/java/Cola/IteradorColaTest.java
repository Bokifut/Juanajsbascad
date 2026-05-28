package Cola;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class IteradorColaTest {

    @Test
    void hasNext() {
        Cola<Integer> cola = new Cola<>();
        IteradorCola<Integer> it = (IteradorCola<Integer>) cola.iterator();
        
        assertFalse(it.hasNext(), "Un iterador de una cola vacía no debe tener siguiente.");
        
        cola.encolar(1);
        it = (IteradorCola<Integer>) cola.iterator();
        assertTrue(it.hasNext(), "El iterador debe tener siguiente tras encolar un elemento.");
    }

    @Test
    void next() {
        Cola<String> cola = new Cola<>();
        cola.encolar("Primero");
        cola.encolar("Segundo");
        
        IteradorCola<String> it = (IteradorCola<String>) cola.iterator();
        
        assertEquals("Primero", it.next());
        assertTrue(it.hasNext());
        assertEquals("Segundo", it.next());
        assertFalse(it.hasNext());
        
        assertThrows(NoSuchElementException.class, it::next, "Debe lanzar excepción si no hay más elementos.");
    }
}
