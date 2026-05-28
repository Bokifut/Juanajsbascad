# Diario de uso de IA

## Agente

- Codex / ChatGPT en entorno local del proyecto.

## Skills

- No se uso una skill especializada. La tarea fue implementacion Java/Maven general.

## Prompt principal

El usuario pego el enunciado de la practica y pidio hacer todo lo posible, indicando despues lo no completado, necesidades, partes faltantes, ubicacion y funcion de cada parte.

## Resultado obtenido

- Se transformo el esqueleto JavaFX en un juego funcional.
- Se crearon paquetes de dominio, estructuras, contratos, excepciones, JSON y pruebas.
- Se generaron documentos de diseno, UML, boceto de interfaz y diario de IA.
- Se ejecuto `mvn test` con 7 pruebas correctas.

## Modificaciones principales

- `pom.xml`: Java 21 y Surefire moderno para JUnit 5.
- `HelloApplication`: interfaz JavaFX jugable.
- `data`: estructuras propias.
- `model`: entidades y motor.
- `io`: guardado/carga JSON.
- `docs`: documentacion y UML.

## Analisis critico

La IA acelero la creacion de una base amplia, pero hay que revisar especialmente el JSON manual, la profundidad de pruebas y el cumplimiento exacto de la rubrica de estructuras. La mejora mas importante seria iterar con ejecuciones manuales de interfaz y ampliar casos de prueba antes de la entrega real.
