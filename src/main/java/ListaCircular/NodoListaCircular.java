package ListaCircular;

// Nodo de la lista circular. El ultimo vuelve a enlazar con el primero.
public class NodoListaCircular<T> {
    private T dato;
    private NodoListaCircular<T> siguiente;

    public NodoListaCircular(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoListaCircular<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoListaCircular<T> siguiente) {
        this.siguiente = siguiente;
    }
}
