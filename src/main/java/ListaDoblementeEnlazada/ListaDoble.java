package ListaDoblementeEnlazada;

import java.util.Iterator;
import java.util.Objects;

// Lista doblemente enlazada.
// Al guardar cabeza y cola, insertar en ambos extremos es mas comodo.
public class ListaDoble<T> implements IListaDoble<T>, Iterable<T> {
    private NodoListaDoble<T> cabeza;
    private NodoListaDoble<T> cola;
    private int tamano;

    @Override
    public void insertarAlInicio(T dato) {
        // El nuevo nodo pasa a ser la nueva cabeza.
        NodoListaDoble<T> nuevo = new NodoListaDoble<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevo);
            cabeza = nuevo;
        }

        tamano++;
    }

    @Override
    public void insertarAlFinal(T dato) {
        // El nuevo nodo se conecta detras de la cola actual.
        NodoListaDoble<T> nuevo = new NodoListaDoble<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            nuevo.setAnterior(cola);
            cola = nuevo;
        }

        tamano++;
    }

    @Override
    public boolean eliminar(T dato) {
        NodoListaDoble<T> actual = cabeza;

        while (actual != null) {
            if (Objects.equals(actual.getDato(), dato)) {
                // Hay que reconectar el nodo anterior con el siguiente.
                NodoListaDoble<T> anterior = actual.getAnterior();
                NodoListaDoble<T> siguiente = actual.getSiguiente();

                if (anterior == null) {
                    cabeza = siguiente;
                } else {
                    anterior.setSiguiente(siguiente);
                }

                if (siguiente == null) {
                    cola = anterior;
                } else {
                    siguiente.setAnterior(anterior);
                }

                tamano--;
                return true;
            }
            actual = actual.getSiguiente();
        }

        return false;
    }

    @Override
    public boolean contiene(T dato) {
        // Se recorre la estructura hasta encontrar coincidencia.
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
        // Se eliminan las referencias principales.
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorListaDoble<T>(this.cabeza);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoListaDoble<T> actual = cabeza;

        while (actual != null) {
            texto.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != null) {
                texto.append(" <-> ");
            }
        }

        texto.append("]");
        return texto.toString();
    }
}
