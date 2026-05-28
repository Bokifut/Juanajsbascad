package ListaDoblementeEnlazada;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Iterador que avanza de izquierda a derecha en la lista.
public class IteradorListaDoble<T> implements Iterator<T> {
    private NodoListaDoble<T> actual;

    public IteradorListaDoble(NodoListaDoble<T> inicio) {
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

        // Se devuelve el valor actual y luego se avanza.
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        return dato;
    }
}
