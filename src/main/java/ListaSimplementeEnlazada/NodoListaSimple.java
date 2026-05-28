package ListaSimplementeEnlazada;

// Nodo que guarda un dato y la referencia al siguiente.
public class NodoListaSimple<T> {
    private T dato;
    private NodoListaSimple<T> siguiente;

    public NodoListaSimple(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoListaSimple<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoListaSimple<T> siguiente) {
        this.siguiente = siguiente;
    }
}
