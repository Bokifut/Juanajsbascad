# Juego Final Supremo - Documento de diseno

## Objetivo

Juego de exploracion por habitaciones con matriz de celdas. El jugador se mueve por el mapa, interactua con celdas contiguas, recoge objetos, usa inventario, abre puertas, activa trampas y combate enemigos.

## Decisiones principales

- La logica del juego esta separada de JavaFX en el paquete `model`.
- Las estructuras evaluables estan implementadas en `data`: lista enlazada, cola, pila y grafo de rejilla con BFS.
- El mapa se modela como `Room`, una matriz de `Cell`.
- Una celda solo puede contener una entidad relevante: pared, puerta, trampa, objeto o enemigo.
- El jugador no se guarda dentro de la celda; su posicion se valida contra la habitacion.
- Las acciones sobre objetos, enemigos y puertas se hacen desde una celda contigua.
- Las trampas se activan al entrar en su celda y despues desaparecen.
- La vida se normaliza con `Math.max(0, vida)`.
- JSON se gestiona con `GameJsonRepository` sin librerias externas para dejar visible la serializacion.

## Pantalla

La ventana se divide en cuatro zonas:

- Mapa: matriz de botones con simbolos.
- Estado: vida, ataque, movimiento, posicion y direccion elegida.
- Acciones: mover, atacar, recoger, abrir puerta, usar objeto y guardar JSON.
- Mensajes: log completo de la partida.

## Simbolos del mapa

- `J`: jugador.
- `#`: pared.
- `D`: puerta cerrada.
- `/`: puerta abierta.
- `E`: enemigo.
- `O`: objeto.
- `T`: trampa.
- `.`: celda libre.

## Estructuras de datos

- `MyList`: inventario, log y resultados de busqueda.
- `MyQueue`: turnos y BFS.
- `MyStack`: disponible para deshacer acciones como ampliacion.
- `GridGraph`: grafo implicito de la matriz. Usa BFS para calcular celdas alcanzables.

## Invariantes

- El jugador siempre tiene una `Position` valida dentro de la `Room`.
- Una `Cell` no admite multiples entidades.
- El inventario comprueba identificadores para evitar duplicar el mismo objeto fisico.
- La vida del jugador y enemigos nunca baja de 0.

## Critica del proyecto

La base actual es correcta para una entrega inicial, pero todavia es pequena. Falta un sistema completo de varias habitaciones conectadas por grafo general, IA enemiga avanzada, Dijkstra con costes variables, exportacion PDF automatica y una interfaz mas rica. El JSON es manual y suficiente para el formato de ejemplo, pero seria mas robusto con un parser propio mas formal o una libreria si la asignatura lo permite.
