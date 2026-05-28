package ListaDoblementeEnlazada;

// Nodo con referencia al nodo anterior y al siguiente.
public class NodoListaDoble<T> {
    private T dato;
    private NodoListaDoble<T> siguiente;
    private NodoListaDoble<T> anterior;

    public NodoListaDoble(T dato) {
        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoListaDoble<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoListaDoble<T> siguiente) {
        this.siguiente = siguiente;
    }

    public NodoListaDoble<T> getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoListaDoble<T> anterior) {
        this.anterior = anterior;
    }
}
