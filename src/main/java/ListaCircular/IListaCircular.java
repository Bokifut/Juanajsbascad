package ListaCircular;

// Interfaz con las operaciones principales de una lista circular.
public interface IListaCircular<T> {
    void insertar(T dato);
    boolean eliminar(T dato);
    boolean contiene(T dato);
    int size();
    boolean estaVacia();
    void limpiar();
}
