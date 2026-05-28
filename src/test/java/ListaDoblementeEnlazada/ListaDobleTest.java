package ListaDoblementeEnlazada;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ListaDobleTest {

    private ListaDoble<Integer> lista;

    @BeforeEach
    void setUp() {
        lista = new ListaDoble<>();
    }

    @Test
    void insertarAlInicio() {
        lista.insertarAlInicio(10);
        lista.insertarAlInicio(20);
        assertEquals(2, lista.size());
        assertEquals("[20 <-> 10]", lista.toString());
    }

    @Test
    void insertarAlFinal() {
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        assertEquals(2, lista.size());
        assertEquals("[10 <-> 20]", lista.toString());
    }

    @Test
    void eliminar() {
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);

        assertTrue(lista.eliminar(20), "Debe eliminar un elemento existente.");
        assertEquals(2, lista.size());
        assertFalse(lista.contiene(20));

        assertTrue(lista.eliminar(10), "Debe eliminar la cabeza.");
        assertEquals(1, lista.size());

        assertTrue(lista.eliminar(30), "Debe eliminar la cola.");
        assertTrue(lista.estaVacia());

        assertFalse(lista.eliminar(99), "No debe eliminar un elemento inexistente.");
    }

    @Test
    void contiene() {
        lista.insertarAlFinal(10);
        assertTrue(lista.contiene(10));
        assertFalse(lista.contiene(20));
    }

    @Test
    void size() {
        assertEquals(0, lista.size());
        lista.insertarAlInicio(1);
        assertEquals(1, lista.size());
        lista.eliminar(1);
        assertEquals(0, lista.size());
    }

    @Test
    void estaVacia() {
        assertTrue(lista.estaVacia());
        lista.insertarAlFinal(1);
        assertFalse(lista.estaVacia());
    }

    @Test
    void limpiar() {
        lista.insertarAlFinal(1);
        lista.insertarAlFinal(2);
        lista.limpiar();
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.size());
        assertFalse(lista.iterator().hasNext());
    }

    @Test
    void iterator() {
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        Iterator<Integer> it = lista.iterator();
        assertTrue(it.hasNext());
        assertEquals(10, it.next());
        assertTrue(it.hasNext());
        assertEquals(20, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testToString() {
        assertEquals("[]", lista.toString());
        lista.insertarAlFinal(1);
        assertEquals("[1]", lista.toString());
        lista.insertarAlFinal(2);
        assertEquals("[1 <-> 2]", lista.toString());
    }
}
