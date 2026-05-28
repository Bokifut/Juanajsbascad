package ListaSimplementeEnlazada;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class IteradorListaSimpleTest {

    @Test
    void hasNext() {
        // Caso: Lista vacía
        IteradorListaSimple<Integer> iteradorVacio = new IteradorListaSimple<>(null);
        assertFalse(iteradorVacio.hasNext());

        // Caso: Lista con elementos
        NodoListaSimple<Integer> nodo = new NodoListaSimple<>(10);
        IteradorListaSimple<Integer> iteradorConDatos = new IteradorListaSimple<>(nodo);
        assertTrue(iteradorConDatos.hasNext());
    }

    @Test
    void next() {
        // Configuración de nodos: 10 -> 20
        NodoListaSimple<Integer> nodo2 = new NodoListaSimple<>(20);
        NodoListaSimple<Integer> nodo1 = new NodoListaSimple<>(10);
        nodo1.setSiguiente(nodo2);

        IteradorListaSimple<Integer> iterador = new IteradorListaSimple<>(nodo1);

        // Primer elemento
        assertEquals(10, iterador.next());
        assertTrue(iterador.hasNext());

        // Segundo elemento
        assertEquals(20, iterador.next());
        assertFalse(iterador.hasNext());

        // Caso: No hay más elementos
        assertThrows(NoSuchElementException.class, () -> {
            iterador.next();
        });
    }
}
