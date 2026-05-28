package Pila;

// Interfaz con las operaciones tipicas de una pila.
public interface IPila<T> {
    void apilar(T dato);
    T desapilar();
    T cima();
    int size();
    boolean estaVacia();
    void limpiar();
}
