package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void getDeltaRow() {
        assertEquals(-1, Direction.UP.getDeltaRow());
        assertEquals(1, Direction.DOWN.getDeltaRow());
        assertEquals(0, Direction.LEFT.getDeltaRow());
        assertEquals(0, Direction.RIGHT.getDeltaRow());
    }

    @Test
    void getDeltaColumn() {
        assertEquals(0, Direction.UP.getDeltaColumn());
        assertEquals(0, Direction.DOWN.getDeltaColumn());
        assertEquals(-1, Direction.LEFT.getDeltaColumn());
        assertEquals(1, Direction.RIGHT.getDeltaColumn());
    }

    @Test
    void values() {
        Direction[] directions = Direction.values();
        assertEquals(4, directions.length);
        assertArrayEquals(new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}, directions);
    }

    @Test
    void valueOf() {
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
    }
}
