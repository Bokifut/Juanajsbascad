package ListaCircular;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class IteradorListaCircularTest {

    @Test
    void hasNext() {
        NodoListaCircular<Integer> nodo1 = new NodoListaCircular<>(1);
        IteradorListaCircular<Integer> itVacio = new IteradorListaCircular<>(null);
        assertFalse(itVacio.hasNext(), "Un iterador con inicio nulo no debe tener siguiente.");

        nodo1.setSiguiente(nodo1);
        IteradorListaCircular<Integer> itUnico = new IteradorListaCircular<>(nodo1);
        assertTrue(itUnico.hasNext(), "Debe tener un elemento al empezar.");
        itUnico.next();
        assertFalse(itUnico.hasNext(), "No debe tener mas elementos tras dar la vuelta completa (1 nodo).");
    }

    @Test
    void next() {
        NodoListaCircular<Integer> n1 = new NodoListaCircular<>(10);
        NodoListaCircular<Integer> n2 = new NodoListaCircular<>(20);
        n1.setSiguiente(n2);
        n2.setSiguiente(n1);

        IteradorListaCircular<Integer> it = new IteradorListaCircular<>(n1);
        
        assertTrue(it.hasNext());
        assertEquals(10, it.next());
        assertTrue(it.hasNext());
        assertEquals(20, it.next());
        assertFalse(it.hasNext());
        
        assertThrows(NoSuchElementException.class, it::next, "Debe lanzar excepcion al agotar la vuelta.");
    }
}
