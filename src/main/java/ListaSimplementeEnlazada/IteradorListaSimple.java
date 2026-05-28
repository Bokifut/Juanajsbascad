package ListaSimplementeEnlazada;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Iterador para recorrer la lista desde la cabeza.
public class IteradorListaSimple<T> implements Iterator<T> {
    private NodoListaSimple<T> actual;

    public IteradorListaSimple(NodoListaSimple<T> inicio) {
        this.actual = inicio;
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay mas elementos en la lista.");
        }

        // Devuelve el nodo actual y pasa al siguiente.
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        return dato;
    }
}
