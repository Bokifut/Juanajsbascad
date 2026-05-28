package Cola;

// Interfaz con las operaciones basicas de una cola.
public interface ICola<T> {
    void encolar(T dato);
    T desencolar();
    T frente();
    int size();
    boolean estaVacia();
    void limpiar();
}
