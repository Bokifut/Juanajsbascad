package ListaDoblementeEnlazada;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class IteradorListaDobleTest {

    @Test
    void hasNext() {
        NodoListaDoble<Integer> nodo = new NodoListaDoble<>(10);
        IteradorListaDoble<Integer> it = new IteradorListaDoble<>(nodo);
        
        assertTrue(it.hasNext(), "Debe tener un elemento.");
        it.next();
        assertFalse(it.hasNext(), "No debe tener más elementos.");
        
        IteradorListaDoble<Integer> itVacio = new IteradorListaDoble<>(null);
        assertFalse(itVacio.hasNext(), "Un iterador nulo no debe tener elementos.");
    }

    @Test
    void next() {
        NodoListaDoble<Integer> nodo2 = new NodoListaDoble<>(20);
        NodoListaDoble<Integer> nodo1 = new NodoListaDoble<>(10);
        nodo1.setSiguiente(nodo2);
        
        IteradorListaDoble<Integer> it = new IteradorListaDoble<>(nodo1);
        
        assertEquals(10, it.next(), "El primer elemento debe ser 10.");
        assertEquals(20, it.next(), "El segundo elemento debe ser 20.");
        
        assertThrows(NoSuchElementException.class, () -> {
            it.next();
        }, "Debe lanzar NoSuchElementException al agotar la lista.");
    }
}
