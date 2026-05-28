package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testConstructorAndGetters() {
        Position pos = new Position(5, 10);
        assertEquals(5, pos.getRow());
        assertEquals(10, pos.getColumn());
    }

    @Test
    void testTranslate() {
        Position pos = new Position(5, 5);
        for (Direction dir : Direction.values()) {
            Position translated = pos.translate(dir);
            assertNotNull(translated);
            assertEquals(5 + dir.getDeltaRow(), translated.getRow());
            assertEquals(5 + dir.getDeltaColumn(), translated.getColumn());
        }
    }

    @Test
    void testEquals() {
        Position p1 = new Position(3, 4);
        Position p2 = new Position(3, 4);
        Position p3 = new Position(5, 4);
        Position p4 = new Position(3, 6);

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("String"));
    }

    @Test
    void testHashCode() {
        Position pos1 = new Position(2, 5);
        Position pos2 = new Position(2, 5);
        assertEquals(2 * 31 + 5, pos1.hashCode());
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }

    @Test
    void testToString() {
        Position pos = new Position(7, 8);
        assertEquals("(7,8)", pos.toString());
    }
}