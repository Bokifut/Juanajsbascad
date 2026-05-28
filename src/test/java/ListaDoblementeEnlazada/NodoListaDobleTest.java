package ListaDoblementeEnlazada;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodoListaDobleTest {

    @Test
    void getDato() {
        NodoListaDoble<String> nodo = new NodoListaDoble<>("Test");
        assertEquals("Test", nodo.getDato());
    }

    @Test
    void setDato() {
        NodoListaDoble<Integer> nodo = new NodoListaDoble<>(10);
        nodo.setDato(20);
        assertEquals(20, nodo.getDato());
    }

    @Test
    void getSiguiente() {
        NodoListaDoble<Integer> nodo1 = new NodoListaDoble<>(1);
        NodoListaDoble<Integer> nodo2 = new NodoListaDoble<>(2);
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
    }

    @Test
    void setSiguiente() {
        NodoListaDoble<Integer> nodo1 = new NodoListaDoble<>(1);
        NodoListaDoble<Integer> nodo2 = new NodoListaDoble<>(2);
        nodo1.setSiguiente(nodo2);
        assertEquals(nodo2, nodo1.getSiguiente());
        nodo1.setSiguiente(null);
        assertNull(nodo1.getSiguiente());
    }

    @Test
    void getAnterior() {
        NodoListaDoble<Integer> nodo1 = new NodoListaDoble<>(1);
        NodoListaDoble<Integer> nodo2 = new NodoListaDoble<>(2);
        nodo2.setAnterior(nodo1);
        assertEquals(nodo1, nodo2.getAnterior());
    }

    @Test
    void setAnterior() {
        NodoListaDoble<Integer> nodo1 = new NodoListaDoble<>(1);
        NodoListaDoble<Integer> nodo2 = new NodoListaDoble<>(2);
        nodo2.setAnterior(nodo1);
        assertEquals(nodo1, nodo2.getAnterior());
        nodo2.setAnterior(null);
        assertNull(nodo2.getAnterior());
    }
}
