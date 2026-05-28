package ListaCircular;

import java.util.Iterator;
import java.util.Objects;

// Lista enlazada circular.
// Se guarda el ultimo nodo para localizar tambien el primero con rapidez.
public class ListaCircular<T> implements IListaCircular<T>, Iterable<T> {
    private NodoListaCircular<T> ultimo;
    private int tamano;

    @Override
    public void insertar(T dato) {
        // El nuevo nodo queda antes del primero y pasa a ser el ultimo.
        NodoListaCircular<T> nuevo = new NodoListaCircular<>(dato);

        if (estaVacia()) {
            nuevo.setSiguiente(nuevo);
            ultimo = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        tamano++;
    }

    @Override
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }

        // Recorremos hasta volver al nodo de salida.
        NodoListaCircular<T> actual = ultimo.getSiguiente();
        NodoListaCircular<T> anterior = ultimo;

        do {
            if (Objects.equals(actual.getDato(), dato)) {
                if (actual == ultimo && actual == ultimo.getSiguiente()) {
                    ultimo = null;
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                    if (actual == ultimo) {
                        ultimo = anterior;
                    }
                }

                tamano--;
                return true;
            }

            anterior = actual;
            actual = actual.getSiguiente();
        } while (actual != ultimo.getSiguiente());

        return false;
    }

    @Override
    public boolean contiene(T dato) {
        // Se apoya en el iterador para no repetir logica de recorrido.
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
        // Al quitar la referencia al ultimo, toda la estructura se vacia.
        ultimo = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        if (estaVacia()) {
            return new IteradorListaCircular<T>(null);
        }
        return new IteradorListaCircular<T>(ultimo.getSiguiente());
    }

    @Override
    public String toString() {
        if (estaVacia()) {
            return "[]";
        }

        // Mostramos los nodos hasta regresar al inicio.
        StringBuilder texto = new StringBuilder("[");
        NodoListaCircular<T> actual = ultimo.getSiguiente();

        do {
            texto.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != ultimo.getSiguiente()) {
                texto.append(" -> ");
            }
        } while (actual != ultimo.getSiguiente());

        texto.append("] (circular)");
        return texto.toString();
    }
}
