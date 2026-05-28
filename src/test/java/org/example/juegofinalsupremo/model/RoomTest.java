package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testConstructorsAndGetters() {
        Room r1 = new Room(5, 10);
        assertEquals("room", r1.getId());
        assertEquals("Habitacion", r1.getName());
        assertEquals(5, r1.getRows());
        assertEquals(10, r1.getColumns());

        Room r2 = new Room(3, 4, "Cocina");
        assertEquals("room", r2.getId());
        assertEquals("Cocina", r2.getName());
        assertEquals(3, r2.getRows());
        assertEquals(4, r2.getColumns());

        Room r3 = new Room("custom_id", "Sala", 2, 2);
        assertEquals("custom_id", r3.getId());
        assertEquals("Sala", r3.getName());
        assertEquals(2, r3.getRows());
        assertEquals(2, r3.getColumns());
    }

    @Test
    void testConstructorExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new Room(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Room(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new Room(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> new Room(5, -1));
        assertThrows(IllegalArgumentException.class, () -> new Room("id", "name", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Room("id", "name", -2, -2));
    }

    @Test
    void testIsValid() {
        Room room = new Room(5, 5);

        assertTrue(room.isValid(new Position(0, 0)));
        assertTrue(room.isValid(new Position(2, 3)));
        assertTrue(room.isValid(new Position(4, 4)));

        assertFalse(room.isValid(null));

        assertFalse(room.isValid(new Position(-1, 2)));
        assertFalse(room.isValid(new Position(2, -1)));

        assertFalse(room.isValid(new Position(5, 2)));
        assertFalse(room.isValid(new Position(2, 5)));
        assertFalse(room.isValid(new Position(5, 5)));
    }

    @Test
    void testGetCell() {
        Room room = new Room(3, 3);

        Cell cell = room.getCell(new Position(1, 2));
        assertNotNull(cell);
        assertEquals(1, cell.getPosition().getRow());
        assertEquals(2, cell.getPosition().getColumn());

        assertThrows(IllegalArgumentException.class, () -> room.getCell(null));
        assertThrows(IllegalArgumentException.class, () -> room.getCell(new Position(-1, 0)));
        assertThrows(IllegalArgumentException.class, () -> room.getCell(new Position(0, 3)));
        assertThrows(IllegalArgumentException.class, () -> room.getCell(new Position(3, 0)));
    }
}