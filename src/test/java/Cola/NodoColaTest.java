package Cola;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodoColaTest {

    @Test
    void getDato() {
        NodoCola<Integer> nodo = new NodoCola<>(10);
        assertEquals(10, nodo.getDato());
    }

    @Test
    void setDato() {
        NodoCola<String> nodo = new NodoCola<>("Inicio");
        nodo.setDato("Fin");
        assertEquals("Fin", nodo.getDato());
    }

    @Test
    void getSiguiente() {
        NodoCola<Integer> nodo1 = new NodoCola<>(1);
        NodoCola<Integer> nodo2 = new NodoCola<>(2);
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
    }

    @Test
    void setSiguiente() {
        NodoCola<Integer> nodo1 = new NodoCola<>(1);
        NodoCola<Integer> nodo2 = new NodoCola<>(2);
        nodo1.setSiguiente(nodo2);
        assertNotNull(nodo1.getSiguiente());
        assertEquals(2, nodo1.getSiguiente().getDato());
    }
}
