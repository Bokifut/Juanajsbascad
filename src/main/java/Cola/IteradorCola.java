package Cola;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Recorre la cola desde el primer elemento hasta el ultimo.
public class IteradorCola<T> implements Iterator<T> {
    private NodoCola<T> actual;

    public IteradorCola(NodoCola<T> inicio) {
        this.actual = inicio;
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay mas elementos en la cola.");
        }

        // Devuelve el dato actual y avanza una posicion.
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        return dato;
    }
}
