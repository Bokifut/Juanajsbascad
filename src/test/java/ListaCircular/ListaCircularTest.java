package ListaCircular;

import org.junit.jupiter.api.Test;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;

class ListaCircularTest {

    @Test
    void insertar() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        lista.insertar(1);
        lista.insertar(2);
        lista.insertar(3);
        
        assertEquals(3, lista.size());
        assertTrue(lista.contiene(1));
        assertTrue(lista.contiene(2));
        assertTrue(lista.contiene(3));
        assertEquals("[1 -> 2 -> 3] (circular)", lista.toString());
    }

    @Test
    void eliminar() {
        ListaCircular<String> lista = new ListaCircular<>();
        lista.insertar("A");
        lista.insertar("B");
        lista.insertar("C");
        assertFalse(lista.eliminar("Z"));

        assertTrue(lista.eliminar("B"));
        assertEquals(2, lista.size());
        assertFalse(lista.contiene("B"));
        
        // Eliminar el último (que actualiza la referencia interna)
        assertTrue(lista.eliminar("C"));
        assertEquals(1, lista.size());
        assertEquals("[A] (circular)", lista.toString());

        // Eliminar el único elemento restante
        assertTrue(lista.eliminar("A"));
        assertTrue(lista.estaVacia());
        assertFalse(lista.eliminar("Z"));
        assertEquals(0, lista.size());
    }

    @Test
    void contiene() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        lista.insertar(10);
        lista.insertar(20);

        assertTrue(lista.contiene(10));
        assertTrue(lista.contiene(20));
        assertFalse(lista.contiene(30));
    }

    @Test
    void size() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        assertEquals(0, lista.size());
        lista.insertar(1);
        assertEquals(1, lista.size());
        lista.eliminar(1);
        assertEquals(0, lista.size());
    }

    @Test
    void estaVacia() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        assertTrue(lista.estaVacia());
        lista.insertar(1);
        assertFalse(lista.estaVacia());
    }

    @Test
    void limpiar() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        lista.insertar(1);
        lista.insertar(2);
        lista.limpiar();
        
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.size());
        assertFalse(lista.contiene(1));
    }

    @Test
    void iterator() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        lista.insertar(1);
        lista.insertar(2);

        Iterator<Integer> it = lista.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertTrue(it.hasNext());
        assertEquals(2, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testToString() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        assertEquals("[]", lista.toString());
        
        lista.insertar(1);
        assertEquals("[1] (circular)", lista.toString());
        
        lista.insertar(2);
        assertEquals("[1 -> 2] (circular)", lista.toString());
    }
}
