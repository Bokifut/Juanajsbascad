package ListaSimplementeEnlazada;

// Interfaz con las operaciones principales de una lista simple.
public interface IListaSimple<T> {
    void insertarAlInicio(T dato);
    void insertarAlFinal(T dato);
    boolean eliminar(T dato);
    boolean contiene(T dato);
    int size();
    boolean estaVacia();
    void limpiar();
}
