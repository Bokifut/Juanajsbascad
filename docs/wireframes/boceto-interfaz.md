# Boceto de interfaz

```text
+------------------------------------------------------+----------------------------+
|                                                      | Estado del jugador         |
|                                                      | Vida: 20                   |
|                   MAPA / MATRIZ                      | Ataque: 5                  |
|                                                      | Movimiento: 3              |
|   [#] [.] [.] [.] [.] [.] [.] [D]                    | Posicion: (4,1)            |
|   [.] [#] [#] [.] [.] [.] [.] [.]                    |                            |
|   [.] [.] [.] [.] [T] [.] [E] [.]                    | Inventario                 |
|   [.] [.] [.] [O] [.] [.] [.] [.]                    | - Pocion                   |
|   [.] [J] [.] [.] [O] [.] [.] [.]                    | - Baston ligero            |
|   [.] [.] [.] [.] [.] [.] [.] [.]                    |                            |
|                                                      | Direccion                  |
|                                                      | [Arriba][Abajo][Izq][Der]  |
|                                                      |                            |
|                                                      | Acciones                   |
|                                                      | [Mover]                    |
|                                                      | [Atacar]                   |
|                                                      | [Recoger]                  |
|                                                      | [Abrir puerta]             |
|                                                      | [Usar objeto]              |
|                                                      | [Guardar JSON]             |
+------------------------------------------------------+----------------------------+
| Log de partida                                                                    |
| 1. Partida iniciada                                                               |
| 2. El jugador se mueve a (3,1)                                                     |
+----------------------------------------------------------------------------------+
```

Flujo: el jugador elige direccion, pulsa una accion y la interfaz actualiza mapa, estado, inventario y log.
