package Cola;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Implementacion enlazada de una cola.
// Sigue el comportamiento FIFO: el primero en entrar es el primero en salir.
public class Cola<T> implements ICola<T>, Iterable<T> {
    private NodoCola<T> inicio;
    private NodoCola<T> ultimo;
    private int tamano;

    @Override
    public void encolar(T dato) {
        // Cada nuevo elemento se coloca al final.
        NodoCola<T> nuevo = new NodoCola<>(dato);

        if (estaVacia()) {
            inicio = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        tamano++;
    }

    @Override
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia.");
        }

        // Siempre se elimina el elemento del inicio.
        T dato = inicio.getDato();
        inicio = inicio.getSiguiente();

        if (inicio == null) {
            ultimo = null;
        }

        tamano--;
        return dato;
    }

    @Override
    public T frente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia.");
        }

        // Solo consulta el primer valor, no lo borra.
        return inicio.getDato();
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
        // La cola queda vacia al perder las referencias.
        inicio = null;
        ultimo = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorCola<T>(this.inicio);
    }

    @Override
    public String toString() {
        // Representacion sencilla para ver el contenido por pantalla.
        StringBuilder texto = new StringBuilder("[");
        NodoCola<T> actual = inicio;

        while (actual != null) {
            texto.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != null) {
                texto.append(" <- ");
            }
        }
        texto.append("]");
        return texto.toString();
    }
}
