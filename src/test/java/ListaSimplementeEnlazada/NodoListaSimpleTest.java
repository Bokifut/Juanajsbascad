package ListaSimplementeEnlazada;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodoListaSimpleTest {

    @Test
    void getDato() {
        NodoListaSimple<Integer> nodo = new NodoListaSimple<>(10);
        assertEquals(10, nodo.getDato());
    }

    @Test
    void setDato() {
        NodoListaSimple<Integer> nodo = new NodoListaSimple<>(10);
        nodo.setDato(20);
        assertEquals(20, nodo.getDato());
    }

    @Test
    void getSiguiente() {
        NodoListaSimple<Integer> nodo1 = new NodoListaSimple<>(10);
        NodoListaSimple<Integer> nodo2 = new NodoListaSimple<>(20);
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
    }

    @Test
    void setSiguiente() {
        NodoListaSimple<Integer> nodo1 = new NodoListaSimple<>(10);
        NodoListaSimple<Integer> nodo2 = new NodoListaSimple<>(20);
        nodo1.setSiguiente(nodo2);
        assertNotNull(nodo1.getSiguiente());
        assertEquals(20, nodo1.getSiguiente().getDato());
    }
}
