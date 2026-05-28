package ListaDoblementeEnlazada;

// Interfaz de una lista doblemente enlazada.
public interface IListaDoble<T> {
    void insertarAlInicio(T dato);
    void insertarAlFinal(T dato);
    boolean eliminar(T dato);
    boolean contiene(T dato);
    int size();
    boolean estaVacia();
    void limpiar();
}
