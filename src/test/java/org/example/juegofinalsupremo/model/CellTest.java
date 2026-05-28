package org.example.juegofinalsupremo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void testConstructorAndGetters() {
        Position pos = new Position(1, 1);
        Cell cell = new Cell(pos);

        assertEquals(pos, cell.getPosition());
        assertFalse(cell.isWall());
        assertFalse(cell.isDoor());
        assertFalse(cell.isOpen());
        assertNull(cell.getTargetRoomId());
        assertNull(cell.getTargetPosition());
        assertNull(cell.getRequiredKeyId());
        assertFalse(cell.hasRoomTransition());
        assertFalse(cell.hasTrap());
        assertEquals(0, cell.getTrapDamage());
        assertNull(cell.getObject());
        assertNull(cell.getEnemy());
        assertFalse(cell.hasBlacksmith());
        assertTrue(cell.isWalkable());
        assertTrue(cell.isEmpty());
    }

    @Test
    void testWall() {
        Cell cell = new Cell(new Position(0, 0));

        cell.setWall(true);
        assertTrue(cell.isWall());
        assertFalse(cell.isWalkable());
        assertFalse(cell.isEmpty());

        cell.setWall(false);
        assertFalse(cell.isWall());
        assertTrue(cell.isWalkable());
        assertTrue(cell.isEmpty());
    }

    @Test
    void testDoorBasicAndClear() {
        Cell cell = new Cell(new Position(0, 0));

        cell.setDoor(true, false);
        assertTrue(cell.isDoor());
        assertFalse(cell.isOpen());
        assertNull(cell.getTargetRoomId());
        assertNull(cell.getTargetPosition());

        cell.setRequiredKeyId("key_1");
        assertEquals("key_1", cell.getRequiredKeyId());

        cell.openDoor();
        assertTrue(cell.isOpen());
        assertNull(cell.getRequiredKeyId());

        cell.clearDoor();
        assertFalse(cell.isDoor());
        assertFalse(cell.isOpen());
        assertNull(cell.getTargetRoomId());
        assertNull(cell.getTargetPosition());
        assertNull(cell.getRequiredKeyId());

        cell.openDoor();
        assertFalse(cell.isOpen());
    }

    @Test
    void testDoorAdvancedTransitions() {
        Position targetPos = new Position(2, 2);
        Cell cell = new Cell(new Position(0, 0));

        assertFalse(cell.hasRoomTransition());

        cell.setDoor(true, true);
        assertFalse(cell.hasRoomTransition());

        cell.setDoor(true, true, "room_2", null);
        assertFalse(cell.hasRoomTransition());
        assertEquals("room_2", cell.getTargetRoomId());

        cell.setDoor(true, true, "room_2", targetPos);
        assertTrue(cell.hasRoomTransition());
        assertEquals("room_2", cell.getTargetRoomId());
        assertEquals(targetPos, cell.getTargetPosition());

        cell.setDoor(true, false, "room_2", targetPos);
        assertFalse(cell.hasRoomTransition());

        cell.setDoor(false, true, "room_2", targetPos);
        assertFalse(cell.hasRoomTransition());
    }

    @Test
    void testTrap() {
        Cell cell = new Cell(new Position(0, 0));

        cell.setTrapDamage(-10);
        assertEquals(0, cell.getTrapDamage());
        assertFalse(cell.hasTrap());

        cell.setTrapDamage(15);
        assertTrue(cell.hasTrap());
        assertEquals(15, cell.getTrapDamage());
        assertFalse(cell.isEmpty());

        assertEquals(15, cell.consumeTrap());
        assertEquals(0, cell.getTrapDamage());
        assertFalse(cell.hasTrap());
    }

    @Test
    void testObject() {
        Cell cell = new Cell(new Position(0, 0));
        GameObject obj1 = new GameObject();
        GameObject obj2 = new GameObject();

        cell.setObject(null);
        assertNull(cell.getObject());

        cell.setObject(obj1);
        assertEquals(obj1, cell.getObject());
        assertFalse(cell.isEmpty());

        assertThrows(IllegalStateException.class, () -> cell.setObject(obj2));

        assertEquals(obj1, cell.takeObject());
        assertNull(cell.getObject());
        assertTrue(cell.isEmpty());

        cell.setWall(true);
        assertThrows(IllegalStateException.class, () -> cell.setObject(obj1));
        cell.setWall(false);

        cell.setDoor(true, false);
        assertThrows(IllegalStateException.class, () -> cell.setObject(obj1));
        cell.clearDoor();

        cell.setTrapDamage(5);
        assertThrows(IllegalStateException.class, () -> cell.setObject(obj1));
        cell.consumeTrap();

        cell.setBlacksmith(true);
        assertThrows(IllegalStateException.class, () -> cell.setObject(obj1));
        cell.setBlacksmith(false);
    }

    @Test
    void testEnemy() {
        Cell cell = new Cell(new Position(0, 0));
        Enemy enemy1 = new Enemy();
        Enemy enemy2 = new Enemy();

        cell.setEnemy(null);
        assertNull(cell.getEnemy());

        cell.setEnemy(enemy1);
        assertEquals(enemy1, cell.getEnemy());
        assertFalse(cell.isEmpty());
        assertFalse(cell.isWalkable());

        assertThrows(IllegalStateException.class, () -> cell.setEnemy(enemy2));

        cell.clearEnemy();
        assertNull(cell.getEnemy());
        assertTrue(cell.isEmpty());

        cell.setWall(true);
        assertThrows(IllegalStateException.class, () -> cell.setEnemy(enemy1));
        cell.setWall(false);

        cell.setDoor(true, false);
        assertThrows(IllegalStateException.class, () -> cell.setEnemy(enemy1));
        cell.clearDoor();

        cell.setBlacksmith(true);
        assertThrows(IllegalStateException.class, () -> cell.setEnemy(enemy1));
        cell.setBlacksmith(false);
    }

    @Test
    void testBlacksmith() {
        Cell cell = new Cell(new Position(0, 0));

        cell.setBlacksmith(true);
        assertTrue(cell.hasBlacksmith());
        assertFalse(cell.isEmpty());
        assertFalse(cell.isWalkable());

        cell.setBlacksmith(false);
        assertFalse(cell.hasBlacksmith());
        assertTrue(cell.isEmpty());

        cell.setWall(true);
        assertThrows(IllegalStateException.class, () -> cell.setBlacksmith(true));
        cell.setWall(false);
    }

    @Test
    void testEnsureNoContentExhaustive() {
        Cell cell1 = new Cell(new Position(0, 0));
        cell1.setObject(new GameObject());
        assertThrows(IllegalStateException.class, () -> cell1.setDoor(true, false));
        assertThrows(IllegalStateException.class, () -> cell1.setTrapDamage(10));

        Cell cell2 = new Cell(new Position(0, 0));
        cell2.setEnemy(new Enemy());
        assertThrows(IllegalStateException.class, () -> cell2.setDoor(true, false));
        assertThrows(IllegalStateException.class, () -> cell2.setTrapDamage(10));

        Cell cell3 = new Cell(new Position(0, 0));
        cell3.setBlacksmith(true);
        assertThrows(IllegalStateException.class, () -> cell3.setDoor(true, false));
        assertThrows(IllegalStateException.class, () -> cell3.setTrapDamage(10));
    }

    @Test
    void testWalkableBranches() {
        Cell cell = new Cell(new Position(0, 0));

        assertTrue(cell.isWalkable());

        cell.setWall(true);
        assertFalse(cell.isWalkable());
        cell.setWall(false);

        cell.setDoor(true, false);
        assertFalse(cell.isWalkable());

        cell.openDoor();
        assertTrue(cell.isWalkable());
        cell.clearDoor();

        cell.setEnemy(new Enemy());
        assertFalse(cell.isWalkable());
        cell.clearEnemy();

        cell.setBlacksmith(true);
        assertFalse(cell.isWalkable());
        cell.setBlacksmith(false);
    }

    @Test
    void testIsEmptyBranches() {
        Cell cell = new Cell(new Position(0, 0));
        assertTrue(cell.isEmpty());

        cell.setWall(true);
        assertFalse(cell.isEmpty());
        cell.setWall(false);

        cell.setDoor(true, false);
        assertFalse(cell.isEmpty());
        cell.clearDoor();

        cell.setTrapDamage(5);
        assertFalse(cell.isEmpty());
        cell.consumeTrap();

        cell.setObject(new GameObject());
        assertFalse(cell.isEmpty());
        cell.takeObject();

        cell.setEnemy(new Enemy());
        assertFalse(cell.isEmpty());
        cell.clearEnemy();

        cell.setBlacksmith(true);
        assertFalse(cell.isEmpty());
        cell.setBlacksmith(false);

        assertTrue(cell.isEmpty());
    }
}