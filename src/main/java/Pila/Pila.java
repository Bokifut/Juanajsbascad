package Pila;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Implementacion enlazada de una pila.
// Sigue la norma LIFO: el ultimo en entrar es el primero en salir.
public class Pila<T> implements IPila<T>, Iterable<T> {
    private NodoPila<T> tope;
    private int tamano;

    @Override
    public void apilar(T dato) {
        // Cada elemento nuevo se coloca sobre el tope actual.
        NodoPila<T> nuevo = new NodoPila<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamano++;
    }

    @Override
    public T desapilar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila esta vacia.");
        }

        // Siempre se elimina el elemento que esta arriba.
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamano--;
        return dato;
    }

    @Override
    public T cima() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila esta vacia.");
        }

        // Consulta del tope sin modificar la pila.
        return tope.getDato();
    }

    @Override
    public int size() {
        return tamano;
    }

    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    @Override
    public void limpiar() {
        // Al perder el tope, la pila queda vacia.
        tope = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorPila<T>(this.tope);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoPila<T> actual = tope;

        while (actual != null) {
            texto.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != null) {
                texto.append(" | ");
            }
        }

        texto.append("]");
        return texto.toString();
    }
}
