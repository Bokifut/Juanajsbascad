package org.example.juegofinalsupremo;

import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.example.juegofinalsupremo.exceptions.InvalidActionException;
import org.example.juegofinalsupremo.exceptions.InvalidMoveException;
import org.example.juegofinalsupremo.io.GameJsonRepository;
import org.example.juegofinalsupremo.model.Direction;
import org.example.juegofinalsupremo.model.Position;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExceptionHandlingTest {
    @Test
    void gameExceptionBasePreservesMessage() {
        GameException exception = new GameException("Base");

        assertEquals("Base", exception.getMessage());
    }

    @Test
    void invalidMoveExceptionIsAGameException() {
        InvalidMoveException exception = new InvalidMoveException("Movimiento invalido");

        assertInstanceOf(GameException.class, exception);
        assertEquals("Movimiento invalido", exception.getMessage());
    }

    @Test
    void invalidMoveIsThrownWhenTryingToLeaveTheBoard() {
        TestSupport.TestContext context = TestSupport.context(3, 3, new Position(0, 0));

        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> context.engine.move(Direction.UP));

        assertTrue(exception.getMessage().contains("Movimiento fuera del tablero"));
    }

    @Test
    void invalidActionExceptionIsAGameException() {
        InvalidActionException exception = new InvalidActionException("Accion invalida");

        assertInstanceOf(GameException.class, exception);
        assertEquals("Accion invalida", exception.getMessage());
    }

    @Test
    void invalidActionIsThrownWhenNoEnemyIsPresent() {
        TestSupport.TestContext context = TestSupport.context(3, 3, new Position(1, 1));

        InvalidActionException exception = assertThrows(InvalidActionException.class,
                () -> context.engine.attack(Direction.RIGHT));

        assertTrue(exception.getMessage().contains("No hay enemigo"));
    }

    @Test
    void gameStorageExceptionPreservesCauseAndIsThrownForBrokenFile() {
        GameStorageException exception = new GameStorageException("Error de guardado", new IOException("IO"));
        assertTrue(exception.getCause() instanceof IOException);
        assertEquals("Error de guardado", exception.getMessage());

        GameJsonRepository repository = new GameJsonRepository();
        assertThrows(GameStorageException.class, () -> repository.load("fichero-que-no-existe.json"));
    }
}
