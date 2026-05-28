package Pila;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Recorre la pila desde el tope hasta el fondo.
public class IteradorPila<T> implements Iterator<T> {
    private NodoPila<T> actual;

    public IteradorPila(NodoPila<T> cima) {
        this.actual = cima;
    }

    @Override
    public boolean hasNext() {
        return actual != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay mas elementos en la pila.");
        }

        // Se devuelve el dato actual y se baja al siguiente nodo.
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        return dato;
    }
}
