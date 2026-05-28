package ListaSimplementeEnlazada;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ListaSimpleTest {

    private ListaSimple<Integer> lista;

    @BeforeEach
    void setUp() {
        lista = new ListaSimple<>();
    }

    @Test
    void insertarAlInicio() {
        lista.insertarAlInicio(10);
        lista.insertarAlInicio(20);
        assertEquals(2, lista.size());
        assertEquals("[20 -> 10]", lista.toString());
    }

    @Test
    void insertarAlFinal() {
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        assertEquals(2, lista.size());
        assertEquals("[10 -> 20]", lista.toString());
    }

    @Test
    void eliminar() {
        ListaSimple<String> lista = new ListaSimple<>();

        // 1. CUBRE EL PRIMER IF: if (estaVacia()) return false;
        assertFalse(lista.eliminar("Z"));

        // Insertamos elementos para tener una lista: Cabeza(A) -> B -> C -> null
        lista.insertarAlFinal("A");
        lista.insertarAlFinal("B");
        lista.insertarAlFinal("C");

        // 2. CUBRE EL CASO ESPECIAL DE LA CABEZA:
        assertTrue(lista.eliminar("A"));
        assertEquals(2, lista.size()); // La lista ahora es: Cabeza(B) -> C -> null

        // 3. CUBRE EL AVANCE DEL BUCLE Y EL ÚLTIMO RETURN FALSE:
        assertFalse(lista.eliminar("X"));

        // 4. CUBRE LA ELIMINACIÓN EN EL MEDIO/FINAL DEL BUCLE:
        assertTrue(lista.eliminar("C"));
        assertEquals(1, lista.size()); // Solo queda "B"

        assertTrue(lista.eliminar("B"));
        assertTrue(lista.estaVacia());
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
    }

    @Test
    void estaVacia() {
        assertTrue(lista.estaVacia());
        lista.insertarAlInicio(1);
        assertFalse(lista.estaVacia());
    }

    @Test
    void limpiar() {
        lista.insertarAlFinal(1);
        lista.insertarAlFinal(2);
        lista.limpiar();
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.size());
        assertEquals("[]", lista.toString());
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
        lista.insertarAlFinal(2);
        assertEquals("[1 -> 2]", lista.toString());
    }
}
