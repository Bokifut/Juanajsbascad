package ListaSimplementeEnlazada;

import java.util.Iterator;
import java.util.Objects;

// Lista simplemente enlazada.
// Solo se necesita guardar la cabeza para manejar toda la estructura.
public class ListaSimple<T> implements IListaSimple<T>, Iterable<T> {
    private NodoListaSimple<T> cabeza;
    private int tamano;

    @Override
    public void insertarAlInicio(T dato) {
        // El nuevo nodo apunta a la antigua cabeza.
        NodoListaSimple<T> nuevo = new NodoListaSimple<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamano++;
    }

    @Override
    public void insertarAlFinal(T dato) {
        // Si no esta vacia, avanzamos hasta el ultimo nodo.
        NodoListaSimple<T> nuevo = new NodoListaSimple<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
        } else {
            NodoListaSimple<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }

        tamano++;
    }

    @Override
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }

        // Caso especial: borrar la cabeza.
        if (Objects.equals(cabeza.getDato(), dato)) {
            cabeza = cabeza.getSiguiente();
            tamano--;
            return true;
        }

        NodoListaSimple<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (Objects.equals(actual.getSiguiente().getDato(), dato)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamano--;
                return true;
            }
            actual = actual.getSiguiente();
        }

        return false;
    }

    @Override
    public boolean contiene(T dato) {
        // Se recorre la lista comparando cada dato.
        for (T elemento : this) {
            if (Objects.equals(elemento, dato)) {
                return true;
            }
        }
        return false;
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
        // Al perder la cabeza, la lista queda vacia.
        cabeza = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorListaSimple<T>(this.cabeza);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoListaSimple<T> actual = cabeza;

        while (actual != null) {
            texto.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != null) {
                texto.append(" -> ");
            }
        }

        texto.append("]");
        return texto.toString();
    }
}
