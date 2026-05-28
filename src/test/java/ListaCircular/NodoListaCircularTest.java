package ListaCircular;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodoListaCircularTest {

    @Test
    void getDato() {
        NodoListaCircular<String> nodo = new NodoListaCircular<>("Test");
        assertEquals("Test", nodo.getDato());
    }

    @Test
    void setDato() {
        NodoListaCircular<Integer> nodo = new NodoListaCircular<>(1);
        nodo.setDato(2);
        assertEquals(2, nodo.getDato());
    }

    @Test
    void getSiguiente() {
        NodoListaCircular<Integer> nodo1 = new NodoListaCircular<>(1);
        NodoListaCircular<Integer> nodo2 = new NodoListaCircular<>(2);
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
    }

    @Test
    void setSiguiente() {
        NodoListaCircular<String> nodo1 = new NodoListaCircular<>("A");
        NodoListaCircular<String> nodo2 = new NodoListaCircular<>("B");
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
        
        nodo1.setSiguiente(null);
        assertNull(nodo1.getSiguiente());
    }
}
