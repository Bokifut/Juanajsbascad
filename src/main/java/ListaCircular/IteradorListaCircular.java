package ListaCircular;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Recorre la lista una sola vuelta completa.
public class IteradorListaCircular<T> implements Iterator<T> {
    private final NodoListaCircular<T> inicio;
    private NodoListaCircular<T> actual;
    private boolean primeraVuelta;

    public IteradorListaCircular(NodoListaCircular<T> inicio) {
        this.inicio = inicio;
        this.actual = inicio;
        this.primeraVuelta = true;
    }

    @Override
    public boolean hasNext() {
        // Se puede seguir mientras no volvamos al nodo inicial.
        return actual != null && (primeraVuelta || actual != inicio);
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay mas elementos en la lista circular.");
        }

        // Avanzamos respetando el recorrido circular.
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        primeraVuelta = false;
        return dato;
    }
}
